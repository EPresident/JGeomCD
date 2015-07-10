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
package org.altervista.prezisland.geometry.algorithms;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.LinkedList;
import org.altervista.prezisland.geometry.Geometry;
import org.altervista.prezisland.geometry.shapes.Polygon;

/**
 * Singleton class with algorithms to compute the Minkowski sums of polygons.
 *
 * @author EPresident <prez_enquiry@hotmail.com>
 */
public class MinkowskiSum {

    static final MinkowskiSum INSTANCE = new MinkowskiSum();

    private MinkowskiSum() {

    }

    public static Polygon minkowskiSumConvex(Polygon s1, Polygon s2) {
        return INSTANCE.minkowskiSumConvex_private(s1, s2);
    }

    private Polygon minkowskiSumConvex_private(Polygon s1, Polygon s2) {
        ArrayList<Point2D.Double> pts1 = new ArrayList(s1.getPoints()),
                pts2 = new ArrayList(s2.getPoints());
        LinkedList<Point2D.Double> pts12 = new LinkedList<>();

        minkowskiSumConvex_checkInput(pts1);
        minkowskiSumConvex_checkInput(pts2);
        for (Point2D.Double p : pts1) {
            System.out.print("(" + p.x + "," + p.y + ");");
        }
        System.out.println();
        for (Point2D.Double p : pts2) {
            System.out.print("(" + p.x + "," + p.y + ");");
        }
        System.out.println();

        /*
         Algorithm from "Computational Geometry, Algorithms and Applications",
         page 295, adapted.
         */
        pts1.add(pts1.get(0));
        pts1.add(pts1.get(1));
        pts2.add(pts2.get(0));
        pts2.add(pts2.get(1));
        int i = 0, j = 0;
        do {
            /*  System.out.println("-----------\ni:" + i + ",j:" + j);
             System.out.println("i:" + pts1.get(i) + "," + pts1.get(i + 1));
             System.out.println("j:" + pts2.get(j) + "," + pts2.get(j + 1));*/
            pts12.add(new Point2D.Double(pts1.get(i).x + pts2.get(j).x,
                    pts1.get(i).y + pts2.get(j).y));

            double angle1, angle2;
            if (i != pts1.size() - 2) {
                angle1 = normalizeAngle(Geometry.getAngle(pts1.get(i), pts1.get(i + 1)));
            } else {
                angle1 = 9;
            }
            if (j != pts2.size() - 2) {
                angle2 = normalizeAngle(Geometry.getAngle(pts2.get(j), pts2.get(j + 1)));
            } else {
                angle2 = 9;
            }

            // System.out.println("angleI: " + (angle1 * 360 / 2 / Math.PI) + ", angleJ= " + angle2 * 360 / 2 / Math.PI);
            if (angle1 < angle2) {
                i++;
            } else if (angle1 > angle2) {
                j++;
            } else {
                i++;
                j++;
            }

        } while (i != pts1.size() - 2 || j != pts2.size() - 2);

        /* for (Point2D.Double p : pts12) {
         System.out.print("(" + p.x + "," + p.y + ");");
         }
         System.out.println();*/
        return new Polygon(pts12);
    }

    private static void minkowskiSumConvex_checkInput(ArrayList<Point2D.Double> pts) {
        // Make sure the first vertex has minimum y
        // Due to Java Swing coordinates, Y is maximized (FIXME)
        double minX = Double.MAX_VALUE, maxY = 0, minY = Double.MAX_VALUE;
        int i = 0, index = -1;
        for (Point2D.Double p : pts) {
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
                pts.add(pts.get(0));
                pts.remove(0);
            }
        }

        /*    for (Point2D.Double p : pts) {
         System.out.print("(" + p.x + "," + p.y + ");");
         }
         System.out.println();*/
    }

    private static double normalizeAngle(double angle) {
        if (angle < 0) {
            // System.out.println("norm angle: " + angle + "(" + (angle * 360 / 2 / Math.PI) + ")");
            return 2 * Math.PI + angle;
        }
        return angle;
    }

    public static Polygon bruteMinkowskiSumConvex(Polygon s1, Polygon s2) {
        //return INSTANCE.bruteMinkowskiSumConvex_private(s1, s2);
        ArrayList<Point2D.Double> pts1 = new ArrayList<>(s1.getPoints()),
                pts2 = new ArrayList<>(s2.getPoints()), ptsn = new ArrayList<>();
        for (Point2D.Double p1 : pts1) {
            for (Point2D.Double p2 : pts2) {
                ptsn.add(new Point2D.Double(p1.x + p2.x, p1.y + p2.y));
            }
        }
        return new Polygon(ptsn);
    }

    /**
     * Utility class that represents a Polygon's edge, and stores its angle
     * relative to the x axis.
     */
    private class Edge implements Comparable<Edge> {

        Point2D.Double a, b;
        double angle;

        Edge(Point2D.Double u, Point2D.Double v) {
            a = u;
            b = v;
            angle = Math.atan2(b.y - a.y, b.x - a.x);
        }

        @Override
        public int compareTo(Edge o) {
            if (angle > o.angle) {
                return 1;
            }
            if (angle < o.angle) {
                return -1;
            }
            return 0;
        }

        @Override
        public String toString() {
            // return "("+angle+")";
            return "(" + a.x + "," + a.y + "->" + b.x + "," + b.y + "--" + angle + ")";
        }
    }

}
