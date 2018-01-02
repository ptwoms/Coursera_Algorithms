import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

/**
 * MoveToFront.java
 * 
 * @author Pyae Phyo Myint Soe
 * created on 19/12/17.
 */

public class MoveToFront {
	private static int asciiLength = 256;
	private static BWLinkedList<Byte> extendedASCIILink;

	// apply move-to-front encoding, reading from standard input and writing to standard output
	public static void encode() {
		extendedASCIILink = new BWLinkedList<>();
		for (int i = 0; i < asciiLength; i++) {
			extendedASCIILink.addLast((byte) i);
		}
		
		while(!BinaryStdIn.isEmpty()) {
			char curChar = BinaryStdIn.readChar();
			byte loc = (byte) extendedASCIILink.getLocAndMoveFirst((byte)curChar);
			BinaryStdOut.write(loc);
		}
		BinaryStdOut.flush();
	}

	// apply move-to-front decoding, reading from standard input and writing to standard output
	public static void decode() {
		extendedASCIILink = new BWLinkedList<>();
		for (int i = 0; i < asciiLength; i++) {
			extendedASCIILink.addLast((byte)i);
		}
		while(!BinaryStdIn.isEmpty()) {
			int curIndex = (int)BinaryStdIn.readChar();
			byte value = extendedASCIILink.getValFromLocAndMoveFirst(curIndex);
			BinaryStdOut.write(value);
		}
		BinaryStdOut.flush();
	}

	// if args[0] is '-', apply move-to-front encoding
	// if args[0] is '+', apply move-to-front decoding
	public static void main(String[] args) {
		if(args.length == 0) {
			return;
		}
		if (args[0].equals("-")) {
			MoveToFront.encode();
		}else {
			MoveToFront.decode();
		}
	}
}
