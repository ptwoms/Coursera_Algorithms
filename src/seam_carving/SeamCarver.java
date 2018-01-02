
import edu.princeton.cs.algs4.AcyclicSP;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.DirectedEdge;
import edu.princeton.cs.algs4.EdgeWeightedDigraph;
import edu.princeton.cs.algs4.Picture;
import java.awt.Color;
import java.util.Iterator;

/**
 * SeamCarver.java
 * 
 * @author Pyae Phyo Myint Soe 
 * created on 23/2/17. 
 * Draft submission to see that it is working or not
 */
public class SeamCarver {
	// create a seam carver object based on the given picture
	private Picture mPicture;

	public SeamCarver(Picture picture) {
		if (picture == null) {
			throw new NullPointerException("Picture must not be null");
		}
		mPicture = new Picture(picture);
	}

	// current picture
	public Picture picture() {
		return new Picture(mPicture);
	}

	// width of current picture
	public int width() {
		return mPicture.width();
	}

	// height of current picture
	public int height() {
		return mPicture.height();
	}

	// energy of pixel at column x and row y
	public double energy(int x, int y) {
		if (x < 0 || x >= mPicture.width() || y < 0 || y >= mPicture.height()) {
			throw new IndexOutOfBoundsException("Invalid x and/or y position");
		}
		if (x == 0 || x == mPicture.width() - 1 || y == 0 || y == mPicture.height() - 1) {
			return 1000.0;
		}
		Color x0 = mPicture.get(x - 1, y);
		Color x2 = mPicture.get(x + 1, y);
		Color y0 = mPicture.get(x, y - 1);
		Color y2 = mPicture.get(x, y + 1);
		double xDiffSquared = Math.pow((double) (x2.getRed() - x0.getRed()), 2.0)
				+ Math.pow((double) (x2.getGreen() - x0.getGreen()), 2.0)
				+ Math.pow((double) (x2.getBlue() - x0.getBlue()), 2.0);
		double yDiffSqured = Math.pow((double) (y2.getRed() - y0.getRed()), 2.0)
				+ Math.pow((double) (y2.getGreen() - y0.getGreen()), 2.0)
				+ Math.pow((double) (y2.getBlue() - y0.getBlue()), 2.0);
		return Math.sqrt((xDiffSquared + yDiffSqured));
	}

	private int getY(int pos) {
		return (int) Math.ceil((double) pos / (double) (width() - 2));
	}

	private int getX(int pos) {
		return ((pos - 1) % (width() - 2)) + 1;
	}

	private EdgeWeightedDigraph createTopDownDigraph() {
		int width = width() - 2;// remove left and right most
		int height = height() - 2;// remove top and bottom
		if (width <= 0 || height <= 0)
			return null;
		EdgeWeightedDigraph digraph = new EdgeWeightedDigraph(width * height + 2);

		for (int i = 1; i <= width; i++) {
			digraph.addEdge(new DirectedEdge(0, i, energy(i, 1)));
		}
		int curRow = 1, nextRow = width + 1;
		for (int y = 0; y < height - 1; y++) {
			for (int x = 0; x < width; x++) {
				if (x > 0) {
					digraph.addEdge(new DirectedEdge(curRow + x, nextRow + x - 1, energy(x, y + 2)));
				}
				digraph.addEdge(new DirectedEdge(curRow + x, nextRow + x, energy(x + 1, y + 2)));
				if (x < width - 1) {
					digraph.addEdge(new DirectedEdge(curRow + x, nextRow + x + 1, energy(x + 2, y + 2)));
				}
			}
			curRow += width;
			nextRow += width;
		}
		int lastRow = width * (height - 1);
		int sink = width * height + 1;
		for (int i = 1; i <= width; i++) {
			digraph.addEdge(new DirectedEdge(lastRow + i, sink, energy(i, height + 1)));
		}
		return digraph;
	}

	private EdgeWeightedDigraph createLeftRightDigraph() {
		int width = width() - 2;// remove left and right most
		int height = height() - 2;// remove top and bottom
		if (width <= 0 || height <= 0)
			return null;
		EdgeWeightedDigraph digraph = new EdgeWeightedDigraph(width * height + 2);
		for (int i = 0; i < height; i++) {
			digraph.addEdge(new DirectedEdge(0, (i * width) + 1, energy(1, i + 1)));
		}
		int curRow = 1, nextRow = width + 1, prevRow = 0;
		for (int y = 1; y <= height; y++) {
			for (int x = 0; x < width - 1; x++) {
				if (y > 1) {
					digraph.addEdge(new DirectedEdge(curRow + x, prevRow + x + 1, energy(x + 2, y - 1)));
				}
				digraph.addEdge(new DirectedEdge(curRow + x, curRow + x + 1, energy(x + 2, y)));
				if (y < height) {
					digraph.addEdge(new DirectedEdge(curRow + x, nextRow + x + 1, energy(x + 2, y + 1)));
				}
			}
			prevRow = curRow;
			curRow += width;
			nextRow += width;
		}
		int sink = width * height + 1;
		for (int i = 1; i <= height; i++) {
			digraph.addEdge(new DirectedEdge(i * width, sink, energy(width + 1, i)));
		}
		return digraph;
	}

	// true - verticalSeam, false - horizontalSeam
	private int[] handleCornerImages(boolean verticalSeam) {
		if (width() <= 2 || height() <= 2) {// widths and heights are usually greater than 0
			if (verticalSeam) {
				return new int[height()];// 0 - by default
			} else {
				return new int[width()];// 0 - by default
			}
		}
		return null;
	}

	// sequence of indices for horizontal seam
	public int[] findHorizontalSeam() {
		int[] cornerSeam = handleCornerImages(false);
		if (cornerSeam == null) {
			EdgeWeightedDigraph digraph = createLeftRightDigraph();
			if (digraph != null) {
				AcyclicSP topologySP = new AcyclicSP(digraph, 0);
				int sink = digraph.V() - 1;
				if (topologySP.hasPathTo(sink)) {
					Iterable<DirectedEdge> path = topologySP.pathTo(sink);
					int size = ((Stack<DirectedEdge>) path).size() - 1;
					if (size > 0) {
						int[] newInt = new int[size + 2];
						Iterator<DirectedEdge> edgeIterator = path.iterator();
						for (int i = 1; i <= size && edgeIterator.hasNext(); i++) {
							DirectedEdge curEdge = edgeIterator.next();
							newInt[i] = getY(curEdge.to());
						}
						newInt[0] = newInt[1] - 1;
						newInt[size + 1] = newInt[size] - 1;
						return newInt;
					}
				}
			}
		}
		return cornerSeam;
	}

	// sequence of indices for vertical seam
	public int[] findVerticalSeam() {
		int[] cornerSeam = handleCornerImages(true);
		if (cornerSeam == null) {
			EdgeWeightedDigraph digraph = createTopDownDigraph();
			if (digraph != null) {
				AcyclicSP topologySP = new AcyclicSP(digraph, 0);
				int sink = digraph.V() - 1;
				if (topologySP.hasPathTo(sink)) {
					Iterable<DirectedEdge> path = topologySP.pathTo(sink);
					int size = ((Stack<DirectedEdge>) path).size() - 1;
					if (size > 0) {
						int[] newInt = new int[size + 2];
						Iterator<DirectedEdge> edgeIterator = path.iterator();
						for (int i = 1; i <= size && edgeIterator.hasNext(); i++) {
							DirectedEdge curEdge = edgeIterator.next();
							newInt[i] = getX(curEdge.to());
						}
						newInt[0] = newInt[1] - 1;
						newInt[size + 1] = newInt[size] - 1;
						return newInt;
					}
				}
			}
		}
		return cornerSeam;
	}

	// remove horizontal seam from current picture
	public void removeHorizontalSeam(int[] seam) {
		if (seam == null) {
			throw new NullPointerException("seam must not be null");
		} else if (mPicture.height() <= 1 || seam.length != mPicture.width()) {
			throw new IllegalArgumentException("Invalid steam length or img height is <= 1");
		}
		int origWidth = mPicture.width();
		int origHeight = mPicture.height();
		Picture newPic = new Picture(origWidth, origHeight - 1);
		int indexExtra;
		for (int x = 0, y; x < origWidth; x++) {
			if (seam[x] < 0 || seam[x] >= origHeight || (x > 0 && Math.abs(seam[x] - seam[x - 1]) > 1)) {
				throw new IllegalArgumentException("Invalid steam value" + seam[x]);
			}
			indexExtra = 0;
			for (y = 0; y < origHeight; y++) {
				if (y == seam[x]) {
					indexExtra = -1;
				} else {
					newPic.set(x, y + indexExtra, mPicture.get(x, y));
				}
			}
		}
		mPicture = newPic;
	}

	// remove vertical seam from current picture
	public void removeVerticalSeam(int[] seam) {
		if (seam == null) {
			throw new NullPointerException("seam must not be null");
		} else if (mPicture.width() <= 1 || seam.length != mPicture.height()) {
			throw new IllegalArgumentException("Invalid steam length or img width is <= 1");
		}
		int origWidth = mPicture.width();
		int origHeight = mPicture.height();
		Picture newPic = new Picture(origWidth - 1, origHeight);
		int indexExtra;
		for (int y = 0, x; y < origHeight; y++) {
			if (seam[y] < 0 || seam[y] >= origWidth || (y > 0 && Math.abs(seam[y] - seam[y - 1]) > 1)) {
				throw new IllegalArgumentException("Invalid steam value " + seam[y]);
			}
			indexExtra = 0;
			for (x = 0; x < origWidth; x++) {
				if (x == seam[y]) {
					indexExtra = -1;
				} else {
					newPic.set(x + indexExtra, y, mPicture.get(x, y));
				}
			}
		}
		mPicture = newPic;
	}

}
