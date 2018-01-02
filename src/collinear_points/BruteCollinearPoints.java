import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * BruteCollinearPoints.java
 * 
 * @author Pyae Phyo Myint Soe
 * created on 20/3/17.
 */
public class BruteCollinearPoints {
    private final LineSegment[] lineSegments;

    private LineSegment retrieveLineSegment(Point[] points){
        Arrays.sort(points);
        return new LineSegment(points[0], points[points.length-1]);
    }

    public BruteCollinearPoints(Point[] points)// finds all line segments containing 4 points
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
        //For simplicity, we will not supply any input to BruteCollinearPoints that has 5 or more collinear points.
        List<LineSegment> segments = new ArrayList<>();
        for (int i = 0; i < tempPoints.length-3; i++) {
            Point pivotPoint = tempPoints[i];
            for(int j = i+1; j < tempPoints.length-2;j++){
                double slope1 = pivotPoint.slopeTo(tempPoints[j]);
                for (int k = j+1; k < tempPoints.length-1; k++) {
                    double slope2 = pivotPoint.slopeTo(tempPoints[k]);
                    for (int l = k+1; l < tempPoints.length; l++) {
                        double slope3 = pivotPoint.slopeTo(tempPoints[l]);
                        if(slope1 == slope2 && slope1 == slope3){
                            segments.add(retrieveLineSegment(new Point[]{pivotPoint, tempPoints[j], tempPoints[k], tempPoints[l]}));
                        }
                    }
                }
            }
        }
        if (segments.size() > 0){
            lineSegments = segments.toArray(new LineSegment[segments.size()]);
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
    		String fileName = ClassLoader.getSystemClassLoader().getResource("collinear/input10.txt").toString();
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
        BruteCollinearPoints collinear = new BruteCollinearPoints(points);
        for (LineSegment segment : collinear.segments()) {
            StdOut.println(segment);
            segment.draw();
        }
        StdDraw.show();
    }

}
