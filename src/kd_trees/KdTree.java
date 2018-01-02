import edu.princeton.cs.algs4.*;

import java.util.ArrayList;
import java.util.List;

/**
 * KdTree.java
 * 
 * @author Pyae Phyo Myint Soe
 * created on 27/3/17.
 */
public class KdTree {

    private class Node{
        private Point2D mKey;
        private int rank;
        private double mValue;
        private Node mLeft, mRight;
        private RectHV mBoundingRect;

        public Node(Point2D key, int rank, double value, RectHV boundingRect) {
            mKey = key;
            this.rank = rank;
            mValue = value;
            mBoundingRect = boundingRect;
        }
    }

    private Node root;
    private int mNumOfPoints;

    public KdTree() {
        mNumOfPoints = 0;
    }

    public boolean isEmpty()// is the set empty?
    {
        return root == null;
    }

    public int size()// number of points in the set
    {
        return mNumOfPoints;
    }

    private void checkNull(Point2D p){
        if (p == null){
            throw new NullPointerException("Point must not be null");
        }
    }

    private boolean isGoingLeft(Node curNode, Point2D p){
        double valueToSearch = (curNode.rank % 2 == 0) ? p.x() : p.y();
        return valueToSearch < curNode.mValue;
    }

    private Node searchParent(Node node, Point2D p){
        Node curNode = node;
        Node parentNode = node;
        while (curNode != null){
            parentNode = curNode;
            if (parentNode.mKey.equals(p)){
                return null;
            }
            if (isGoingLeft(curNode, p)){
                curNode = parentNode.mLeft;
            }else {
                curNode = parentNode.mRight;
            }
        }
        return parentNode;
    }

    public void insert(Point2D p)// add the point to the set (if it is not already in the set)
    {
        checkNull(p);
        if (root == null){
            root = new Node(p, 0, p.x(), new RectHV(0, 0, 1, 1));
        }else {
            Node parentNode = searchParent(root, p);
            if (parentNode == null){
                return;
            }
            int newRank = parentNode.rank + 1;
            Node nodeToInsert;
            double nodeValue = (newRank % 2 == 0) ? p.x() : p.y();
            RectHV parentRect = parentNode.mBoundingRect;
            if (isGoingLeft(parentNode, p)) {
                if (parentNode.rank % 2 == 0) {
                    nodeToInsert = new Node(p, newRank, nodeValue, new RectHV(parentRect.xmin(), parentRect.ymin(), parentNode.mValue, parentRect.ymax()));
                } else {
                    nodeToInsert = new Node(p, newRank, nodeValue, new RectHV(parentRect.xmin(), parentRect.ymin(), parentRect.xmax(), parentNode.mValue));
                }
                parentNode.mLeft = nodeToInsert;
            } else {
                if (parentNode.rank % 2 == 0) {
                    nodeToInsert = new Node(p, newRank, nodeValue, new RectHV(parentNode.mValue, parentRect.ymin(), parentRect.xmax(), parentRect.ymax()));
                } else {
                    nodeToInsert = new Node(p, newRank, nodeValue, new RectHV(parentRect.xmin(), parentNode.mValue, parentRect.xmax(), parentRect.ymax()));
                }
                parentNode.mRight = nodeToInsert;
            }
        }
        mNumOfPoints++;
    }

    public boolean contains(Point2D p)// does the set contain point p?
    {
        checkNull(p);
        Node curNode = root;
        while (curNode != null){
            if (p.equals(curNode.mKey)){
                return true;
            }
            if (isGoingLeft(curNode, p)){
                curNode = curNode.mLeft;
            }else {
                curNode = curNode.mRight;
            }
        }
        return false;
    }

    private void draw(Node element){
        StdDraw.setPenColor(10, 100, 0);
        element.mKey.draw();
        if (element.rank%2 == 0){
            StdDraw.setPenColor(255, 0, 0);
            StdDraw.line(element.mValue, element.mBoundingRect.ymin(), element.mValue, element.mBoundingRect.ymax());
        }else {
            StdDraw.setPenColor(0, 0, 255);
            StdDraw.line(element.mBoundingRect.xmin(), element.mValue, element.mBoundingRect.xmax(), element.mValue);
        }
    }

    public void draw()// draw all points to standard draw
    {
        if (root != null){
            Queue<Node> queue = new Queue<>();
            queue.enqueue(root);
            draw(root);
            while (!queue.isEmpty()){
                Node firstElement = queue.dequeue();
                if (firstElement.mLeft != null){
                    Node curNode = firstElement.mLeft;
                    draw(curNode);
                    queue.enqueue(curNode);
                }
                if (firstElement.mRight != null){
                    Node curNode = firstElement.mRight;
                    draw(curNode);
                    queue.enqueue(curNode);
                }
            }
        }
    }

    private void processNode(Node node, RectHV rectToCover, List<Point2D> allPoints){
        if (rectToCover.contains(node.mKey)){
            allPoints.add(node.mKey);
        }
        if (node.mLeft != null){
            if (node.mLeft.mBoundingRect.intersects(rectToCover)){
                processNode(node.mLeft, rectToCover, allPoints);
            }
        }
        if (node.mRight != null){
            if (node.mRight.mBoundingRect.intersects(rectToCover)){
                processNode(node.mRight, rectToCover, allPoints);
            }else if (node.mRight.mLeft != null){
                processNode(node.mRight.mLeft, rectToCover, allPoints);
            }
        }
    }

    public Iterable<Point2D> range(RectHV rect)// all points that are inside the rectangle
    {
        List<Point2D> ptsInsideRect = new ArrayList<>();
        if (root != null){
            processNode(root, rect, ptsInsideRect);
        }
        return ptsInsideRect;
    }

    private class NearestResult{
        Point2D mPoint;
        double mDistance;

        public NearestResult(Point2D point, double distance) {
            mPoint = point;
            mDistance = distance;
        }
    }

    private void getNearest(Node curNode, Point2D point, NearestResult bestResultSofar){
        double newDistance = point.distanceSquaredTo(curNode.mKey);
        if (newDistance < bestResultSofar.mDistance){
            bestResultSofar.mDistance = newDistance;
            bestResultSofar.mPoint = curNode.mKey;
        }
        Node firstNode, secondNode;
        if (isGoingLeft(curNode, point)){
            firstNode = curNode.mLeft;
            secondNode = curNode.mRight;
        }else {
            firstNode = curNode.mRight;
            secondNode = curNode.mLeft;
        }
        if (firstNode != null){
            if (firstNode.mBoundingRect.distanceSquaredTo(point) < bestResultSofar.mDistance){
                getNearest(firstNode, point, bestResultSofar);
            }
        }
        if (secondNode != null){
            if (secondNode.mBoundingRect.distanceSquaredTo(point) < bestResultSofar.mDistance){
                getNearest(secondNode, point, bestResultSofar);
            }
        }
    }

    public Point2D nearest(Point2D p)// a nearest neighbor in the set to point p; null if the set is empty
    {
        checkNull(p);
        if (root == null){
            return null;
        }
        NearestResult result = new NearestResult(root.mKey, Double.POSITIVE_INFINITY);
        getNearest(root, p, result);
        return result.mPoint;
    }

    public static void main(String[] args)// unit testing of the methods (optional)
    {
        StdDraw.enableDoubleBuffering();
        KdTree kdtree = new KdTree();
        String fileName = ClassLoader.getSystemClassLoader().getResource("kdtree/circle10.txt").toString();
        In in = new In(fileName);

        StdDraw.enableDoubleBuffering();

        // initialize the two data structures with point from standard input
        while (!in.isEmpty()) {
            double x = in.readDouble();
            double y = in.readDouble();
            Point2D p = new Point2D(x, y);
            kdtree.insert(p);
        }
        System.out.println("Size :" + kdtree.size());
        StdDraw.clear();
        kdtree.draw();
        StdDraw.show();
    }
}
