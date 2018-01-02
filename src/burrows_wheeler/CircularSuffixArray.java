import java.util.Arrays;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Stack;

/**
 * CircularSuffixArray.java
 * 
 * @author Pyae Phyo Myint Soe
 * created on 19/12/17.
 */

public class CircularSuffixArray {
	private int strLength;
	private int[] suffixes;
	private char[] strArr;
	private final int INSERTION_SORT_CUTOFF = 15; 
	
	private static final int EXTENDED_ASCII_SIZE = 256;
	
	private class SuffixIndex {
		public int origIndex;
		public int sortIndex;
		
		public SuffixIndex(int index) {
			this.origIndex = index;
			this.sortIndex = 0;
		}		
	}
	
	private class UnSortedIndex{
		public int depth, low, high;
		public UnSortedIndex(int low, int high, int depth) {
			this.depth = depth;
			this.low = low;
			this.high = high;
		}
	}
	
	private void doInsertionSort(SuffixIndex[] indexes, UnSortedIndex curIndex) {
		SuffixIndex curSIndex, prevSIndex;
		char curChar, prevChar;
		boolean doneForOne;
		SuffixIndex tempIndex;
		for(int i = curIndex.low, j, strIndex; i <= curIndex.high; i++) {
			doneForOne = false;
			for(j = i; j > curIndex.low; j--) {
				curSIndex = indexes[j];
				prevSIndex = indexes[j-1];
				for (strIndex = curIndex.depth; strIndex < strLength; strIndex++) {
					curChar = strArr[(strIndex + curSIndex.origIndex)%strLength];
					prevChar = strArr[(strIndex + prevSIndex.origIndex)%strLength]; 
					if(curChar < prevChar) {
						tempIndex = indexes[j];
						indexes[j] = indexes[j-1];
						indexes[j-1] = tempIndex;
						break;
					}else if(curChar > prevChar) {
						doneForOne = true;
						break;
					}
				}
				if(doneForOne) {
					break;
				}
			}
		}
	}
	
	private void sort(SuffixIndex[] indexes, UnSortedIndex curUSortedIndex, SuffixIndex[] authArr, Stack<UnSortedIndex> traceStack, int[] counter, int[] incCounter) {
		Arrays.fill(counter, 0);
		Arrays.fill(incCounter, 0);
		
		for(int i = curUSortedIndex.low; i <= curUSortedIndex.high; i++) {
			SuffixIndex curSIndex = indexes[i];
			counter[strArr[(curSIndex.origIndex + curUSortedIndex.depth)%strLength]]++;
		}
		
		//transform to indexes
		int curValue = 0, tempValue;
		for(int i = 0; i < EXTENDED_ASCII_SIZE; i++) {
			tempValue = counter[i];
			counter[i] = curValue;
			incCounter[i] = curValue;
			curValue += tempValue;
		}
		
		//redistribute and copy
		char curChar;
		SuffixIndex curSIndex;
		for (int i = curUSortedIndex.low; i <= curUSortedIndex.high; i++) {
			curSIndex = indexes[i];
			curChar = strArr[(curSIndex.origIndex + curUSortedIndex.depth)%strLength];
			curSIndex.sortIndex += counter[curChar];
			authArr[incCounter[curChar]] = curSIndex;
			incCounter[curChar]++;
		}
		
		int prevSortIndex = -1, curSortIndex;
		int overlapCount = 0;
		for(int i = curUSortedIndex.low; i <= curUSortedIndex.high; i++) {
			curSIndex = authArr[i-curUSortedIndex.low];
			indexes[i] = curSIndex;
			curSortIndex = indexes[i].sortIndex;
			if(curSortIndex == prevSortIndex) {
				overlapCount++;
			}else {
				addUnsortedIfNeeded(indexes, overlapCount, i-1, curUSortedIndex.depth, traceStack);
				overlapCount = 1;
				prevSortIndex = curSortIndex;
			}
		}
		addUnsortedIfNeeded(indexes, overlapCount, curUSortedIndex.high, curUSortedIndex.depth, traceStack);
	}
	
	private void addUnsortedIfNeeded(SuffixIndex[] indexes, int overlapCount, int lastIndex, int curDepth, Stack<UnSortedIndex> tracer) {
		if(overlapCount > 1 && curDepth < strLength - 2) {
			UnSortedIndex newPartition = new UnSortedIndex(lastIndex - (overlapCount - 1), lastIndex, curDepth+1);
			if (overlapCount <= INSERTION_SORT_CUTOFF) {
				doInsertionSort(indexes, newPartition);
			}else {
				tracer.push(newPartition);
			}
		}
	}
	
	private void doMSDCountSort(SuffixIndex[] indexes) {
		int indexLength = indexes.length;		
		UnSortedIndex unsorted = new UnSortedIndex(0, indexLength-1, 0);
		if(indexLength <= INSERTION_SORT_CUTOFF) {
			doInsertionSort(indexes, unsorted);
		}else {
			int[] counter = new int[EXTENDED_ASCII_SIZE];
			int[] incCounter = new int[EXTENDED_ASCII_SIZE];

			SuffixIndex[] authArr = new SuffixIndex[indexLength];
			Stack<UnSortedIndex> traceStack = new Stack<>();
			traceStack.push(unsorted);
			while (!traceStack.isEmpty()) {
				UnSortedIndex curIndex = traceStack.pop();
				sort(indexes, curIndex, authArr, traceStack, counter, incCounter); 
			}
		}
	}
	
    // circular suffix array of s
	public CircularSuffixArray(String s) {
		if(s == null) {
			throw new IllegalArgumentException("you must provide valid string.");
		}
		strLength = s.length();
		strArr = s.toCharArray();
		SuffixIndex[] suffixArray = new SuffixIndex[strLength];
//		//initial population
		if(strLength > 0) {
			for(int i = 0; i < strLength; i++) {
				suffixArray[i] = new SuffixIndex(i);
			}
			
			doMSDCountSort(suffixArray);
					
			//assign
			int index = 0;
			suffixes = new int[strLength];
			for(SuffixIndex curSuffix: suffixArray) {
				suffixes[index++] = curSuffix.origIndex;
			}
		}
	}
	
    // length of s
	public int length() { 
		return strLength;
	}

	public int index(int i) {
		if (i < 0 || i >= strLength) {
			throw new IllegalArgumentException("Index out of bound");
		}
		return suffixes[i];
	}

	public static void main(String[] args) {
		String fileName = args[0];
		In file = new In(fileName);
		CircularSuffixArray cSfx = new CircularSuffixArray(file.readAll());
		for (int i = 0; i < cSfx.length(); i++) {
			System.out.println("" + cSfx.index(i));
		}

	}

}
