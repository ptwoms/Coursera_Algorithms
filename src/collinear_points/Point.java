import edu.princeton.cs.algs4.StdDraw;

import java.util.Comparator;

/**
 * Point.java
 * 
 * @author Pyae Phyo Myint Soe
 * created on 17/3/17.
 */
public class Point implements Comparable<Point> {
    private final static double EPSILON = 0.00001;
    private final int x;     // x-coordinate of this point
    private final int y;     // y-coordinate of this point

    /**
     * Initializes a new point.
     *
     * @param  x the <em>x</em>-coordinate of the point
     * @param  y the <em>y</em>-coordinate of the point
     */
    public Point(int x, int y) {
        /* DO NOT MODIFY */
        this.x = x;
        this.y = y;
    }

    /**
     * Draws this point to standard draw.
     */
    public void draw() {
        /* DO NOT MODIFY */
        StdDraw.point(x, y);
    }

    /**
     * Draws the line segment between this point and the specified point
     * to standard draw.
     *
     * @param that the other point
     */
    public void drawTo(Point that) {
        /* DO NOT MODIFY */
        StdDraw.line(this.x, this.y, that.x, that.y);
    }

    /**
     * Compares two points by y-coordinate, breaking ties by x-coordinate.
     * Formally, the invoking point (x0, y0) is less than the argument point
     * (x1, y1) if and only if either y0 < y1 or if y0 = y1 and x0 < x1.
     *
     * @param  point the other point
     * @return the value <tt>0</tt> if this point is equal to the argument
     *         point (x0 = x1 and y0 = y1);
     *         a negative integer if this point is less than the argument
     *         point; and a positive integer if this point is greater than the
     *         argument point
     */
    @Override
    public int compareTo(Point point) {// compare two points by y-coordinates, breaking ties by x-coordinates
        if (this.y < point.y){
            return -1;
        }else if(this.y > point.y){
            return 1;
        }else {
            if (this.x < point.x){
                return -1;
            }else if (this.x > point.x){
                return 1;
            }
        }
        return 0;
    }

    /**
     * Returns the slope between this point and the specified point.
     * Formally, if the two points are (x0, y0) and (x1, y1), then the slope
     * is (y1 - y0) / (x1 - x0). For completeness, the slope is defined to be
     * +0.0 if the line segment connecting the two points is horizontal;
     * Double.POSITIVE_INFINITY if the line segment is vertical;
     * and Double.NEGATIVE_INFINITY if (x0, y0) and (x1, y1) are equal.
     *
     * @param  that the other point
     * @return the slope between this point and the specified point
     */
    public double slopeTo(Point that)// the slope between this point and that point
    {
        if (this.y == that.y && this.x == that.x){
            return Double.NEGATIVE_INFINITY;
        }else if (this.x == that.x){
            return Double.POSITIVE_INFINITY;
        }else if(this.y == that.y){
            return 0.0;
        }
        return (double)(that.y - this.y)/(double)(that.x - this.x);
    }

    private static class PointComparator implements Comparator<Point>{
        private Point centerPoint;

        public PointComparator(Point centerPoint) {
            this.centerPoint = centerPoint;
        }

        @Override
        public int compare(Point point, Point t1) {
            double pointSlope = centerPoint.slopeTo(point);
            double t1Slope = centerPoint.slopeTo(t1);
            if (pointSlope == t1Slope || Math.abs(pointSlope-t1Slope) < EPSILON){
                return 0;
            }else if(pointSlope > t1Slope){
                return 1;
            }else {
                return -1;
            }
        }
    }

    /**
     * Compares two points by the slope they make with this point.
     * The slope is defined as in the slopeTo() method.
     *
     * @return the Comparator that defines this ordering on points
     */
    public Comparator<Point> slopeOrder()// compare two points by slopes they make with this point
    {
        return new PointComparator(this);
    }

    /**
     * Returns a string representation of this point.
     * This method is provide for debugging;
     * your program should not rely on the format of the string representation.
     *
     * @return a string representation of this point
     */
    public String toString() {
        /* DO NOT MODIFY */
        return "(" + x + ", " + y + ")";
    }

    public static void main(String[] args) {
        Point p = new Point(245, 400);
        Point q = new Point(245, 297);
        System.out.println("" + p.slopeOrder().compare(q,q));

    }
}
