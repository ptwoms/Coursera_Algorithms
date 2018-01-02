import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.List;

/**
 * Solver.java
 * 
 * @author Pyae Phyo Myint Soe
 * created on 26/3/17.
 */
public class Solver {
    private static class Node implements Comparable<Node>{
        private Board mBoard;
        private int mMoves;
        private int mHeuristicDistance;
        private Node mPrevNode;

        public Node(Board board, int moves, Node prevNode) {
            mBoard = board;
            mMoves = moves;
            mHeuristicDistance = board.manhattan();
            mPrevNode = prevNode;
        }

        @Override
        public int compareTo(Node node) {
            int myDistance = mHeuristicDistance + mMoves;
            int otherDistance = node.mHeuristicDistance + node.mMoves;
            if (myDistance < otherDistance){
                return -1;
            }else if (myDistance > otherDistance){
                return 1;
            }else{
                if (mHeuristicDistance < node.mHeuristicDistance){
                    return -1;
                }else if (mHeuristicDistance > node.mHeuristicDistance){
                    return 1;
                }
            }
            return 0;
        }

        public Board getBoard() {
            return mBoard;
        }

        public int getMoves() {
            return mMoves;
        }

        public Node getPrevNode() {
            return mPrevNode;
        }
    }

    private List<Board> mSolutionPath;
    private boolean mSolvable;

    public Solver(Board initial)// find a solution to the initial board (using the A* algorithm)
    {
        if (initial == null){
            throw new NullPointerException("Initial board must not be null");
        }
        int boardDimension = initial.dimension();
        Board[][] path = new Board[boardDimension][boardDimension];
        for (int i = 0; i < i; i++) {
            path[i] = new Board[boardDimension];
        }
        MinPQ<Node> possibleMoves = new MinPQ<>();
        MinPQ<Node> switchedMoves = new MinPQ<>();
        possibleMoves.insert(new Node(initial, 0, null));
        Board twinBoard = initial.twin();
        switchedMoves.insert(new Node(twinBoard, 0, null));

        while (!possibleMoves.isEmpty() || !switchedMoves.isEmpty()){
            Node minNode = possibleMoves.delMin();
            Node switchMinNode = switchedMoves.delMin();
            Board minBoard = minNode.getBoard();
            Board alternativeBoard = switchMinNode.getBoard();
            if (minBoard.isGoal()){
                mSolvable = true;
                mSolutionPath = new ArrayList<>();
                Node curNode = minNode;
                while (curNode != null){
                    mSolutionPath.add(0, curNode.getBoard());
                    curNode = curNode.getPrevNode();
                }
                break;
            }
            else if (alternativeBoard.isGoal()){
                mSolvable = false;
                break;
            }
            int nextMove = minNode.getMoves()+1;
            Board prevBoard = null;
            if (minNode.getPrevNode() != null){
                prevBoard = minNode.getPrevNode().getBoard();
            }
            for (Board curBoard: minBoard.neighbors()) {
                if (prevBoard != null && prevBoard.equals(curBoard)) {
                    continue;
                }
                possibleMoves.insert(new Node(curBoard, nextMove, minNode));
            }
            nextMove = switchMinNode.getMoves() + 1;
            Board prevTwinBoard = null;
            if (switchMinNode.getPrevNode() != null){
                prevTwinBoard = switchMinNode.getPrevNode().getBoard();
            }
            for (Board curBoard: alternativeBoard.neighbors()) {
                if (prevTwinBoard != null && prevTwinBoard.equals(curBoard)){
                    continue;
                }
                switchedMoves.insert(new Node(curBoard, nextMove, switchMinNode));
            }
        }
    }

    public boolean isSolvable()// is the initial board solvable?
    {
        return mSolvable;
    }

    public int moves()// min number of moves to solve initial board; -1 if unsolvable
    {
        return (mSolvable) ? mSolutionPath.size()-1 : -1;
    }

    public Iterable<Board> solution()// sequence of boards in a shortest solution; null if unsolvable
    {
        return mSolutionPath;
    }

    public static void main(String[] args)// solve a slider puzzle (given below)
    {
        // create initial board from file
    		String fileName = ClassLoader.getSystemClassLoader().getResource("8puzzle/puzzle4x4-21.txt").toString();
        In in = new In(fileName);
        int n = in.readInt();
        int[][] blocks = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                blocks[i][j] = in.readInt();
        Board initial = new Board(blocks);

        // solve the puzzle
        Solver solver = new Solver(initial);

        // print solution to standard output
        if (!solver.isSolvable())
            StdOut.println("No solution possible");
        else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution())
                StdOut.println(board);
        }
    }
}
