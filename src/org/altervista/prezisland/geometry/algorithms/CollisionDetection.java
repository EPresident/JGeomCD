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
import java.util.List;
import org.altervista.prezisland.geometry.Geometry;
import org.altervista.prezisland.geometry.Line;
import org.altervista.prezisland.geometry.Line.Position;
import static org.altervista.prezisland.geometry.Line.Position.*;
import org.altervista.prezisland.geometry.shapes.Polygon;

/**
 *
 * @author EPresident <prez_enquiry@hotmail.com>
 */
public class CollisionDetection {

    /*  private Point2D.Double getEndpoints(Polygon poly, boolean upper) {
     // Minimum Y point must be first
     ArrayList<Point2D.Double> pts = poly.getPoints();
     double maxY = pts.get(0).y;
     int l = 0, r = pts.size() - 1, m = -1;
     do {
            
     } while (l-r>0);
     return null;
     }*/
    public double getPenetrationAmount(Polygon P1, Polygon P2, int pMin, int pMax, int qMin, int qMax) {
        Polygon P = new Polygon(P1.getPoints()), Q = new Polygon(P2.getPoints());
        // Reference point for P
        Point2D.Double x = P.getPoints().get(0);
        // Reference point for Q
        Point2D.Double y = Q.getPoints().get(0);

        // Normalize polygons to the origin
        P.traslate(-x.x, -x.y);
        Q.traslate(-y.x, -y.y);

        /* ---------------------------
         Algorithm begins here
         ---------------------------*/
        // Left shadow of P
        List<Point2D.Double> A = P.getPoints().subList(pMin, pMax + 1);
        // Right shadow of Q (inverted)
        List<Point2D.Double> B = Q.getPoints().subList(qMax, Q.getPointsNumber());
        B.addAll(B.subList(0, qMin + 1));
        // z = y - x   <- the point to test the shadows' convolution against
        Point2D.Double z = new Point2D.Double(y.x - x.x, y.y - x.y);

        return 0;
    }

    /**
     * Given two left shadows A and B, discriminate w agains A*B Time O(log n)
     * See "Ruler, Compass and Computer" (Guibas & Stolfi)
     *
     * @param A left shadow of a convex polygon P
     * @param B left shadow (reversed right shadow of P)
     * @param w point
     * @return pen amount
     */
    public double guibasStolfi(Polygon A, Polygon B, Point2D.Double w) {
        // endpoints
        Point2D.Double a1 = A.getPoints().get(0),
                a2 = A.getPoints().get(A.getPoints().size() - 1),
                b1 = A.getPoints().get(0),
                b2 = A.getPoints().get(A.getPoints().size() - 1);
        // Loop until a shadow is reduced to a single vertex
        while (A.getPointsNumber() > 1 && B.getPointsNumber() > 1) {
            // Median point indexes
            int i = A.getPoints().size() / 2, j = B.getPoints().size() / 2;
            // Check if the segments starting from i and j are in slope order
            if (Geometry.getNormalizedAngle(A.getPoints().get(i), A.getPoints().get(i + 1))
                    < Geometry.getNormalizedAngle(B.getPoints().get(j), B.getPoints().get(j + 1))) {
                int k = i;
                i = j;
                j = k;
                Polygon C = A;
                A = B;
                B = C;
            }
            // Now assuming f and g are in slope order
            // Calculate the displacement of f in the chain D (Al*Bl - f - g - Ah*Bh)
            double f1X = A.getPoints().get(i).x + B.getPoints().get(j).x,
                    f1Y = A.getPoints().get(i).y + B.getPoints().get(j).y;
            double f2X = f1X + A.getPoints().get(i + 1).x,
                    f2Y = f1Y + A.getPoints().get(i + 1).y;
            // g1 == f2
            double g2X = f2X + B.getPoints().get(j + 1).x,
                    g2Y = f2Y + B.getPoints().get(j + 1).y;
            Line lineF = new Line(f1X, f1Y, f2X, f2Y),
                    lineG = new Line(f2X, f2Y, g2X, g2Y);
            Position testF = lineF.testAgainst(w),
                    testG = lineG.testAgainst(w);
            if (w.y > g2Y
                    || (testF == RIGHT && testG == RIGHT && w.y > f2Y)) {
                // ABOVE
                // Drop Al and f
                A = new Polygon(A.getPoints().subList(i + 1, A.getPoints().size()));
            } else if (w.y < f1Y
                    || (testF == RIGHT && testG == RIGHT && w.y < f2Y)) {
                // BELOW
                // Drop Bh and g
                B = new Polygon(B.getPoints().subList(0, j + 1));
            } else if (testF == RIGHT && testG == RIGHT && w.y == f2Y) {
                // IN BETWEEN
                // below/above sans g/f ?
                B = new Polygon(B.getPoints().subList(0, j + 2));
                System.out.println("Warning: in between.");
            } else if ((testF == LEFT || testF == COLLIDES)
                    && (testG == LEFT || testG == COLLIDES)) {
                // LEFT
                return 1;
            } else {
                // ???
            }

        }
        // End loop
        // One shadow is reduced to one vertex v, check the other one against w-v
        if (A.getPointsNumber() == 1) {
            w.setLocation(w.x - A.getPoints().get(0).x, w.y - A.getPoints().get(0).y);
        } else if (B.getPointsNumber() == 1) {
            w.setLocation(w.x - B.getPoints().get(0).x, w.y - B.getPoints().get(0).y);
            A = B;
        } else {
            throw new RuntimeException("Impossible? Shadows not reduced to one vertex.");
        }

        // Binary search in the remaining shadow
        int i = A.getPointsNumber() / 2;
        while (A.getPointsNumber() >= 2) {
            Point2D.Double e1 = A.getPoints().get(i), e2 = A.getPoints().get(i + 1);
            Line l = new Line(e1, e2);
            Position pos = l.testAgainst(w);
            if (pos == LEFT || pos == COLLIDES) {
                if (w.y >= e1.y && w.y <= e2.y) {
                    // w lies inside
                    return 1;
                } else if (w.y < e1.y) {
                    // w is below e1
                    A = new Polygon(A.getPoints().subList(0, i));
                    i = A.getPointsNumber() / 2;
                } else {
                    // w is above e2
                    A = new Polygon(A.getPoints().subList(i, A.getPointsNumber()));
                    i = A.getPointsNumber() / 2;
                }
            } else {
                // w is outside
                return 0;
            }

        }

        return 0;
    }

}
