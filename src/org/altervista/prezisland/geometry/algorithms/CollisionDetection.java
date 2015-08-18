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
import java.util.List;
import org.altervista.prezisland.geometry.GeomGUI;
import org.altervista.prezisland.geometry.Geometry;
import org.altervista.prezisland.geometry.Line;
import org.altervista.prezisland.geometry.Line.Position;
import static org.altervista.prezisland.geometry.Line.Position.*;
import org.altervista.prezisland.geometry.Segment;
import org.altervista.prezisland.geometry.shapes.Polygon;

/**
 *
 * @author EPresident <prez_enquiry@hotmail.com>
 */
public class CollisionDetection {

    final static double NEG_INFINITY = -100000;

    public static double getPenetrationDepth(final Polygon P1, final Polygon P2,
            Line d, boolean orient, GeomGUI gui) {
        // Duplicate polygons to avoid conflicts
        Polygon P = new Polygon(P1), Q = new Polygon(P2);
        // Reference point for P
        Point2D.Double x = P.getPoints().get(0);
        x = new Point2D.Double(x.x, x.y);
        // Reference point for Q
        Point2D.Double y = Q.getPoints().get(0);
        y = new Point2D.Double(y.x, y.y);

        // Normalize polygons to the origin
        P.traslate(-x.x, -x.y);
        Q.traslate(-y.x, -y.y);
        /*  gui.addShape(P);
         gui.addShape(Q);
         throw new RuntimeException("stahp");*/

        int i = -1, j = -1;
        double max = 0, min = Double.MAX_VALUE;
        for (int k = 0; k < P1.getPointsNumber(); k++) {
            Point2D.Double p = P1.getPoints().get(k);
            if (p.y > max) {
                max = p.y;
                i = k;
            }
            if (p.y < min) {
                min = p.y;
                j = k;
            }
        }
        int pMin = j, pMax = i;

        i = -1;
        j = -1;
        max = 0;
        min = Double.MAX_VALUE;
        for (int k = 0; k < P2.getPointsNumber(); k++) {
            Point2D.Double p = P2.getPoints().get(k);
            if (p.y > max) {
                max = p.y;
                i = k;
            }
            if (p.y < min) {
                min = p.y;
                j = k;
            }
        }
        int qMin = j, qMax = i;

        // Left shadow of P
        Polygon lsP = getLeftShadow(P, pMax, pMin);
        // Right shadow of Q (inverted)
        Polygon rsiQ = getRightShadowInv(Q, qMax, qMin);
        // Left shadow of Q
        Polygon lsQ = getLeftShadow(Q, qMax, qMin);
        // Right shadow of P (inverted)
        Polygon rsiP = getRightShadowInv(P, pMax, pMin);
        // w = y - x   <- the point to test the shadows' convolution against
        // y = left shadow reference point
        // x = right shadow reference point
        Point2D.Double w1 = new Point2D.Double(y.x - x.x, y.y - x.y);
        Point2D.Double w2 = new Point2D.Double(x.x - y.x, x.y - y.y);

        Line d1 = new Line(d), d2 = new Line(d);
        d1.traslate(w1);
        d2.traslate(w2);

        double pen1 = penDepth(lsP, rsiQ, w1, d1, orient, gui);
        if (pen1 == 0) {
            return penDepth(lsQ, rsiP, w2, d2, !orient, gui);
        } else {
            return pen1;
        }
    }

    public static Point2D.Double getPenetrationVector(final Polygon P1, final Polygon P2,
            Line d, boolean orient, GeomGUI gui) {
        // Duplicate polygons to avoid conflicts
        Polygon P = new Polygon(P1), Q = new Polygon(P2);
        // Reference point for P
        Point2D.Double x = P.getPoints().get(0);
        x = new Point2D.Double(x.x, x.y);
        // Reference point for Q
        Point2D.Double y = Q.getPoints().get(0);
        y = new Point2D.Double(y.x, y.y);

        // Normalize polygons to the origin
        P.traslate(-x.x, -x.y);
        Q.traslate(-y.x, -y.y);
        /*  gui.addShape(P);
         gui.addShape(Q);
         throw new RuntimeException("stahp");*/

        int i = -1, j = -1;
        double max = 0, min = Double.MAX_VALUE;
        for (int k = 0; k < P1.getPointsNumber(); k++) {
            Point2D.Double p = P1.getPoints().get(k);
            if (p.y > max) {
                max = p.y;
                i = k;
            }
            if (p.y < min) {
                min = p.y;
                j = k;
            }
        }
        int pMin = j, pMax = i;

        i = -1;
        j = -1;
        max = 0;
        min = Double.MAX_VALUE;
        for (int k = 0; k < P2.getPointsNumber(); k++) {
            Point2D.Double p = P2.getPoints().get(k);
            if (p.y > max) {
                max = p.y;
                i = k;
            }
            if (p.y < min) {
                min = p.y;
                j = k;
            }
        }
        int qMin = j, qMax = i;

        // Left shadow of P
        Polygon lsP = getLeftShadow(P, pMax, pMin);
        // Right shadow of Q (inverted)
        Polygon rsiQ = getRightShadowInv(Q, qMax, qMin);
        // Left shadow of Q
        Polygon lsQ = getLeftShadow(Q, qMax, qMin);
        // Right shadow of P (inverted)
        Polygon rsiP = getRightShadowInv(P, pMax, pMin);
        // w = y - x   <- the point to test the shadows' convolution against
        // y = left shadow reference point
        // x = right shadow reference point
        Point2D.Double w1 = new Point2D.Double(y.x - x.x, y.y - x.y);
        Point2D.Double w2 = new Point2D.Double(x.x - y.x, x.y - y.y);

        // Test : horizontal direction
        Line d1 = new Line(d), d2 = new Line(d);
        d1.traslate(w1);
        d2.traslate(w2);

        System.out.println("\n\n --- TEST 1 --- \n\n");
        Point2D.Double pen1 = penVect(lsP, rsiQ, w1, d1, orient, gui);
        if (pen1.x == 0 && pen1.y == 0) {
            //  gui.clearAll();
            System.out.println("\n\n --- TEST 2 --- \n\n");
            return penVect(lsQ, rsiP, w2, d2, !orient, gui);
        } else {
            return pen1;
        }
    }

    public static void step(GeomGUI gui) {
        System.out.print("Stepping... ");
        while (!gui.step()) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
                System.out.println(ex.getMessage());
            }
        }
        System.out.println("Stepped.");
    }

    public static Point2D.Double getPenetrationVectorStep(final Polygon P1, final Polygon P2,
            Line d, boolean orient, GeomGUI gui) {

        step(gui);
        // Duplicate polygons to avoid conflicts
        Polygon P = new Polygon(P1), Q = new Polygon(P2);
        // Reference point for P
        Point2D.Double x = P.getPoints().get(0);
        x = new Point2D.Double(x.x, x.y);
        // Reference point for Q
        Point2D.Double y = Q.getPoints().get(0);
        y = new Point2D.Double(y.x, y.y);

        // Normalize polygons to the origin
        P.traslate(-x.x, -x.y);
        Q.traslate(-y.x, -y.y);
        gui.pushStack();
        gui.addShape(P);
        gui.addShape(Q);

        step(gui);
        gui.popStack();

        int i = -1, j = -1;
        double max = 0, min = Double.MAX_VALUE;
        for (int k = 0; k < P1.getPointsNumber(); k++) {
            Point2D.Double p = P1.getPoints().get(k);
            if (p.y > max) {
                max = p.y;
                i = k;
            }
            if (p.y < min) {
                min = p.y;
                j = k;
            }
        }
        int pMin = j, pMax = i;

        i = -1;
        j = -1;
        max = 0;
        min = Double.MAX_VALUE;
        for (int k = 0; k < P2.getPointsNumber(); k++) {
            Point2D.Double p = P2.getPoints().get(k);
            if (p.y > max) {
                max = p.y;
                i = k;
            }
            if (p.y < min) {
                min = p.y;
                j = k;
            }
        }
        int qMin = j, qMax = i;

        // Left shadow of P
        Polygon lsP = getLeftShadow(P, pMax, pMin);
        // Right shadow of Q (inverted)
        Polygon rsiQ = getRightShadowInv(Q, qMax, qMin);
        // Left shadow of Q
        Polygon lsQ = getLeftShadow(Q, qMax, qMin);
        // Right shadow of P (inverted)
        Polygon rsiP = getRightShadowInv(P, pMax, pMin);
        // w = y - x   <- the point to test the shadows' convolution against
        // y = left shadow reference point
        // x = right shadow reference point
        Point2D.Double w1 = new Point2D.Double(y.x - x.x, y.y - x.y);
        Point2D.Double w2 = new Point2D.Double(x.x - y.x, x.y - y.y);

        // Test : horizontal direction
        Line d1 = new Line(d), d2 = new Line(d);
        d1.traslate(w1);
        d2.traslate(w2);

        System.out.println("\n\n --- TEST 1 --- \n\n");
        Point2D.Double pen1 = penVect(lsP, rsiQ, w1, d1, orient, gui);
        if (pen1.x == 0 && pen1.y == 0) {
            //  gui.clearAll();
            System.out.println("\n\n --- TEST 2 --- \n\n");
            return penVect(lsQ, rsiP, w2, d2, !orient, gui);
        } else {
            return pen1;
        }
    }

    /**
     * Algorithm to calculate the directional penetration depth between two
     * convex polygons, based on an algorithm by Guibas and Stolfi.
     *
     * @param A Left shadow of a Polygon
     * @param B Inverted right shadow of a Polygon
     * @param w Position vector (difference between A and B's position vectors)
     * @param d Direction of penetration ( as a Line )
     * @param orient Orientation of the direction ray d
     * @param gui GeomGui
     * @return Point representing the penetration vector
     */
    public static Point2D.Double penVectStep(Polygon A, Polygon B, Point2D.Double w, Line d,
            boolean orient, GeomGUI gui) {
        /*System.out.println("A: " + A.getPoints());
         System.out.println("B: " + B.getPoints());
         System.out.println("w: " + w);
         System.out.println("Direction: " + d);*/
        //    gui.clearAll();
        //  gui.addLine(d);
        // gui.addShape(A);
        // gui.addShape(B);
        //   gui.addShape(MinkowskiSum.minkowskiSumConvex(A, B));
        //   gui.addVector(w);
        //throw new RuntimeException("Stop");

        // Loop until a shadow is reduced to a single vertex
        while (A.getPointsNumber() > 1 && B.getPointsNumber() > 1) {
            // Median point indexes
            int i = (A.getPoints().size() - 1) / 2,
                    j = (B.getPoints().size() - 1) / 2;
            // Make sure not to go out of bounds
            if (i == A.getPointsNumber() - 1) {
                i--;
            }
            if (j == B.getPointsNumber() - 1) {
                j--;
            }
            System.out.println("\nSizes: " + A.getPointsNumber() + "," + B.getPointsNumber());
            // Check if the segments starting from i and j are in slope order
            if (Geometry.getNormalizedAngle(A.getPoints().get(i), A.getPoints().get(i + 1))
                    > Geometry.getNormalizedAngle(B.getPoints().get(j), B.getPoints().get(j + 1))) {
                int k = i;
                i = j;
                j = k;
                Polygon C = A;
                A = B;
                B = C;
            }
            // Now assuming f and g are in slope order
            // Calculate the displacement of f in the chain D: (Al*Bl -> f -> g -> Ah*Bh)
            double f1X = A.getPoints().get(i).x + B.getPoints().get(j).x,
                    f1Y = A.getPoints().get(i).y + B.getPoints().get(j).y;

            double f2X = f1X + (A.getPoints().get(i + 1).x - A.getPoints().get(i).x),
                    f2Y = f1Y + (A.getPoints().get(i + 1).y - A.getPoints().get(i).y);

            // g1 == f2
            double g2X = f2X + (B.getPoints().get(j + 1).x - B.getPoints().get(j).x),
                    g2Y = f2Y + (B.getPoints().get(j + 1).y - B.getPoints().get(j).y);

            Line lineF = new Line(f1X, f1Y, f2X, f2Y),
                    lineG = new Line(f2X, f2Y, g2X, g2Y);
            Segment segF = new Segment(f1X, f1Y, f2X, f2Y),
                    segG = new Segment(f2X, f2Y, g2X, g2Y);

            System.out.println("Xs: " + f1X + "," + f2X + "," + g2X);
            System.out.println("Ys: " + f1Y + "," + f2Y + "," + g2Y);
            /*   gui.clearLines();
             gui.addLine(lineF);
             gui.addLine(lineG);*/
            /*
             ------------------------------------------------------------------
             Check where the intersection lies
             ------------------------------------------------------------------
             */
            Point2D.Double testF = lineF.testIntersection(d),
                    testG = lineG.testIntersection(d);
            // testIntersection() returns a Point or null

            // Got at least one intersection
            if (testF != null) {
                /*
                 Check the position of the intersection point relative to edge f
                 Interesting cases:
                 - testF is on f
                 - testF is below f
                 */
                Segment.Position posF = (Segment.Position) segF.testAgainst(testF);
                System.out.println("posF: " + posF);
                if ((!d.isVertical() && ((orient && testF.x >= w.x) || (!orient && testF.x <= w.x)))
                        || (d.isVertical() && ((orient && testF.y >= w.y) || (!orient && testF.y <= w.y)))) {
                    // Intersection is valid
                    if (posF == Segment.Position.COLLIDES) {
                        // GOTCHA!
                        Point2D.Double out = new Point2D.Double(Math.abs(testF.x - w.x), Math.abs(testF.y - w.y));
                        System.out.println("GOTCHA! " + out);
                        return out;
                    } else if (posF == Segment.Position.COLLINEAR_BELOW) {
                        // Intersection below f: g and Bh can be removed
                        System.out.println("BELOW");
                        B = new Polygon(B.getPoints().subList(0, j + 1));
                        System.out.println("B shortened to " + B.getPointsNumber());
                        continue;
                    }
                } else {
                    // Intersection generated from the line, not the ray: discard it.
                    System.out.println("Intersection for f invalid.");
                }
            }
            if (testG != null) {
                /*
                 Check the position of the intersection point relative to edge g
                 Interesting cases:
                 - testG is on g
                 - testG is above g
                 */
                Segment.Position posG = (Segment.Position) segG.testAgainst(testG);
                System.out.println("posG: " + posG);
                if ((!d.isVertical() && ((orient && testG.x >= w.x) || (!orient && testG.x <= w.x)))
                        || (d.isVertical() && ((orient && testG.y >= w.y) || (!orient && testG.y <= w.y)))) {
                    // Intersection is valid
                    if (posG == Segment.Position.COLLIDES) {
                        // GOTCHA!
                        Point2D.Double out = new Point2D.Double(Math.abs(testG.x - w.x), Math.abs(testG.y - w.y));
                        System.out.println("GOTCHA! " + out);
                        return out;
                    } else if (posG == Segment.Position.COLLINEAR_ABOVE) {
                        // Intersection above g: f and Al can be removed
                        System.out.println("ABOVE");
                        // Drop Al and f
                        A = new Polygon(A.getPoints().subList(i + 1, A.getPoints().size()));
                        System.out.println("A shortened to " + A.getPointsNumber());
                        continue;
                    }
                } else {
                    // Intersection generated from the line, not the ray: discard it.
                    System.out.println("Intersection for g invalid.");
                }
            }

            // No valid intersection
            if (d.isVertical()) {
                if (orient) {
                    System.out.println("ABOVE_VERT");
                    // Drop Al and f
                    A = new Polygon(A.getPoints().subList(i + 1, A.getPoints().size()));
                    System.out.println("A shortened to " + A.getPointsNumber());
                    continue;
                } else {
                    System.out.println("BELOW_VERT");
                    B = new Polygon(B.getPoints().subList(0, j + 1));
                    System.out.println("B shortened to " + B.getPointsNumber());
                    continue;
                }
            }

            //throw new RuntimeException("No valid intersection!");
            System.out.println("No valid intersection!");
            return new Point2D.Double(0, 0);

        }// End while loop

        // None of the above cases triggered
        System.out.println("???2");
        //throw new RuntimeException("???2");
        return new Point2D.Double(0, 0);
    }

    public static double getGuiPenAm(Polygon P1, Polygon P2, GeomGUI gui) {
        Polygon P = new Polygon(P1), Q = new Polygon(P2);
        // Reference point for P
        Point2D.Double x = P.getPoints().get(0);
        x = new Point2D.Double(x.x, x.y);
        // Reference point for Q
        Point2D.Double y = Q.getPoints().get(0);
        y = new Point2D.Double(y.x, y.y);

        /*gui.addShape(P);
         gui.addShape(Q);
         gui.addVector(x);
         gui.addVector(y);
         throw new RuntimeException();*/
        // Normalize polygons to the origin
        P.traslate(-x.x, -x.y);
        Q.traslate(-y.x, -y.y);

        /*  gui.addShape(P);
         gui.addShape(Q);*/
        int i = -1, j = -1;
        double max = 0, min = Double.MAX_VALUE;
        for (int k = 0; k < P1.getPointsNumber(); k++) {
            Point2D.Double p = P1.getPoints().get(k);
            if (p.y > max) {
                max = p.y;
                i = k;
            }
            if (p.y < min) {
                min = p.y;
                j = k;
            }
        }
        int pMin = j, pMax = i;

        i = -1;
        j = -1;
        max = 0;
        min = Double.MAX_VALUE;
        for (int k = 0; k < P2.getPointsNumber(); k++) {
            Point2D.Double p = P2.getPoints().get(k);
            if (p.y > max) {
                max = p.y;
                i = k;
            }
            if (p.y < min) {
                min = p.y;
                j = k;
            }
        }
        int qMin = j, qMax = i;

        // Left shadow of P
        List<Point2D.Double> A = P.getPoints().subList(pMin, pMax + 1);
        // Right shadow of Q (inverted)
        List<Point2D.Double> B = new ArrayList<>();
        // System.out.println("" + B + qMin + qMax);
        for (int k = qMax; k < Q.getPointsNumber(); k++) {
            B.add(Q.getPoints().get(k));
        }
        for (int k = 0; k <= qMin; k++) {
            B.add(Q.getPoints().get(k));
        }
        for (Point2D.Double p : B) {
            p.setLocation(-p.x, -p.y);
        }
        // z = y - x   <- the point to test the shadows' convolution against
        Point2D.Double z = new Point2D.Double(y.x - x.x, y.y - x.y);
        //Point2D.Double z = new Point2D.Double(x.x - y.x, x.y - y.y);

        /*gui.addShape(new Polygon(A));
         gui.addShape(new Polygon(B));*/
        // gui.addVector(z);
        //gui.addShape(MinkowskiSum.minkowskiSumConvex(new Polygon(A), new Polygon(B)));
        return guibasStolfi(new Polygon(A), new Polygon(B), z, gui);
    }

    public static double getGuiPenAm2(Polygon P1, Polygon P2, GeomGUI gui) {
        System.out.println("\n\n////////////\nPART TWO\n//////////////////////\n\n");
        Polygon P = new Polygon(P1), Q = new Polygon(P2);
        // Reference point for P
        Point2D.Double x = P.getPoints().get(0);
        x = new Point2D.Double(x.x, x.y);
        // Reference point for Q
        Point2D.Double y = Q.getPoints().get(0);
        y = new Point2D.Double(y.x, y.y);

        /*gui.addShape(P);
         gui.addShape(Q);
         gui.addVector(x);
         gui.addVector(y);
         throw new RuntimeException();*/
        // Normalize polygons to the origin
        P.traslate(-x.x, -x.y);
        Q.traslate(-y.x, -y.y);

        /*  gui.addShape(P);
         gui.addShape(Q);*/
        int i = -1, j = -1;
        double max = 0, min = Double.MAX_VALUE;
        for (int k = 0; k < P1.getPointsNumber(); k++) {
            Point2D.Double p = P1.getPoints().get(k);
            if (p.y > max) {
                max = p.y;
                i = k;
            }
            if (p.y < min) {
                min = p.y;
                j = k;
            }
        }
        int pMin = j, pMax = i;

        i = -1;
        j = -1;
        max = 0;
        min = Double.MAX_VALUE;
        for (int k = 0; k < P2.getPointsNumber(); k++) {
            Point2D.Double p = P2.getPoints().get(k);
            if (p.y > max) {
                max = p.y;
                i = k;
            }
            if (p.y < min) {
                min = p.y;
                j = k;
            }
        }
        int qMin = j, qMax = i;

        // Left shadow of Q
        List<Point2D.Double> B = Q.getPoints().subList(qMin, qMax + 1);
        // Right shadow of P (inverted)
        List<Point2D.Double> A = new ArrayList<>();
        // System.out.println("" + B + qMin + qMax);
        for (int k = pMax; k < P.getPointsNumber(); k++) {
            A.add(P.getPoints().get(k));
        }
        for (int k = 0; k <= pMin; k++) {
            A.add(P.getPoints().get(k));
        }
        for (Point2D.Double p : A) {
            p.setLocation(-p.x, -p.y);
        }
        // z = y - x   <- the point to test the shadows' convolution against
        //Point2D.Double z = new Point2D.Double(y.x - x.x, y.y - x.y);
        //Point2D.Double z = new Point2D.Double(Math.abs(y.x - x.x), Math.abs(y.y - x.y));
        Point2D.Double z = new Point2D.Double(x.x - y.x, x.y - y.y);

        /* gui.addShape(new Polygon(A));
         gui.addShape(new Polygon(B));*/
        //gui.addVector(z);
        // gui.addShape(MinkowskiSum.minkowskiSumConvex(new Polygon(A), new Polygon(B)));
        // throw new RuntimeException();
        return guibasStolfi(new Polygon(A), new Polygon(B), z, gui);
    }

    /**
     * Given two left shadows A and B, discriminate w agains A*B Time O(log n)
     * See "Ruler, Compass and Computer" (Guibas & Stolfi)
     *
     * @param A left shadow of a convex polygon P
     * @param B left shadow (reversed right shadow of P)
     * @param w point
     * @return 0 for no collision, 1 for collision
     */
    public static double guibasStolfi(Polygon A, Polygon B, Point2D.Double w, GeomGUI gui) {
        // endpoints
        Point2D.Double a1 = A.getPoints().get(0),
                a2 = A.getPoints().get(A.getPoints().size() - 1),
                b1 = A.getPoints().get(0),
                b2 = A.getPoints().get(A.getPoints().size() - 1);
        // Loop until a shadow is reduced to a single vertex
        while (A.getPointsNumber() > 1 && B.getPointsNumber() > 1) {
            // Median point indexes
            int i = A.getPoints().size() / 2, j = B.getPoints().size() / 2;
            // Make sure not to go out of bounds
            if (i == A.getPointsNumber() - 1) {
                i--;
            }
            if (j == B.getPointsNumber() - 1) {
                j--;
            }
            /*     System.out.println("Median edges: " + A.getPoints().get(i) + "-" + A.getPoints().get(i + 1) + ", "
             + B.getPoints().get(j) + "-" + B.getPoints().get(j + 1));*/
            System.out.println("Sizes: " + A.getPointsNumber() + "," + B.getPointsNumber());
            // Check if the segments starting from i and j are in slope order
            System.out.println("Pre Angles: " + Geometry.getNormalizedAngle(A.getPoints().get(i), A.getPoints().get(i + 1))
                    + "," + Geometry.getNormalizedAngle(B.getPoints().get(j), B.getPoints().get(j + 1)));
            if (Geometry.getNormalizedAngle(A.getPoints().get(i), A.getPoints().get(i + 1))
                    > Geometry.getNormalizedAngle(B.getPoints().get(j), B.getPoints().get(j + 1))) {
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

            double f2X = f1X + (A.getPoints().get(i + 1).x - A.getPoints().get(i).x),
                    f2Y = f1Y + (A.getPoints().get(i + 1).y - A.getPoints().get(i).y);

            // g1 == f2
            double g2X = f2X + (B.getPoints().get(j + 1).x - B.getPoints().get(j).x),
                    g2Y = f2Y + (B.getPoints().get(j + 1).y - B.getPoints().get(j).y);

            Line lineF = new Line(f1X, f1Y, f2X, f2Y),
                    lineG = new Line(f2X, f2Y, g2X, g2Y);

            System.out.println("Angles: " + Geometry.getNormalizedAngle(new Point2D.Double(f1X, f1Y), new Point2D.Double(f2X, f2Y))
                    + "," + Geometry.getNormalizedAngle(new Point2D.Double(f2X, f2Y), new Point2D.Double(g2X, g2Y)));
            System.out.println("Xs: " + f1X + "," + f2X + "," + g2X);
            System.out.println("Ys: " + f1Y + "," + f2Y + "," + g2Y);
            System.out.println("lineF: " + lineF);
            System.out.println("lineG: " + lineG);
            System.out.println("w:" + w);
            /*   gui.clearLines();
             gui.addLine(lineF);
             gui.addLine(lineG);*/

            Position testF = (Position) lineF.testAgainst(w),
                    testG = (Position) lineG.testAgainst(w);
            System.out.println("tests: " + testF + "," + testG);
            if (w.y > g2Y
                    || (/*testF == RIGHT &&*/testG == RIGHT && w.y > f2Y)) {
                // ABOVE
                System.out.println("ABOVE");
                // Drop Al and f
                A = new Polygon(A.getPoints().subList(i + 1, A.getPoints().size()));
            } else if (w.y < f1Y
                    || (testF == RIGHT && /*testG == RIGHT &&*/ w.y < f2Y)) {
                // BELOW
                System.out.println("BELOW");
                // Drop Bh and g
                B = new Polygon(B.getPoints().subList(0, j + 1));
            } else if (testF == RIGHT && testG == RIGHT && w.y == f2Y) {
                // IN BETWEEN
                System.out.println("BETWEEN");
                // below/above sans g/f ?
                B = new Polygon(B.getPoints().subList(0, j + 1));
            } else if ((testF == LEFT || testF == COLLIDES)
                    && (testG == LEFT || testG == COLLIDES)) {
                // LEFT
                System.out.println("LEFT");
                return 1;
            } else {
                // ???
                System.out.println("???");
                throw new RuntimeException("???");
            }

        }
        // End loop
        // One shadow is reduced to one vertex v, check the other one against w-v
        if (A.getPointsNumber() == 1) {
            w.setLocation(w.x - A.getPoints().get(0).x, w.y - A.getPoints().get(0).y);
            A = B;
        } else if (B.getPointsNumber() == 1) {
            w.setLocation(w.x - B.getPoints().get(0).x, w.y - B.getPoints().get(0).y);
        } else {
            throw new RuntimeException("Impossible? Shadows not reduced to one vertex.");
        }

        // Binary search in the remaining shadow
        int i = A.getPointsNumber() / 2;
        System.out.println("TRYING TO PLACE " + w);
        // Make sure not to go out of bounds        
        while (A.getPointsNumber() >= 2) {
            System.out.println("size: " + A.getPointsNumber());
            if (i == A.getPointsNumber() - 1) {
                i--;
            }
            System.out.println("i: " + i);
            Point2D.Double e1 = A.getPoints().get(i), e2 = A.getPoints().get(i + 1);
            Line l = new Line(e1, e2);
            System.out.println("against " + l);
            System.out.println("with points " + e1 + " - " + e2);
            Position pos = (Position) l.testAgainst(w);
            if (pos == LEFT || pos == COLLIDES) {
                if (/*(l.getSlope() >= 0) && */w.y >= e1.y && w.y <= e2.y) {
                    // w lies inside
                    System.out.println("--- > INSIDE SHADOW1");
                    return 1;
                } /*else if ((l.getSlope() < 0) && w.y >= e2.y && w.y <= e1.y) {
                 // w lies inside
                 System.out.println("--- > INSIDE SHADOW2");
                 return 1;
                 } */ else if (w.y < e1.y) {
                    // w is below e1
                    System.out.println("BELOW SHADOW");
                    A = new Polygon(A.getPoints().subList(0, i));
                    i = A.getPointsNumber() / 2;
                } else {
                    // w is above e2
                    System.out.println("ABOVE SHADOW");
                    A = new Polygon(A.getPoints().subList(i, A.getPointsNumber() - 1));
                    i = A.getPointsNumber() / 2;
                }
            } else {
                // w is outside
                System.out.println("--- >OUTSIDE SHADOW");
                return 0;
            }

        }

        // None of the above cases triggered: point outside of the convolution
        /*
         Possible causes: shadows composed only of vertical/horizontal lines
         */
        return 0;
        /*System.out.println("???2");
         throw new RuntimeException("???2");*/
        //     return 0;
    }

    /**
     * Algorithm to calculate the directional penetration depth between two
     * convex polygons, based on an algorithm by Guibas and Stolfi.
     *
     * @param A Left shadow of a Polygon
     * @param B Inverted right shadow of a Polygon
     * @param w Position vector (difference between A and B's position vectors)
     * @param d Direction of penetration ( as a Line )
     * @param orient Orientation of the direction ray d
     * @param gui GeomGui
     * @return Depth of penetration (in pixels)
     */
    public static double penDepth(Polygon A, Polygon B, Point2D.Double w, Line d,
            boolean orient, GeomGUI gui) {
        // Loop until a shadow is reduced to a single vertex
        // Loop until a shadow is reduced to a single vertex
        while (A.getPointsNumber() > 1 && B.getPointsNumber() > 1) {
            // Median point indexes
            int i = (A.getPoints().size() - 1) / 2,
                    j = (B.getPoints().size() - 1) / 2;
            // Make sure not to go out of bounds
            if (i == A.getPointsNumber() - 1) {
                i--;
            }
            if (j == B.getPointsNumber() - 1) {
                j--;
            }
            // Check if the segments starting from i and j are in slope order
            if (Geometry.getNormalizedAngle(A.getPoints().get(i), A.getPoints().get(i + 1))
                    > Geometry.getNormalizedAngle(B.getPoints().get(j), B.getPoints().get(j + 1))) {
                int k = i;
                i = j;
                j = k;
                Polygon C = A;
                A = B;
                B = C;
            }
            // Now assuming f and g are in slope order
            // Calculate the displacement of f in the chain D: (Al*Bl -> f -> g -> Ah*Bh)
            double f1X = A.getPoints().get(i).x + B.getPoints().get(j).x,
                    f1Y = A.getPoints().get(i).y + B.getPoints().get(j).y;

            double f2X = f1X + (A.getPoints().get(i + 1).x - A.getPoints().get(i).x),
                    f2Y = f1Y + (A.getPoints().get(i + 1).y - A.getPoints().get(i).y);

            // g1 == f2
            double g2X = f2X + (B.getPoints().get(j + 1).x - B.getPoints().get(j).x),
                    g2Y = f2Y + (B.getPoints().get(j + 1).y - B.getPoints().get(j).y);

            Line lineF = new Line(f1X, f1Y, f2X, f2Y),
                    lineG = new Line(f2X, f2Y, g2X, g2Y);
            Segment segF = new Segment(f1X, f1Y, f2X, f2Y),
                    segG = new Segment(f2X, f2Y, g2X, g2Y);

            /*   gui.clearLines();
             gui.addLine(lineF);
             gui.addLine(lineG);*/
            /*
             ------------------------------------------------------------------
             Check where the intersection lies
             ------------------------------------------------------------------
             */
            Point2D.Double testF = lineF.testIntersection(d),
                    testG = lineG.testIntersection(d);
            // testIntersection() returns a Point or null

            // Got at least one intersection
            if (testF != null) {
                /*
                 Check the position of the intersection point relative to edge f
                 Interesting cases:
                 - testF is on f
                 - testF is below f
                 */
                Segment.Position posF = (Segment.Position) segF.testAgainst(testF);
                if ((!d.isVertical() && ((orient && testF.x >= w.x) || (!orient && testF.x <= w.x)))
                        || (d.isVertical() && ((orient && testF.y >= w.y) || (!orient && testF.y <= w.y)))) {
                    // Intersection is valid
                    if (posF == Segment.Position.COLLIDES) {
                        // GOTCHA!
                        return Geometry.getLength(w, testF);
                    } else if (posF == Segment.Position.COLLINEAR_BELOW) {
                        // Intersection below f: g and Bh can be removed
                        B = new Polygon(B.getPoints().subList(0, j + 1));
                        continue;
                    }
                } else {
                    // Intersection generated from the line, not the ray: discard it.
                    System.out.println("Intersection for f invalid.");
                }
            }
            if (testG != null) {
                /*
                 Check the position of the intersection point relative to edge g
                 Interesting cases:
                 - testG is on g
                 - testG is above g
                 */
                Segment.Position posG = (Segment.Position) segG.testAgainst(testG);
                if ((!d.isVertical() && ((orient && testG.x >= w.x) || (!orient && testG.x <= w.x)))
                        || (d.isVertical() && ((orient && testG.y >= w.y) || (!orient && testG.y <= w.y)))) {
                    // Intersection is valid
                    if (posG == Segment.Position.COLLIDES) {
                        // GOTCHA!
                        return Geometry.getLength(w, testG);
                    } else if (posG == Segment.Position.COLLINEAR_ABOVE) {
                        // Intersection above g: f and Al can be removed
                        // Drop Al and f
                        A = new Polygon(A.getPoints().subList(i + 1, A.getPoints().size()));
                        continue;
                    }
                } else {
                    // Intersection generated from the line, not the ray: discard it.
                }
            }

            // No valid intersection
            if (d.isVertical()) {
                if (orient) {
                    // Drop Al and f
                    A = new Polygon(A.getPoints().subList(i + 1, A.getPoints().size()));
                    continue;
                } else {
                    B = new Polygon(B.getPoints().subList(0, j + 1));
                    continue;
                }
            }
            return 0;
        }// End while loop

        // None of the above cases triggered
        System.err.println("???");
        return 0;
    }

    /**
     * Algorithm to calculate the directional penetration depth between two
     * convex polygons, based on an algorithm by Guibas and Stolfi.
     *
     * @param A Left shadow of a Polygon
     * @param B Inverted right shadow of a Polygon
     * @param w Position vector (difference between A and B's position vectors)
     * @param d Direction of penetration ( as a Line )
     * @param orient Orientation of the direction ray d
     * @param gui GeomGui
     * @return Point representing the penetration vector
     */
    public static Point2D.Double penVect(Polygon A, Polygon B, Point2D.Double w, Line d,
            boolean orient, GeomGUI gui) {
        /*System.out.println("A: " + A.getPoints());
         System.out.println("B: " + B.getPoints());
         System.out.println("w: " + w);
         System.out.println("Direction: " + d);*/
        //    gui.clearAll();
        //  gui.addLine(d);
        // gui.addShape(A);
        // gui.addShape(B);
        //   gui.addShape(MinkowskiSum.minkowskiSumConvex(A, B));
        //   gui.addVector(w);
        //throw new RuntimeException("Stop");

        // Loop until a shadow is reduced to a single vertex
        while (A.getPointsNumber() > 1 && B.getPointsNumber() > 1) {
            // Median point indexes
            int i = (A.getPoints().size() - 1) / 2,
                    j = (B.getPoints().size() - 1) / 2;
            // Make sure not to go out of bounds
            if (i == A.getPointsNumber() - 1) {
                i--;
            }
            if (j == B.getPointsNumber() - 1) {
                j--;
            }
            System.out.println("\nSizes: " + A.getPointsNumber() + "," + B.getPointsNumber());
            // Check if the segments starting from i and j are in slope order
            if (Geometry.getNormalizedAngle(A.getPoints().get(i), A.getPoints().get(i + 1))
                    > Geometry.getNormalizedAngle(B.getPoints().get(j), B.getPoints().get(j + 1))) {
                int k = i;
                i = j;
                j = k;
                Polygon C = A;
                A = B;
                B = C;
            }
            // Now assuming f and g are in slope order
            // Calculate the displacement of f in the chain D: (Al*Bl -> f -> g -> Ah*Bh)
            double f1X = A.getPoints().get(i).x + B.getPoints().get(j).x,
                    f1Y = A.getPoints().get(i).y + B.getPoints().get(j).y;

            double f2X = f1X + (A.getPoints().get(i + 1).x - A.getPoints().get(i).x),
                    f2Y = f1Y + (A.getPoints().get(i + 1).y - A.getPoints().get(i).y);

            // g1 == f2
            double g2X = f2X + (B.getPoints().get(j + 1).x - B.getPoints().get(j).x),
                    g2Y = f2Y + (B.getPoints().get(j + 1).y - B.getPoints().get(j).y);

            Line lineF = new Line(f1X, f1Y, f2X, f2Y),
                    lineG = new Line(f2X, f2Y, g2X, g2Y);
            Segment segF = new Segment(f1X, f1Y, f2X, f2Y),
                    segG = new Segment(f2X, f2Y, g2X, g2Y);

            System.out.println("Xs: " + f1X + "," + f2X + "," + g2X);
            System.out.println("Ys: " + f1Y + "," + f2Y + "," + g2Y);
            /*   gui.clearLines();
             gui.addLine(lineF);
             gui.addLine(lineG);*/
            /*
             ------------------------------------------------------------------
             Check where the intersection lies
             ------------------------------------------------------------------
             */
            Point2D.Double testF = lineF.testIntersection(d),
                    testG = lineG.testIntersection(d);
            // testIntersection() returns a Point or null

            // Got at least one intersection
            if (testF != null) {
                /*
                 Check the position of the intersection point relative to edge f
                 Interesting cases:
                 - testF is on f
                 - testF is below f
                 */
                Segment.Position posF = (Segment.Position) segF.testAgainst(testF);
                System.out.println("posF: " + posF);
                if ((!d.isVertical() && ((orient && testF.x >= w.x) || (!orient && testF.x <= w.x)))
                        || (d.isVertical() && ((orient && testF.y >= w.y) || (!orient && testF.y <= w.y)))) {
                    // Intersection is valid
                    if (posF == Segment.Position.COLLIDES) {
                        // GOTCHA!
                        Point2D.Double out = new Point2D.Double(Math.abs(testF.x - w.x), Math.abs(testF.y - w.y));
                        System.out.println("GOTCHA! " + out);
                        return out;
                    } else if (posF == Segment.Position.COLLINEAR_BELOW) {
                        // Intersection below f: g and Bh can be removed
                        System.out.println("BELOW");
                        B = new Polygon(B.getPoints().subList(0, j + 1));
                        System.out.println("B shortened to " + B.getPointsNumber());
                        continue;
                    }
                } else {
                    // Intersection generated from the line, not the ray: discard it.
                    System.out.println("Intersection for f invalid.");
                }
            }
            if (testG != null) {
                /*
                 Check the position of the intersection point relative to edge g
                 Interesting cases:
                 - testG is on g
                 - testG is above g
                 */
                Segment.Position posG = (Segment.Position) segG.testAgainst(testG);
                System.out.println("posG: " + posG);
                if ((!d.isVertical() && ((orient && testG.x >= w.x) || (!orient && testG.x <= w.x)))
                        || (d.isVertical() && ((orient && testG.y >= w.y) || (!orient && testG.y <= w.y)))) {
                    // Intersection is valid
                    if (posG == Segment.Position.COLLIDES) {
                        // GOTCHA!
                        Point2D.Double out = new Point2D.Double(Math.abs(testG.x - w.x), Math.abs(testG.y - w.y));
                        System.out.println("GOTCHA! " + out);
                        return out;
                    } else if (posG == Segment.Position.COLLINEAR_ABOVE) {
                        // Intersection above g: f and Al can be removed
                        System.out.println("ABOVE");
                        // Drop Al and f
                        A = new Polygon(A.getPoints().subList(i + 1, A.getPoints().size()));
                        System.out.println("A shortened to " + A.getPointsNumber());
                        continue;
                    }
                } else {
                    // Intersection generated from the line, not the ray: discard it.
                    System.out.println("Intersection for g invalid.");
                }
            }

            // No valid intersection
            if (d.isVertical()) {
                if (orient) {
                    System.out.println("ABOVE_VERT");
                    // Drop Al and f
                    A = new Polygon(A.getPoints().subList(i + 1, A.getPoints().size()));
                    System.out.println("A shortened to " + A.getPointsNumber());
                    continue;
                } else {
                    System.out.println("BELOW_VERT");
                    B = new Polygon(B.getPoints().subList(0, j + 1));
                    System.out.println("B shortened to " + B.getPointsNumber());
                    continue;
                }
            }

            //throw new RuntimeException("No valid intersection!");
            System.out.println("No valid intersection!");
            return new Point2D.Double(0, 0);

        }// End while loop

        // None of the above cases triggered
        System.out.println("???2");
        //throw new RuntimeException("???2");
        return new Point2D.Double(0, 0);
    }

    public static Polygon getLeftShadow(Polygon P, int max, int min) {
        // use the Polygon constructor to clone the points
        Polygon P2 = new Polygon(P);
        LinkedList<Point2D.Double> ptsR = new LinkedList<>();
        List<Point2D.Double> ptsP2 = P2.getPoints();

        Point2D.Double pMin = ptsP2.get(min);
        // Add Point to infinity
        ptsR.add(new Point2D.Double(NEG_INFINITY, pMin.getY()));
        ptsR.add(pMin);
        for (int i = min + 1; i < max + 1; i++) {
            Point2D.Double p = ptsP2.get(i);
            // Avoid inserting horizontal lines in the shadow
            if (ptsR.getLast().y == p.y) {
                ptsR.removeLast();
            }
            ptsR.add(p);
        }
        Point2D.Double pMax = ptsR.get(ptsR.size() - 1);
        // Add Point to infinity
        ptsR.add(new Point2D.Double(NEG_INFINITY, pMax.y));
        return new Polygon(ptsR);
    }

    public static Polygon getRightShadowInv(Polygon P, int max, int min) {
        // use the Polygon constructor to clone the points      
        Polygon P2 = new Polygon(P);
        /*System.out.println("P2: " + P2.getPoints());
         System.out.println("min: " + min + ", max: " + max);*/
        LinkedList<Point2D.Double> ptsR = new LinkedList<>();
        List<Point2D.Double> ptsP2 = P2.getPoints();

        Point2D.Double pMax = ptsP2.get(max);
        // Add Point to infinity
        ptsR.add(new Point2D.Double(NEG_INFINITY, -pMax.y));
        ptsR.add(new Point2D.Double(-pMax.x, -pMax.y));
        if (max < ptsP2.size()) {
            for (int i = max + 1; i < ptsP2.size(); i++) {
                Point2D.Double p = ptsP2.get(i);
                p.setLocation(-p.x, -p.y);
                // Avoid inserting horizontal lines in the shadow
                if (ptsR.getLast().y == p.y) {
                    ptsR.removeLast();
                }
                ptsR.add(p);
            }
        }
        Point2D.Double pMin = ptsP2.get(0);
        ptsR.add(new Point2D.Double(-pMin.x, -pMin.y));
        for (int i = 1; i < min; i++) {
            Point2D.Double p = ptsP2.get(i);
            p.setLocation(-p.x, -p.y);
            // Avoid inserting horizontal lines in the shadow
            if (ptsR.getLast().y != p.y) {
                ptsR.add(p);
            }
        }
        // Add Point to infinity
        ptsR.add(new Point2D.Double(NEG_INFINITY, -ptsR.get(ptsR.size() - 1).y));

        // System.out.println("R: " + ptsR);
        Polygon R = new Polygon(ptsR);
        return R;
    }

}
