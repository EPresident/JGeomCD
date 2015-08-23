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

import java.awt.geom.Point2D;

/**
 * Class representing 2D segments with double precision.
 *
 * @author EPresident <prez_enquiry@hotmail.com>
 */
public class Segment extends Line {

    /**
     * Points defining the segment. This class allows queries for the position
     * of points relative to the segment.
     */
    Point2D.Double p1, p2;

    /**
     * Positions relative to the segment and its direction, defined by points p1
     * and p2 (left to right, right to left, top to bottom,...)
     */
    public static enum Position implements RelativePosition {

        LEFT,
        RIGHT,
        COLLIDES,
        COLLINEAR_ABOVE,
        COLLINEAR_BELOW
    }

    public Segment(double x1, double y1, double x2, double y2) {
        super(x1, y1, x2, y2);
        // Make sure p1 is the "smallest" point x-lexicographically
        //  if (Geometry.compareLexicographicallyX(new Point2D.Double(x1, y1), new Point2D.Double(x2, y2)) <= 0) {
        p1 = new Point2D.Double(x1, y1);
        p2 = new Point2D.Double(x2, y2);
        /*   } else {
         p2 = new Point2D.Double(x1, y1);
         p1 = new Point2D.Double(x2, y2);
         }*/
    }

    public Segment(Point2D.Double p1, Point2D.Double p2) {
        this(p1.x, p1.y, p2.x, p2.y);
    }

    /**
     * @return The length of the segment
     */
    public double getLength() {
        return Math.sqrt(Math.pow(Math.abs(p1.x - p2.x), 2) + Math.pow(Math.abs(p1.y - p2.y), 2));
    }

    /**
     * Return the position of a point relative to this segment. The "direction"
     * of the segment is from point 1 to point 2, so "up" is beyond point 2,
     * "down" is before point 1, and so on. A set tollerance is employed to
     * minimize approximation errors.
     *
     * @param p Point to test
     * @return Relative position as a Segment.Position enum.
     */
    @Override
    public RelativePosition testAgainst(Point2D.Double p) {
        final double TOLLERANCE = 0.001;

        /*
         Tollerance is used to minimize approximation errors, effectively
         enlarging the segment by a radius of TOLLERANCE in each direction.
         */
        double a = super.getA(), b = super.getB(), c = super.getC();
        if (a == 0) {
            // -----------------------------------------
            //      ---     Horizontal line     ---
            // -----------------------------------------

            // The "direction" used as reference is left to right or right to left?
            boolean direction = Geometry.compareLexicographicallyX(p1, p2) < 0;

            if (p.y < -(c / b) - TOLLERANCE) {
                // Point is below the line
                return direction ? Position.RIGHT : Position.LEFT;
            } else if (p.y > -(c / b) + TOLLERANCE) {
                // Point is above the line
                return direction ? Position.LEFT : Position.RIGHT;
            } else {
                // Point is collinear
                // Order temporainally the points to write less code
                Point2D.Double tp1 = p1, tp2 = p2;
                if (!direction) {
                    tp1 = p2;
                    tp2 = p1;
                }
                if (p.x >= tp1.x - TOLLERANCE && p.x <= tp2.x + TOLLERANCE) {
                    // Point is between p1 and p2
                    return Position.COLLIDES;
                } else if (p.x < tp1.x - TOLLERANCE) {
                    // Point lays before p1
                    return direction ? Position.COLLINEAR_BELOW : Position.COLLINEAR_ABOVE;
                } else {
                    // Point lays after p2
                    return direction ? Position.COLLINEAR_ABOVE : Position.COLLINEAR_BELOW;
                }
            }
        } else if (b == 0) {
            // -----------------------------------------
            //      ---     Vertical line       ---
            // -----------------------------------------

            // The "direction" used as reference is left to right or right to left?
            boolean direction = p1.y < p2.y;

            if (p.x < -(c / a) - TOLLERANCE) {
                return direction ? Position.LEFT : Position.RIGHT;
            }
            if (p.x > -(c / a) + TOLLERANCE) {
                return direction ? Position.RIGHT : Position.LEFT;
            } else {
                // Point is collinear
                // In this case we order temporainally the points to write less code
                Point2D.Double tp1 = p1, tp2 = p2;
                if (!direction) {
                    tp1 = p2;
                    tp2 = p1;
                }
                if (p.y >= tp1.y - TOLLERANCE && p.y <= tp2.y + TOLLERANCE) {
                    return Position.COLLIDES;
                } else if (p.y < tp1.y + TOLLERANCE) {
                    return direction ? Position.COLLINEAR_BELOW : Position.COLLINEAR_ABOVE;
                } else {
                    // p.y > p2.y
                    return direction ? Position.COLLINEAR_ABOVE : Position.COLLINEAR_BELOW;
                }
            }
        } else {
            // -----------------------------------------
            //      ---     Oblique line        ---
            // -----------------------------------------

            double slope = super.getSlope(), yIntercept = super.getYIntercept();
            // The "direction" used as reference is left to right or right to left?
            boolean direction = Geometry.compareLexicographicallyX(p1, p2) < 0;
            if (p.y > slope * p.x + yIntercept + TOLLERANCE) {
                return direction ? Position.LEFT : Position.RIGHT;
            } else if (p.y < slope * p.x + yIntercept - TOLLERANCE) {
                return direction ? Position.RIGHT : Position.LEFT;
            } else {
                //  y = m*x + q => Point is collinear
                // We order temporainally the points to write less code
                Point2D.Double tp1 = p1, tp2 = p2;
                if (!direction) {
                    tp1 = p2;
                    tp2 = p1;
                }
                if (p.x >= tp1.x - TOLLERANCE && p.x <= tp2.x + TOLLERANCE) {
                    return Position.COLLIDES;
                } else if (p.x < tp1.x - TOLLERANCE) {
                    return direction ? Position.COLLINEAR_BELOW : Position.COLLINEAR_ABOVE;
                } else {
                    return direction ? Position.COLLINEAR_ABOVE : Position.COLLINEAR_BELOW;
                }
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Segment (");
        sb.append(p1.x).append(",").append(p1.y).append(")->(").append(p2.x)
                .append(",").append(p2.y).append("); ").append(super.toString());
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    /**
     * Given the y coordinate, calculate the relative x coordinate for this
     * line.
     *
     * @param y Position on the y axis
     * @return Position on the x axis
     */
    @Override
    public double calculateX(double y) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    /**
     * Given the x coordinate, calculate the relative y coordinate for this
     * line.
     *
     * @param x Position on the x axis
     * @return Position on the y axis
     */
    @Override
    public double calculateY(double x) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    /**
     * Check if a line intersects this segment. Point intersections supported only.
     * @param l Test line
     * @return Point of intersection (a witness in the intersection is big)
     */
    @Override
    public Point2D.Double testIntersection(Line l) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    /**
     * Return the line passing through this segment
     * @return 
     */
    public Line asLine() {
        return new Line(p1, p2);
    }

    public Point2D.Double getP1() {
        return p1;
    }

    public Point2D.Double getP2() {
        return p2;
    }

}
