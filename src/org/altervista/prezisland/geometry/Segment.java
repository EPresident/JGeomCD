/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.altervista.prezisland.geometry;

import java.awt.geom.Point2D;

/**
 * Class representing 2D segments with double precision.
 *
 * @author EPresident <prez_enquiry@hotmail.com>
 */
public class Segment extends Line {

    Point2D.Double p1, p2;

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
        if (Geometry.compareLexicographicallyX(new Point2D.Double(x1, y1), new Point2D.Double(x2, y2)) <= 0) {
            p1 = new Point2D.Double(x1, y1);
            p2 = new Point2D.Double(x2, y2);
        } else {
            p2 = new Point2D.Double(x1, y1);
            p1 = new Point2D.Double(x2, y2);
        }
    }

    public Segment(Point2D.Double p1, Point2D.Double p2) {
        this(p1.x, p1.y, p2.x, p2.y);
    }

    public double getLength() {
        return Math.sqrt(Math.pow(Math.abs(p1.x - p2.x), 2) + Math.pow(Math.abs(p1.y - p2.y), 2));
    }

    @Override
    public RelativePosition testAgainst(Point2D.Double p) {
        double a = super.getA(), b = super.getB(), c = super.getC();
        if (a == 0) {
            // Horizontal line
            if (p.y < -(c / b)) {
                return Position.RIGHT;
            }
            if (p.y > -(c / b)) {
                return Position.LEFT;
            } else {
                // Point is collinear
                if (p.x >= p1.x && p.x <= p2.x) {
                    return Position.COLLIDES;
                } else if (p.x < p1.x) {
                    return Position.COLLINEAR_BELOW;
                } else {
                    // p.x > p2.x
                    return Position.COLLINEAR_ABOVE;
                }
            }
        } else if (b == 0) {
            // Vertical line
            if (p.x < -(c / a)) {
                return Position.LEFT;
            }
            if (p.x > -(c / a)) {
                return Position.RIGHT;
            } else {
                // Point is collinear
                if (p.y >= p1.y && p.y <= p2.y) {
                    return Position.COLLIDES;
                } else if (p.y < p1.y) {
                    return Position.COLLINEAR_BELOW;
                } else {
                    // p.y > p2.y
                    return Position.COLLINEAR_ABOVE;
                }
            }
        } else {
            // Oblique line
            double slope = super.getSlope(), yIntercept = super.getYIntercept();
            if (slope < 0) {
                if (p.y > slope * p.x + yIntercept) {
                    return Position.RIGHT;
                } else if (p.y < slope * p.x + yIntercept) {
                    return Position.LEFT;
                } else {
                    // Point is collinear
                    if (p.y >= p2.y && p.y <= p1.y && p.x >= p1.x && p.x <= p2.x) {
                        return Position.COLLIDES;
                    } else if (p.y > p1.y && p.x < p1.x) {
                        return Position.COLLINEAR_BELOW;
                    } else {
                        // p.y < p2.y && p.x > p2.x
                        return Position.COLLINEAR_ABOVE;
                    }
                }
            } else {
                // Slope > 0
                System.out.println(yIntercept);
                if (p.y < slope * p.x + yIntercept) {
                    return Position.RIGHT;
                } else if (p.y > slope * p.x + yIntercept) {
                    return Position.LEFT;
                } else {
                    // Point is collinear
                    if (p.y >= p1.y && p.y <= p2.y && p.x >= p1.x && p.x <= p2.x) {
                        return Position.COLLIDES;
                    } else if (p.y < p1.y && p.x < p1.x) {
                        return Position.COLLINEAR_BELOW;
                    } else {
                        // p.y > p2.y && p.x > p2.x
                        return Position.COLLINEAR_ABOVE;
                    }
                }
            }
        }
    }

    @Override
    public String toString() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public boolean equals(Object o) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public double calculateX(double y) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public double calculateY(double x) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public Point2D.Double testIntersection(Line l) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }
    
    public Line asLine(){
        return new Line(p1,p2);
    }

    public Point2D.Double getP1() {
        return p1;
    }

    public Point2D.Double getP2() {
        return p2;
    }

}
