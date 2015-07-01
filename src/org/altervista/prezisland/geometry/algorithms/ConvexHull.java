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
import java.util.List;
import org.altervista.prezisland.geometry.Geometry;

/**
 *
 * @author EPresident <prez_enquiry@hotmail.com>
 */
public class ConvexHull {

    public static ArrayList<Point2D.Double> grahamConvexHull(List<Point2D.Double> pts) {
        if (pts.size() < 3) {
            return new ArrayList<>();
        }
        Geometry.sortLexicographicallyX(pts);
        ArrayList<Point2D.Double> hull = new ArrayList<>();

        lowerHull(pts, hull);
        System.out.println("lowh: "+hull.toString());
        upperHull(pts, hull);
        return hull;
    }

    private static void lowerHull(List<Point2D.Double> pts, ArrayList<Point2D.Double> hull) {
        int n = pts.size();

        hull.add(pts.get(0));
        hull.add(pts.get(1));

        for (int i = 2; i < n; i++) {

            int j = hull.size() - 1;

            while ((j > 0) && !Geometry.isLeftTurn(hull.get(j - 1), hull.get(j), pts.get(i))) {
                hull.remove(j);
                j = j - 1;
            }
            hull.add(pts.get(i));
        }
    }

    private static void upperHull(List<Point2D.Double> pts, ArrayList<Point2D.Double> hull) {
        int n = pts.size(), k = hull.size() - 1;

        hull.add(pts.get(n - 2));

        for (int i = n - 3; i >= 0; i--) {

            int j = hull.size() - 1;

            while ((j > k) && !Geometry.isLeftTurn(hull.get(j - 1), hull.get(j), pts.get(i))) {
                hull.remove(j);
                j = j - 1;
            }
            hull.add(pts.get(i));
        }
        hull.remove(hull.size()-1);
    }
}
