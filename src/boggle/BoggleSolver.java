import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.StdOut;

/**
 * BoggleSolver.java
 * 
 * @author Pyae Phyo Myint Soe
 * 
 */

//unoptimized version
public class BoggleSolver {
	
	private class BoardPosition {
		int index;
		String prefix;
		boolean[] processed;
		Node prevNode;
		public BoardPosition(String prefix, int index, boolean[] processed, Node prevNode) {
			this.processed = processed;
			this.prefix = prefix;
			this.index = index;
			this.prevNode = prevNode;
		}
	}
	
	private static class BoardMember{
		char curChar;
		Bag<Integer> neighbors;
		
		public BoardMember(char curChar) {
			this.curChar = curChar;
			this.neighbors = new Bag<Integer>();
		}
	}
	
	private Trie26ST<Integer> dictionary;
	private BoardMember[] boardMembers;
    
	public BoggleSolver(String[] dictionary) {
		 this.dictionary = new Trie26ST<>();
		 int[] scoreMapping = new int[] { 0, 0, 1, 1, 2, 3, 5, 11 };
		 int maxScoreIndex = scoreMapping.length - 1;
		 for (int i = 0; i < dictionary.length; i++) {
			 String curStr = dictionary[i];
			 int value = Math.min(maxScoreIndex, curStr.length()-1);
			 this.dictionary.put(curStr, scoreMapping[value]);
		}
    }
	
	//prepare board
	private void prepareBoard(BoggleBoard board) {
		int noOfRows = board.rows();
		int noOfCols = board.cols();
		int[] prevCols = new int[noOfCols];
		int[] nextCols = new int[noOfCols];
		for (int i = 0; i < noOfCols; i++) {
			prevCols[i] = i-1;
			nextCols[i] = i+1;
		}
		prevCols[0] = 0;
		nextCols[noOfCols-1] = noOfCols-1;
		
		boardMembers = new BoardMember[noOfRows*noOfCols];
		int memberIndex = 0;
		for (int curRow = 0; curRow < noOfRows ; curRow++) {

			int prevRow = Math.max(0, curRow-1);
			int nextRow = Math.min(noOfRows-1, curRow+1);
			
			for (int curCol = 0; curCol < noOfCols; curCol++) {
				char curChar = board.getLetter(curRow, curCol);
				BoardMember newMember = new BoardMember(curChar);

				for (int neighborRow = prevRow; neighborRow <= nextRow; neighborRow++) {
					for (int neighborCol = prevCols[curCol]; neighborCol <= nextCols[curCol]; neighborCol++) {
						if (!(neighborRow == curRow && neighborCol == curCol)) {
							newMember.neighbors.add((neighborRow*noOfCols) + neighborCol);
						}
					}
				}
				boardMembers[memberIndex] = newMember;
				memberIndex++;
			}
		}
	}
	

	// Returns the set of all valid words in the given Boggle board, as an Iterable.
	public Iterable<String> getAllValidWords(BoggleBoard board){
		Set<String> allValidWords = new HashSet<>();
		prepareBoard(board);
		boolean[] processed = new boolean[board.rows()*board.cols()];
		Arrays.fill(processed, false);
		for (int i = 0; i < boardMembers.length; i++) {
			search(i, processed, board, allValidWords);			
		}
		return allValidWords;
	}
	
	private void search(int index, boolean[] processed, BoggleBoard board, Set<String> existingWords){
		Queue<BoardPosition> queue = new Queue<>();
		queue.enqueue(new BoardPosition("", index, processed, dictionary.getRootNode()));
		while (!queue.isEmpty()) {
			BoardPosition curPos = queue.dequeue();
			BoardMember curMember = boardMembers[curPos.index];
			String curStr = "" + curMember.curChar;
			if (curStr.charAt(0) == 'Q') {
				curStr += "U";
			}
			
			Node curNode = this.dictionary.get(curPos.prevNode, curStr, 0);
			if (curNode != null) {
				boolean[] curProcessed = curPos.processed;
				boolean[] newProc = new boolean[curProcessed.length];
				System.arraycopy(curProcessed, 0, newProc, 0, curProcessed.length);
				newProc[curPos.index] = true;

				String wholeString = curPos.prefix + curStr;
				if (curNode.val != null && wholeString.length() > 2) {
					existingWords.add(wholeString);
				}
				for(Integer curNeighborIndex: curMember.neighbors) {
					if (!newProc[curNeighborIndex]) {
						queue.enqueue(new BoardPosition(wholeString, curNeighborIndex, newProc, curNode));
					}
				}
			}
		}
	}

    // Returns the score of the given word if it is in the dictionary, zero otherwise.
    // (You can assume the word contains only the uppercase letters A through Z.)
	public int scoreOf(String word) {
		Integer value = this.dictionary.get(word);
		if (value == null) {
			return 0;
		}else {
			return value;
		}
	}
	
	public static void main(String[] args) {
		String dictionaryName = ClassLoader.getSystemClassLoader().getResource("boggle/dictionary-yawl.txt").toString();//"dictionary-algs4.txt";
		String boardName = ClassLoader.getSystemClassLoader().getResource("boggle/board-points26539.txt").toString();
		long startTime = System.currentTimeMillis();

	    In in = new In(dictionaryName);
	    String[] dictionary = in.readAllStrings();
	    BoggleSolver solver = new BoggleSolver(dictionary);

	    BoggleBoard board = new BoggleBoard(boardName);
	    int score = 0;
	    for (String word : solver.getAllValidWords(board)) {
	        StdOut.println(word);
	        score += solver.scoreOf(word);
	    }
	    StdOut.println("Score = " + score);
		//code
		long endTime = System.currentTimeMillis();
		System.out.println("Took "+(endTime - startTime) + " ms");
	}

}
