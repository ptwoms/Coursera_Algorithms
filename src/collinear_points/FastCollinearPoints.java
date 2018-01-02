import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * FastCollinearPoints.java
 * 
 * @author Pyae Phyo Myint Soe
 * created on 20/3/17.
 */
public class FastCollinearPoints {
    private LineSegment[] lineSegments;
    private final static double EPSILON = 0.00001;

    private static class SlopePoints implements Comparable<SlopePoints> {
        private Point[] mPoints;
        private double mSlope;
//        private double mB;

        public SlopePoints(double slope, Point[] points) {
            mSlope = slope;
            mPoints = points;
//            Point curSt = points[0];
//            mB = curSt.getY() - (mSlope * curSt.getX());
        }

        @Override
        public int compareTo(SlopePoints slopePoints) {
            if (mSlope == slopePoints.mSlope || (Math.abs(mSlope - slopePoints.mSlope) < EPSILON)){
                Point[] otherPts = slopePoints.getPoints();
                return mPoints[1].compareTo(otherPts[1]);
            }else if(mSlope > slopePoints.mSlope){
                return 1;
            }else {
                return -1;
            }
        }

        public double getSlope() {
            return mSlope;
        }

        public Point[] getPoints() {
            return mPoints;
        }
    }

    private Point[] retrieveSegment(List<Point> finalPoints){
        Point[] points = finalPoints.toArray(new Point[finalPoints.size()]);
        Arrays.sort(points);
        return new Point[]{points[0], points[points.length-1]};
    }

    private boolean isTwoLinesConnected(Point p1Start, Point p1End, Point p2Start, Point p2End){
        if (p1Start == null || p1End == null || p2Start == null || p2End == null){
            return false;
        }
        return p1End.compareTo(p2End) == 0;
//        Point[] ptArr = new Point[]{p1Start, p1End, p2Start, p2End};
//        Arrays.sort(ptArr);
//        double lineSlope;
//        int startIndex;
//
//        if (ptArr[0] == ptArr[1]){
//            lineSlope = ptArr[0].slopeTo(ptArr[2]);
//            startIndex = 2;
//        }else {
//            lineSlope = ptArr[0].slopeTo(ptArr[1]);
//            startIndex = 1;
//        }
//        boolean isConnected = false;
//        for (int i = startIndex; i < ptArr.length ; i++) {
//            isConnected = ptArr[0].compareTo(ptArr[i]) == 0 || (lineSlope == ptArr[0].slopeTo(ptArr[i]));
//        }
//        return isConnected;
    }

    public FastCollinearPoints(Point[] points)     // finds all line segments containing 4 or more points
    {
        if (points == null){
            throw new NullPointerException("at least one point should exist.");
        }
        Point[] tempPoints = Arrays.copyOf(points, points.length);
        Arrays.sort(tempPoints);
        for (int i = 0; i < tempPoints.length-1; i++) {
            if (tempPoints[i] == null){
                throw new NullPointerException("Point must not be null");
            }else if (tempPoints[i].compareTo(tempPoints[i+1]) == 0){
                throw new IllegalArgumentException("Repeated point discovered");
            }
        }
        List<SlopePoints> segments = new ArrayList<>();
        for (int i = 0; i < tempPoints.length-3; i++) {
            Point pivotPoint = tempPoints[i];
            Point[] otherPoints = new Point[tempPoints.length - i - 1];
            System.arraycopy(tempPoints, i+1, otherPoints, 0, otherPoints.length);
            Arrays.sort(otherPoints, pivotPoint.slopeOrder());
            List<Point> ptOfInterest = new ArrayList<>();
            double prevSlope = -1;
            for (Point curPt : otherPoints) {
                double curSlope = pivotPoint.slopeTo(curPt);
                if (ptOfInterest.size() > 0 && prevSlope == curSlope) {
                    ptOfInterest.add(curPt);
                } else {
                    if (ptOfInterest.size() >= 3) {
                        ptOfInterest.add(pivotPoint);
                        Point[] segmentPts = retrieveSegment(ptOfInterest);
                        segments.add(new SlopePoints(segmentPts[0].slopeTo(segmentPts[1]), segmentPts));
                    }
                    ptOfInterest.clear();
                    ptOfInterest.add(curPt);
                    prevSlope = curSlope;
                }
            }
            if (ptOfInterest.size() >= 3) {
                ptOfInterest.add(pivotPoint);
                Point[] segmentPts = retrieveSegment(ptOfInterest);
                segments.add(new SlopePoints(segmentPts[0].slopeTo(segmentPts[1]), segmentPts));
            }
        }

        if (segments.size() > 0){
            SlopePoints[] slopeArr = segments.toArray(new SlopePoints[segments.size()]);
            Arrays.sort(slopeArr);
            List<LineSegment> finalSegments = new ArrayList<>();
            double curSlope = Double.MAX_VALUE;
            Point startPt = null;
            Point endPt = null;
            for (int i = 0; i < slopeArr.length; i++) {
                SlopePoints curSlPt = slopeArr[i];
                Point[] curPts = curSlPt.getPoints();
                if (curSlope == curSlPt.getSlope() && isTwoLinesConnected(startPt, endPt, curPts[0], curPts[1])){
                    Point[] ptsOfInterest = new Point[]{startPt, endPt, curPts[0], curPts[1]};
                    Point[] newPts = retrieveSegment(Arrays.asList(ptsOfInterest));
                    startPt = newPts[0];
                    endPt = newPts[1];
                }else {
                    if (startPt != null && endPt != null){
                        finalSegments.add(new LineSegment(startPt, endPt));
                    }
                    startPt = curPts[0];
                    endPt = curPts[1];
                    curSlope = curSlPt.getSlope();
                }
            }
            if (startPt != null && endPt != null){
                finalSegments.add(new LineSegment(startPt, endPt));
            }
            this.lineSegments = finalSegments.toArray(new LineSegment[finalSegments.size()]);
        }else {
            lineSegments = new LineSegment[0];
        }
    }

    public int numberOfSegments()// the number of line segments
    {
        return (lineSegments == null) ? 0 : lineSegments.length;
    }

    public LineSegment[] segments()// the line segments
    {
        return Arrays.copyOf(lineSegments, lineSegments.length) ;
    }

    public static void main(String[] args) {
        // read the n points from a file
    		String fileName = ClassLoader.getSystemClassLoader().getResource("collinear/kw1260.txt").toString();
        In in = new In(fileName);
        int n = in.readInt();

        Point[] points = new Point[n];
        for (int i = 0; i < n; i++) {
            int x = in.readInt();
            int y = in.readInt();
            points[i] = new Point(x, y);
        }

        // draw the points
        StdDraw.enableDoubleBuffering();
        StdDraw.setXscale(0, 32768);
        StdDraw.setYscale(0, 32768);
        for (Point p : points) {
            p.draw();
        }
        StdDraw.show();

        // print and draw the line segments
        FastCollinearPoints collinear = new FastCollinearPoints(points);
        for (LineSegment segment : collinear.segments()) {
            StdOut.println(segment);
            StdDraw.setPenColor(StdRandom.uniform(256), StdRandom.uniform(256), StdRandom.uniform(256));
            segment.draw();
        }
        StdDraw.show();

    }
}
