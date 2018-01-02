import edu.princeton.cs.algs4.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * SAPTopology.java
 * 
 * @author Pyae Phyo Myint Soe
 * created on 21/2/17.
 */

public class SAPTopology {
    private Digraph mDigraph;

    public SAPTopology(Digraph G) {
        mDigraph = new Digraph(G);;
    }

    private void checkIsValidIndex(int v, int w) throws IndexOutOfBoundsException{
        int noOfVertices = mDigraph.V();
        if (v < 0 || v >= noOfVertices || w < 0 || w >= noOfVertices){
            throw new IndexOutOfBoundsException("Invalid Vertex number");
        }
    }

    private void  checkForNull(Iterable<Integer> v, Iterable<Integer> w) throws NullPointerException{
        if (v == null || w == null)throw new NullPointerException("Both v and w must not be null");
    }

    private static class CalculatedValue{
        int  shortestDistance, shortestAnsector;

        public CalculatedValue(int shortestDistance, int shortestAnsector) {
            this.shortestDistance = shortestDistance;
            this.shortestAnsector = shortestAnsector;
        }
    }

    private CalculatedValue calculateSAP(Iterable<Integer> v, Iterable<Integer> w){
        int noOfVertices = mDigraph.V();
        int distanceV[] = new int[noOfVertices];
        int distanceW[] = new int[noOfVertices];
//        int edgesFromV[] = new int[noOfVertices];
//        int edgesFromW[] = new int[noOfVertices];

        Queue<Integer> queueV = new LinkedList<>();
        boolean[] markedV = new boolean[noOfVertices];
        Queue<Integer> queueW = new LinkedList<>();
        boolean[] markedW = new boolean[noOfVertices];

        for (int i = 0; i < noOfVertices; i++){
            distanceV[i] = 0;
            markedV[i] = false;
//            edgesFromV[i] = -1;
//            edgesFromW[i] = -1;
            distanceW[i] = 0;
            markedW[i] = false;
        }

        for (Integer curV: v) {
            markedV[curV] = true;
            distanceV[curV] = 0;
            queueV.add(curV);
        }

        for (Integer curW: w){
            markedW[curW] = true;
            distanceW[curW] = 0;
            if (markedV[curW]){
                return new CalculatedValue(distanceV[curW] + distanceW[curW], curW);
            }
            queueW.add(curW);
        }
        int nearestAnscetor = -1;
        int nearestDistance = Integer.MAX_VALUE;
        boolean stopV = false, stopW = false;
        boolean emptyV = false, emptyW = false;
        int curIteration = 0;
        while ((!stopV || !stopW) || (curIteration <= nearestDistance*2 && (!emptyV || !emptyW))){
            if (!queueV.isEmpty()){
                int curV = queueV.remove();
                Iterable<Integer> adjV = mDigraph.adj(curV);
                for (Integer i : adjV){
                    if (!markedV[i]){
                        distanceV[i] = distanceV[curV] + 1;
//                        edgesFromV[i] = curV;
                        markedV[i] = true;
                        queueV.add(i);
                        if (markedW[i]){
                            int newDistance = distanceV[i] + distanceW[i];
                            if (newDistance < nearestDistance){
                                nearestAnscetor = i;
                                nearestDistance = newDistance;
                            }
                            stopV = true;
                        }
                    }
                }
            }else{
                emptyV = true;
                stopV = true;
            }
            if (!queueW.isEmpty()){
                int curW = queueW.remove();
                Iterable<Integer> adjW = mDigraph.adj(curW);
                for (Integer i : adjW){
                    if (!markedW[i]){
                        distanceW[i] = distanceW[curW] + 1;
//                        edgesFromW[i] = curW;
                        markedW[i] = true;
                        queueW.add(i);
                        if (markedV[i]){
                            int newDistance = distanceV[i] + distanceW[i];
                            if (newDistance < nearestDistance){
                                nearestAnscetor = i;
                                nearestDistance = newDistance;
                            }
                            stopW = true;
                        }
                    }
                }
            }else {
                emptyW = true;
                stopW = true;
            }
            curIteration += 1;
        }

        if (nearestAnscetor != -1){
//            if (mNearestAnsector[source][sink] == -2){////BFS always return the shortest path
//                mNearestAnsector[source][sink] = nearestAnscetor;
//                mNearestAnsectorDistance[source][sink] = distanceV[nearestAnscetor] + distanceW[nearestAnscetor];
//            }
            return new CalculatedValue(nearestDistance, nearestAnscetor);
        }
        return null;
    }

    public int length(int v, int w){
        checkIsValidIndex(v, w);
            List<Integer> vIter = new ArrayList<>(1);
            vIter.add(v);
            List<Integer> wIter = new ArrayList<>(1);
            wIter.add(w);
            CalculatedValue calculatedValue = calculateSAP(vIter, wIter);
            if (calculatedValue != null){
                return calculatedValue.shortestDistance;
            }
            return -1;
    }

    public int ancestor(int v, int w){
        checkIsValidIndex(v, w);
            List<Integer> vIter = new ArrayList<>(1);
            vIter.add(v);
            List<Integer> wIter = new ArrayList<>(1);
            wIter.add(w);
        CalculatedValue calculatedValue = calculateSAP(vIter, wIter);
        if (calculatedValue != null){
            return calculatedValue.shortestAnsector;
        }
        return -1;
    }

    public int length(Iterable<Integer> v, Iterable<Integer> w){
//        checkForNull(v, w);
        CalculatedValue calValue = calculateSAP(v, w);
        if (calValue != null){
            return calValue.shortestDistance;
        }
        return -1;
    }

    public int ancestor(Iterable<Integer> v, Iterable<Integer> w){
        checkForNull(v, w);
        CalculatedValue calValue = calculateSAP(v, w);
        if (calValue != null){
            return calValue.shortestAnsector;
        }
        return -1;
    }

    public static void main(String[] args){
        String digraphFile = ClassLoader.getSystemClassLoader().getResource("wordnet/digraph-wordnet.txt").toString();
        In in = new In(digraphFile);
        Digraph G = new Digraph(in);
        System.out.println(G.toString());
        System.out.println();
        SAPTopology sap = new SAPTopology(G);
        while (!StdIn.isEmpty()) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            int length   = sap.length(v, w);
            int ancestor = sap.ancestor(v, w);
            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        }
    }
}
