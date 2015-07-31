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
package org.altervista.prezisland.geometry;

import org.altervista.prezisland.geometry.shapes.Polygon;
import java.awt.geom.Point2D;
import java.util.List;
import org.altervista.prezisland.geometry.algorithms.CollisionDetection;
import org.altervista.prezisland.geometry.algorithms.MinkowskiSum;
import org.altervista.prezisland.geometry.shapes.AABB;

/**
 *
 * @author EPresident <prez_enquiry@hotmail.com>
 */
public final class Geometry {

    private final GeomGUI gui;
    private static final Polygon RECTANGLE1 = new AABB(200, 200, 200, 100),
            SQUARE1 = new Polygon(new Point2D.Double[]{new Point2D.Double(0, 0),
                new Point2D.Double(100, 0), new Point2D.Double(100, 100),
                new Point2D.Double(0, 100)});

    ;

    private Geometry() {
        gui = new GeomGUI();
        gui.setVisible(true);

        // Convolution test
        Polygon p1 = new Polygon(new Point2D.Double[]{new Point2D.Double(0, 0),
            new Point2D.Double(100, 0), new Point2D.Double(100, 100),
            new Point2D.Double(0, 100)});
        p1.traslate(-1, 0);
        Polygon p2 = new Polygon(new Point2D.Double[]{
            new Point2D.Double(0, 0), new Point2D.Double(50, -10),
            new Point2D.Double(80, 70), new Point2D.Double(50, 100),
            new Point2D.Double(20, 60)});
        p2.traslate(100, 100);
        gui.addShape(p1);
        gui.addShape(p2);
        //     gui.addShape(MinkowskiSum.bruteMinkowskiSumConvex(p1, p2));
        //     gui.addShape(MinkowskiSum.minkowskiSumConvex(p1, p2));

        System.out.println("Result: " + CollisionDetection.getGuiPenAm(p1, p2, gui) + "-" + CollisionDetection.getGuiPenAm2(p1, p2, gui));
    }

    public static void main(String[] args) {
        new Geometry();
    }

    /**
     * -
     *
     * @param p1
     * @param p2
     * @return < 0 if p1<p2, 0 if p1==p2, > 0 else
     */
    public static int compareLexicographicallyX(Point2D.Double p1, Point2D.Double p2) {
        if (p1.x < p2.x) {
            return -1;
        } else if (p1.x > p2.x) {
            return 1;
        } else {
            // x1==x2
            if (p1.y < p2.y) {
                return -1;
            } else if (p1.y > p2.y) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    /**
     * -
     *
     * @param p1
     * @param p2
     * @return < 0 if p1<p2, 0 if p1==p2, > 0 else
     */
    public static int compareLexicographicallyY(Point2D.Double p1, Point2D.Double p2) {
        if (p1.y < p2.y) {
            return -1;
        } else if (p1.y > p2.y) {
            return 1;
        } else {
            // x1==x2
            if (p1.x < p2.x) {
                return -1;
            } else if (p1.x > p2.x) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    public static void sortLexicographicallyX(Point2D.Double[] pts) {
        // Bubble sort
        for (int i = 0; i < pts.length - 1; i++) {
            if (compareLexicographicallyX(pts[i], pts[i + 1]) > 0) {
                Point2D.Double temp = pts[i + 1];
                pts[i + 1] = pts[i];
                pts[i] = temp;
                int j = i - 1;
                while (j >= 0 && (compareLexicographicallyX(pts[j], pts[j + 1]) > 0)) {
                    temp = pts[j + 1];
                    pts[j + 1] = pts[j];
                    pts[j] = temp;
                    j--;
                }
            }
        }
    }

    public static void sortLexicographicallyX(List<Point2D.Double> pts) {
        // Bubble sort
        for (int i = 0; i < pts.size() - 1; i++) {
            if (compareLexicographicallyX(pts.get(i), pts.get(i + 1)) > 0) {
                Point2D.Double temp = pts.get(i + 1);
                pts.set(i + 1, pts.get(i));
                pts.set(i, temp);
                int j = i - 1;
                while (j >= 0 && (compareLexicographicallyX(pts.get(j), pts.get(j + 1)) > 0)) {
                    temp = pts.get(j + 1);
                    pts.set(j + 1, pts.get(j));
                    pts.set(j, temp);
                    j--;
                }
            }
            //System.out.println(pts.toString());
        }
    }

    public static boolean isLeftTurn(Point2D.Double p1, Point2D.Double p2, Point2D.Double p3) {
        /* 
         Alternative
         double crossProduct = (p1.x * (p2.y - p3.y) + p2.x * (p3.y - p1.y)
         + p3.x * (p1.y - p2.y));
         */
        double crossProduct = (p2.x - p1.x) * (p3.y - p1.y) - (p2.y - p1.y)
                * (p3.x - p1.x);
        return crossProduct > 0;
    }

    /**
     * Returns the direction of the segment p2-p3 relative to p1-p2. See
     * https://en.wikipedia.org/wiki/Cross_product#Computational_geometry
     *
     * @param p1 A Point
     * @param p2 A Point
     * @param p3 A Point
     * @return 1 for left turn, -1 for right turn, 0 for collinearity.
     */
    public static int getTurn(Point2D.Double p1, Point2D.Double p2, Point2D.Double p3) {
        double crossProduct = (p2.x - p1.x) * (p3.y - p1.y) - (p2.y - p1.y)
                * (p3.x - p1.x);
        if (crossProduct > 0) {
            return 1;
        } else if (crossProduct < 0) {
            return -1;
        } else {
            return 0;
        }
    }

    public static double getAngle(Point2D.Double p1, Point2D.Double p2) {
        return Math.atan2(p2.y - p1.y, p2.x - p1.x);
    }

    public static double getNormalizedAngle(Point2D.Double p1, Point2D.Double p2) {
        double angle = getAngle(p1, p2);
        if (angle < 0) {
            return 2 * Math.PI + angle;
        }
        return angle;
    }

    public static double normalizeAngle(double angle) {
        if (angle < 0) {
            return 2 * Math.PI + angle;
        }
        return angle;
    }

    public static double getLength(Point2D.Double p1, Point2D.Double p2) {
        return Math.sqrt(Math.pow(Math.abs(p1.x - p2.x), 2) + Math.pow(Math.abs(p1.y - p2.y), 2));
    }

}
