import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.WeightedQuickUnionUF;

/**
 * Percolation.java
 * 
 * @author Pyae Phyo Myint Soe
 * created on 15/3/17.
 */
public class Percolation {
    private int gridSize;
    private WeightedQuickUnionUF gridData;
    private boolean isPercolate = false;
    private byte[] upperConnected;
    private byte[] lowerConnected;
    private byte[] openSites;

    private int openSitesCount;

    public Percolation(int n)                // create n-by-n grid, with all sites blocked
    {
        if (n <= 0){
            throw new IllegalArgumentException("n must be greater than 0");
        }
        gridSize = n;
        int totalSize = gridSize * gridSize;
        gridData = new WeightedQuickUnionUF(totalSize);

        int totalBytesRequired = (int)Math.ceil((double)totalSize/8.0);
        openSites = new byte[totalBytesRequired];
        upperConnected = new byte[totalBytesRequired];
        lowerConnected = new byte[totalBytesRequired];
        for (int i = 0; i < totalBytesRequired; i++) {
            openSites[i] = 0;
            upperConnected[i] = 0;
            lowerConnected[i] = 0;
        }
        openSitesCount = 0;
        isPercolate = false;
    }

    private void setBit(byte[] byteArray, int position){
        int posToInsert = position/8;
        byteArray[posToInsert] = (byte) (byteArray[posToInsert] | (byte)(128 >> (position%8)));
    }

    private boolean isBitSet(byte[] byteArray, int position){
        return (byteArray[position/8] & (byte)(128 >> (position%8))) != 0;
    }

    private boolean isSiteOpen(int position){
        return isBitSet(openSites, position);
    }

    private boolean isTopConnected(int position){
        return isBitSet(upperConnected, position);
    }

    private boolean isBottomConnected(int position){
        return isBitSet(lowerConnected, position);
    }

    private void setTopConnected(int position){
        setBit(upperConnected, position);
    }

    private void setBottomConnected(int position){
        setBit(lowerConnected, position);
    }

    private void checkBounds(int row, int col){
        if(row <= 0 || row > gridSize || col <= 0 || col > gridSize){
            throw new IndexOutOfBoundsException("invalid row or column");
        }
    }

    private int getPos(int row, int col){
        checkBounds(row, col);
        return (row - 1) * gridSize + (col - 1);
    }

    public void open(int row, int col)    // open site (row, col) if it is not open already
    {
        int posToOpen = getPos(row, col);
        if (isSiteOpen(posToOpen)){
            return;
        }
        setBit(openSites, posToOpen);
        openSitesCount++;
            boolean[] preConditions = new boolean[]{
                    (row > 1 && isSiteOpen(posToOpen-gridSize)),
                    row < gridSize && isSiteOpen(posToOpen + gridSize),
                    col > 1 && isSiteOpen(posToOpen - 1),
                    col < gridSize && isSiteOpen(posToOpen + 1)
            };
            int[] posOfInterest = new int[]{
                    posToOpen - gridSize,
                    posToOpen + gridSize,
                    posToOpen - 1,
                    posToOpen + 1
            };
            boolean topConnected = (row == 1)? true: false;
            boolean bottomConnected = (row == gridSize) ? true: false;
            for (int i = 0; i < preConditions.length; i++) {
                if (preConditions[i]){
                    int posToMerge = posOfInterest[i];
                    int posToMergeParent = gridData.find(posToMerge);
                    topConnected = topConnected || isTopConnected(posToMergeParent);
                    bottomConnected = bottomConnected || isBottomConnected(posToMergeParent);
                    gridData.union(posToOpen, posToMerge);
                }
            }
            int newParent = gridData.find(posToOpen);
            if (topConnected){
                setTopConnected(newParent);
            }
            if (bottomConnected){
                setBottomConnected(newParent);
            }
            if (topConnected && bottomConnected){
                isPercolate = true;
            }
    }

    public boolean isOpen(int row, int col)  // is site (row, col) open?
    {
        return  isSiteOpen(getPos(row, col));
    }

    public boolean isFull(int row, int col)  // is site (row, col) full?
    {
        return isTopConnected(gridData.find(getPos(row, col)));
    }

    public int numberOfOpenSites()       // number of open sites
    {
        return openSitesCount;
    }

    public boolean percolates()              // does the system percolate?
    {
        return isPercolate;
    }

    public static void main(String[] args)   // test client (optional)
    {
        String baseFilePath = "percolation/";
        String[] allFileNames = new String[]{
                "input1-no.txt",
                "input1.txt",
                "input2-no.txt",
                "input2.txt",
                "input3.txt",
                "input4.txt",
                "input5.txt",
                "input6.txt",
                "input7.txt",
                "input8-no.txt",
                "input8.txt",
                "input10-no.txt",
                "input10.txt",
                "input20.txt",
                "input50.txt",
                "jerry47.txt",
                "sedgewick60.txt",
                "wayne98.txt"
        };

        System.out.println("Percolate test:");
        for (String curFileName: allFileNames){
            In file = new In(ClassLoader.getSystemClassLoader().getResource(baseFilePath + curFileName).toString());
            int totalCount = file.readInt();
            Percolation percolation = new Percolation(totalCount);
            while (!file.isEmpty()){
                percolation.open(file.readInt(), file.readInt());
            }
            System.out.println(curFileName + ":         " + percolation.percolates());
        }
    }
}
