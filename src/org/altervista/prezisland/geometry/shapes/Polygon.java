/*
 * The MIT License
 *
 * Copyright 2015 EPresident <prez_enquiry@hotmail.com>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.altervista.prezisland.geometry.shapes;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.altervista.prezisland.geometry.CartesianVector;
import org.altervista.prezisland.geometry.Geometry;

/**
 * Polygon represented by an array of points, which are linked by edges in a
 * counter-clockwise order; edges are not explicitly stored.
 *
 * @author EPresident <prez_enquiry@hotmail.com>
 */
public class Polygon {

    protected final ArrayList<Point2D.Double> points;
    protected final Point2D.Double center;
    protected CartesianVector[] separatingAxes;

    public Polygon(Point2D.Double[] pts) {
        points = new ArrayList<>();
        for (Point2D.Double p : pts) {
            points.add(new Point2D.Double(p.x, p.y));
        }
        center = calculateCenter();
    }

    public Polygon(Collection<Point2D.Double> pts) {
        points = new ArrayList<>();
        for (Point2D.Double p : pts) {
            points.add(new Point2D.Double(p.x, p.y));
        }
        center = calculateCenter();
    }

    public Polygon(Polygon p) {
        points = new ArrayList<>();
        for (Point2D.Double pp : p.getPoints()) {
            points.add(new Point2D.Double(pp.x, pp.y));
        }
        center = calculateCenter();
    }

    protected Polygon(Collection<Point2D.Double> pts, Point2D.Double ctr) {
        points = new ArrayList<>(pts);
        center = ctr;
    }

    protected Polygon(Point2D.Double[] pts, Point2D.Double ctr) {
        points = new ArrayList<>(Arrays.asList(pts));
        center = ctr;
        /*  System.out.print("new shape : ");
         for(Point2D p : points){
         System.out.print(p+";");
         }*/
    }

    /**
     * This method orders the polygon's points so that the first has the minimum
     * y
     */
    public void normalizePointOrder() {
        // Make sure the first vertex has minimum y
        double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE;
        int i = 0, index = -1;
        for (Point2D.Double p : points) {
            // if (p.y > maxY || (p.y == maxY && p.x < minX)) {
            if (p.y < minY || (p.y == minY && p.x < minX)) {
                minX = p.x;
                minY = p.y;
                index = i;
            }
            i++;
        }
        if (index != 0) {
            /*  System.out.println("original:");
             for (Point2D.Double p : pts) {
             System.out.print("(" + p.x + "," + p.y + ");");
             }
             System.out.println();*/
            // shift vertices
            for (int j = 0; j < index; j++) {
                points.add(points.get(0));
                points.remove(0);
            }
        }

        /*    for (Point2D.Double p : pts) {
         System.out.print("(" + p.x + "," + p.y + ");");
         }
         System.out.println();*/
    }

    /**
     * Calculates the center of this generic Shape by enclosing it in a bounding
     * rectangle.
     *
     * @return The center of this Shape
     */
    protected Point2D.Double calculateCenter() {
        double maxX = 0, maxY = 0, minX = 999999999, minY = 999999999;
        for (Point2D.Double p : points) {
            if (p.x > maxX) {
                maxX = p.x;
            }
            if (p.x < minX) {
                minX = p.x;
            }
            if (p.y > maxY) {
                maxY = p.y;
            }
            if (p.y < minY) {
                maxY = p.y;
            }
        }
        double w = maxX - minX, h = maxY - minY;
        //    System.out.println("center: " + new Point2D.Double(minX + w / 2, minY + h / 2));
        return new Point2D.Double(minX + w / 2, minY + h / 2);
    }

    public Point2D.Double getCenter() {
        return center;
    }

    /**
     * @return The list of points composing this shape.
     */
    public ArrayList<Point2D.Double> getPoints() {
        return points;
    }

    /**
     * @return The number of points composing this shape.
     */
    public int getPointsNumber() {
        return points.size();
    }

    /**
     * Calculate the separating axes for this shape, <i>if</i> they aren't
     * defined yet.
     *
     * @return An Array of separating (unit) axes for this shape, i.e. the axes
     * perpendicular to each edge.
     */
    public CartesianVector[] getSeparatingAxes() {
        if (separatingAxes == null) {
            separatingAxes = new CartesianVector[points.size() - 1];
            // Get direction vector for each edge
            for (int i = 0; i < points.size() - 1; i++) {
                Point2D p1 = points.get(i), p2 = points.get(i + 1);
                separatingAxes[i] = new CartesianVector(p1, p2).getRighthandNormal().makeUnit();
            }
        }
        return separatingAxes;
    }

    public double getDistance(Polygon s) {
        return this.center.distance(s.center);
    }

    /**
     * Traslate the Shape a set distance, moving all of its points.
     *
     * @param dx Traslation on the X axis.
     * @param dy Traslation on the Y axis.
     */
    public void traslate(double dx, double dy) {
        for (Point2D p : points) {
            p.setLocation(p.getX() + dx, p.getY() + dy);
        }
        center.setLocation(center.x + dx, center.y + dy);
        // Axes need not updating
    }

    /**
     * Add a Point to the Shape.
     *
     * @param p
     */
    public void addPoint(Point2D.Double p) {
        //points.add(p);
        throw new UnsupportedOperationException("NYI");
    }

    public AABB getBoundingRectangle() {
        throw new UnsupportedOperationException("NYI");
    }

    public CartesianVector[] getEdges() {
        CartesianVector[] edges = new CartesianVector[points.size() - 1];
        for (int i = 0; i < edges.length; i++) {
            edges[i] = new CartesianVector(points.get(i).x, points.get(i).y,
                    points.get(i + 1).x, points.get(i + 1).y);
        }
        return edges;
    }

    public boolean isConvex() {
        for (int i = 1; i < points.size() - 1; i++) {
            if (!Geometry.isLeftTurn(points.get(i - 1), points.get(i), points.get(i + 1))) {
                return false;
            }
        }
        return true;
    }
}
