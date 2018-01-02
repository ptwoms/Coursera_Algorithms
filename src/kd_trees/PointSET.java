import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.SET;

import java.util.ArrayList;
import java.util.List;

/**
 * PointSET.java
 * 
 * @author Pyae Phyo Myint Soe
 * created on 27/3/17.
 */
public class PointSET {

    private SET<Point2D> points;

    public PointSET()// construct an empty set of points
    {
        points = new SET<Point2D>();
    }

    public boolean isEmpty()// is the set empty?
    {
        return points.isEmpty();
    }

    public int size()// number of points in the set
    {
        return points.size();
    }

    public void insert(Point2D p)// add the point to the set (if it is not already in the set)
    {
        if (p == null){
            throw new NullPointerException("Point must not be null");
        }
        points.add(p);
    }

    public boolean contains(Point2D p)// does the set contain point p?
    {
        if (p == null){
            throw new NullPointerException("Point must not be null");
        }
        return points.contains(p);
    }

    public void draw()// draw all points to standard draw
    {
        for (Point2D curPt:points) {
            curPt.draw();
        }
    }

    public Iterable<Point2D> range(RectHV rect)// all points that are inside the rectangle
    {
        if (rect == null){
            throw new NullPointerException("rect should not be null");
        }
        List<Point2D> ptsInsideRect = new ArrayList<>();
        for (Point2D curPt:points) {
            if (rect.distanceSquaredTo(curPt) == 0){
                ptsInsideRect.add(curPt);
            }
        }
        return ptsInsideRect;
    }

    public Point2D nearest(Point2D p)// a nearest neighbor in the set to point p; null if the set is empty
    {
        if (p == null){
            throw new NullPointerException("Point must not be null");
        }

        double shortedDistance = Double.MAX_VALUE;
        Point2D nearestPoint = null;
        for (Point2D curPt:points) {
            double curDistance = p.distanceSquaredTo(curPt);
            if (curDistance < shortedDistance){
                shortedDistance = curDistance;
                nearestPoint = curPt;
            }
        }
        return nearestPoint;
    }

    public static void main(String[] args)// unit testing of the methods (optional)
    {
    }
}
