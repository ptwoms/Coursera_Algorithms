import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdRandom;

import java.util.ArrayList;
import java.util.List;

/**
 * Board.java
 * 
 * @author Pyae Phyo Myint Soe
 * created on 26/3/17.
 */

public class Board {
    private int blocks[][];
    private int noOfDimensions;
    private int emptyPositionX, emptyPositionY;

    public Board(int[][] blocks)// construct a board from an n-by-n array of blocks (where blocks[i][j] = block in row i, column j)
    {
        noOfDimensions = blocks.length;

        int[][] newBlks = new int[blocks.length][];
        for (int i = 0; i < blocks.length; i++){
            newBlks[i] = new int[blocks[i].length];
            for (int j = 0; j < blocks.length; j++) {
                newBlks[i][j] = blocks[i][j];
                if (blocks[i][j] == 0){
                    emptyPositionX = j;
                    emptyPositionY = i;
                }
            }
        }
        this.blocks = newBlks;
    }

//    private int[][] copyBlocks(int[][] blocks){
//        int[][] newBlks = new int[blocks.length][];
//        for (int i = 0; i < blocks.length; i++){
//            newBlks[i] = Arrays.copyOf(blocks[i], blocks[i].length);
//        }
//        return newBlks;
//    }

    public int dimension()// board dimension n
    {
        return noOfDimensions;
    }

    public int hamming()// number of blocks out of place
    {
        int outOfSpaceBlks = 0;
        int curIndex = 1;
        for (int i = 0; i < noOfDimensions ; i++) {
            for (int j = 0; j < noOfDimensions; j++) {
                outOfSpaceBlks += (blocks[i][j] == curIndex) ? 0 : 1;
                curIndex++;
            }
        }
        return outOfSpaceBlks-1;
    }

    public int manhattan()// sum of Manhattan distances between blocks and goal
    {
        int totalDistance = 0;
        for (int i = 0; i < noOfDimensions ; i++) {
            for (int j = 0; j < noOfDimensions; j++) {
                if (blocks[i][j] == 0)continue;
                int curPos = blocks[i][j] - 1 ;
                int goalY = curPos/noOfDimensions;
                int goalX = curPos%noOfDimensions;
                totalDistance += Math.abs(j - goalX) + Math.abs(i - goalY);
            }
        }
        return totalDistance;
    }

    public boolean isGoal() // is this board the goal board?
    {
        int curIndex = 1;
        int innerLoop = noOfDimensions;
        for (int i = 0; i < noOfDimensions ; i++) {
            if (i == noOfDimensions - 1){
                innerLoop = noOfDimensions - 1;
            }
            for (int j = 0; j < innerLoop; j++) {
                if (blocks[i][j] != curIndex++){
                    return false;
                }
            }
        }
        return true;
    }

    public Board twin()// a board that is obtained by exchanging any pair of blocks
    {
        if (noOfDimensions <= 1){
            return new Board(blocks);
        }
        int firstX, firstY;
        do{
            int firstPos = StdRandom.uniform(noOfDimensions*noOfDimensions);
            firstY = firstPos/noOfDimensions;
            firstX = firstPos%noOfDimensions;
        }while (blocks[firstY][firstX] == 0);

        int secondX, secondY;
        do{
            int secondPos = StdRandom.uniform(noOfDimensions*noOfDimensions);
            secondY = secondPos/noOfDimensions;
            secondX = secondPos%noOfDimensions;
        }while (blocks[secondY][secondX] == 0 || (firstX == secondX && firstY == secondY));
        return swapBlock(firstX, firstY, secondX, secondY);
    }

    public boolean equals(Object y)// does this board equal y?
    {
        if (y == null || !(y instanceof Board)){
            return false;
        }
        if (this == y){
            return true;
        }
        Board otherBoard = (Board) y;
        if (noOfDimensions != otherBoard.noOfDimensions){
            return false;
        }
        for (int i = 0; i < noOfDimensions; i++) {
            for (int j = 0; j < noOfDimensions; j++) {
                if (blocks[i][j] != otherBoard.blocks[i][j]){
                    return false;
                }
            }
        }
        return true;
    }

    private Board swapBlock(int firstX, int firstY, int secondX, int secondY){
        int tempX = blocks[firstY][firstX];
        blocks[firstY][firstX] = blocks[secondY][secondX];
        blocks[secondY][secondX] = tempX;
        Board newBoard = new Board(blocks);
        blocks[secondY][secondX] = blocks[firstY][firstX];
        blocks[firstY][firstX] = tempX;
        return newBoard;
    }

    private Board moveBlock(int dx, int dy){
        return swapBlock(emptyPositionX, emptyPositionY, emptyPositionX + dx, emptyPositionY + dy );
    }

    public Iterable<Board> neighbors()// all neighboring boards
    {
        List<Board> neighbors = new ArrayList<>();
        if (emptyPositionY > 0){//top
            neighbors.add(moveBlock(0, -1));
        }
        if (emptyPositionY < noOfDimensions - 1){//bottom
            neighbors.add(moveBlock(0, 1));
        }
        if (emptyPositionX > 0){//left
            neighbors.add(moveBlock(-1, 0));
        }
        if (emptyPositionX < noOfDimensions - 1){//right
            neighbors.add(moveBlock(1, 0));
        }
        return neighbors;
    }

    public String toString()// string representation of this board (in the output format specified below)
    {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(String.valueOf(noOfDimensions) + "\n");
        for (int i = 0; i < noOfDimensions ; i++) {
            for (int j = 0; j < noOfDimensions-1; j++) {
                stringBuilder.append(String.valueOf(blocks[i][j]) + " ");
            }
            stringBuilder.append(String.valueOf(blocks[i][noOfDimensions-1]) + "\n");
        }
        return stringBuilder.toString();
    }

    public static void main(String[] args) // unit tests (not graded)
    {
    		String fileName = ClassLoader.getSystemClassLoader().getResource("8puzzle/puzzle2x2-unsolvable1.txt").toString();
        In in = new In(fileName);
        int n = in.readInt();
        int[][] blocks = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                blocks[i][j] = in.readInt();
        Board initial = new Board(blocks);
        System.out.println("No of dimensions:" + initial.dimension());
        System.out.println("Manhattan:" + initial.manhattan());
        System.out.println("Hamming:" + initial.hamming());
        System.out.println(initial.toString());

        System.out.println(initial.equals(new Board(blocks)));
        System.out.println("Twin");
        System.out.println(initial.twin().toString());
        System.out.println("Is Goal: " + initial.isGoal());
        for (Board curNeighbor: initial.neighbors()) {
            System.out.println(curNeighbor.toString());
        }
    }
}
