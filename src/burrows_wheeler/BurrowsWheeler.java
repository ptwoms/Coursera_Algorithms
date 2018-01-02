import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

/**
 * BurrowsWheeler.java
 * 
 * @author Pyae Phyo Myint Soe
 * created on 19/12/17.
 */

public class BurrowsWheeler {
	private static final int EXTENDED_ASCII_SIZE = 256;

	// apply Burrows-Wheeler encoding, reading from standard input and writing to standard output
	public static void transform() {
		StringBuilder strBuilder = new StringBuilder();
		while (!BinaryStdIn.isEmpty()) {
			strBuilder.append(BinaryStdIn.readChar());
		}
		String str = strBuilder.toString();
		CircularSuffixArray suffixArr = new CircularSuffixArray(str);
		int suffixArrLength = suffixArr.length();
//		BinaryStdOut.write(suffixArr.getIndexForFirstCharacter());
//		BinaryStdOut.flush();
		
		for(int i = 0; i < suffixArrLength; i++) {
			int curIndex =suffixArr.index(i);
			if (curIndex == 0) {
				BinaryStdOut.write(i);
				break;
			}
		}

		for(int i = 0; i < suffixArrLength; i++) {
			int curIndex =suffixArr.index(i);
			int finalIndex = curIndex-1;
			if(finalIndex < 0) {
				finalIndex = str.length() - 1;
			}
			byte curChar = (byte) str.charAt(finalIndex);
			BinaryStdOut.write(curChar);
		}
		BinaryStdOut.flush();
	}

	// apply Burrows-Wheeler decoding, reading from standard input and writing to standard output
	public static void inverseTransform() {
		int nextStart = BinaryStdIn.readInt();
		List<Integer> tValue = new ArrayList<>();
		while(!BinaryStdIn.isEmpty()) {
			tValue.add((int) BinaryStdIn.readChar());
		}
		int[] tArray = new int[tValue.size()];
		int[] nextArr = new int[tValue.size()];
		int i = 0;
		int[] sortIndexes = new int[EXTENDED_ASCII_SIZE];
		Arrays.fill(sortIndexes, 0);
		for(Integer curValue: tValue) {
			tArray[i] = curValue;
			sortIndexes[curValue]++;
			i++;
		}
		int curMax = 0;
		int curValue = 0;
		for (int j = 0; j < sortIndexes.length; j++) {
			curValue = sortIndexes[j];
			sortIndexes[j] = curMax;
			curMax += curValue;
		}
		int strLength = tArray.length;
		for(int j = 0; j < strLength; j++) {
			curValue = tArray[j];
			nextArr[sortIndexes[curValue]] = j;
			sortIndexes[curValue]++;
		}
		Arrays.sort(tArray);
		curValue = nextStart;
		for (int j = 0; j < strLength; j++) {
			BinaryStdOut.write((char)tArray[curValue]);
			curValue = nextArr[curValue];
		}
		BinaryStdOut.flush();
	}

	// if args[0] is '-', apply Burrows-Wheeler encoding
	// if args[0] is '+', apply Burrows-Wheeler decoding
	public static void main(String[] args) {
		if(args.length == 0) {
			return;
		}
		if (args[0].equals("-")) {
			BurrowsWheeler.transform();
		}else if(args[0].equals("+")) {
			BurrowsWheeler.inverseTransform();
		}
	}
}
