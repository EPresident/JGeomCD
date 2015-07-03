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

        /* 
         Build Edge arrays:
         We need to calculate the angle ( relative to the x axis ) for each
         edge.
         */
        Edge[] E = new Edge[pts1.size() + pts2.size()];
        for (int i = 0; i < pts1.size(); i++) {
            E[i] = new Edge(pts1.get(i), pts1.get((i + 1) % pts1.size()));
        }
        for (int i = 0; i < pts2.size(); i++) {
            E[pts1.size() + i] = new Edge(pts2.get(i), pts2.get((i + 1) % pts2.size()));
        }
        Sorting.mergeSort(E);
        for (Edge e : E) {
            System.out.print(e.angle + ",");
        }
        System.out.println("");
        LinkedList<Point2D.Double> pts = new LinkedList<>();
        for (Edge e : E) {
            System.out.print(e + ",");
            pts.add(e.a);
            pts.add(e.b);
        }
        System.out.println("\n");
        return new Polygon(pts);
    }

    public static Polygon bruteMinkowskiSumConvex(Polygon s1, Polygon s2) {
        //return INSTANCE.bruteMinkowskiSumConvex_private(s1, s2);
        ArrayList<Point2D.Double> pts1 = new ArrayList<>(s1.getPoints()),
                pts2 = new ArrayList<>(s2.getPoints()), ptsn=new ArrayList<>();
        for (Point2D.Double p1 : pts1) {
            for (Point2D.Double p2 : pts2) {
                ptsn.add(new Point2D.Double(p1.x+p2.x, p1.y+p2.y));
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
