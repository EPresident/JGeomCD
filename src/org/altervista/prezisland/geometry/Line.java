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
 * Class representing an infinite, continuous line. Equation: a*x + b*y + c = 0,
 * y = slope*x + yIntercept
 *
 * @author EPresident <prez_enquiry@hotmail.com>
 */
public class Line {

    private double slope, yIntercept;
    private double a, b, c;
    public static final double INFINITY = Double.MAX_VALUE;

    public static enum Position implements RelativePosition {

        LEFT, //ABOVE, 
        RIGHT, //BELOW, 
        COLLIDES
    };

    /**
     * Clone constructor.
     *
     * @param l Line to clone
     */
    public Line(Line l) {
        this.a = l.a;
        this.b = l.b;
        this.c = l.c;
        if (a == 0 && b == 0) {
            // degenerates into a Point
            throw new RuntimeException("Invalid Line.");
        } else if (a == 0) {
            // Horizontal line
            slope = 0;
            yIntercept = -c / b;
        } else if (b == 0) {
            // Vertical line
            slope = INFINITY;
            yIntercept = INFINITY;
        } else {
            slope = (-a) / b;
            yIntercept = (-c) / b;
        }
    }

    public Line(Point2D.Double p1, Point2D.Double p2) {
        this(p1.x, p1.y, p2.x, p2.y);
    }

    public Line(double p1X, double p1Y, double p2X, double p2Y) {
        //Equation: a*x + b*y + c = 0
        a = p1Y - p2Y;
        b = p2X - p1X;
        c = p1Y * (p1X - p2X) + p1X * (p2Y - p1Y);
        if (a == 0 && b == 0) {
            // degenerates into a Point
            throw new RuntimeException("Points (" + p1X + "," + p1Y + ") and "
                    + "(" + p2X + "," + p2Y + ") are equal! Invalid Line.");
        } else if (a == 0) {
            // Horizontal line
            slope = 0;
            yIntercept = -c / b;
        } else if (b == 0) {
            // Vertical line
            slope = INFINITY;
            yIntercept = INFINITY;
        } else {
            slope = (-a) / b;
            yIntercept = (-c) / b;
        }
    }

    public RelativePosition testAgainst(Point2D.Double p) {
        if (a == 0) {
            // Horizontal line
            if (p.y < -(c / b)) {
                return Position.RIGHT;
            }
            if (p.y > -(c / b)) {
                return Position.LEFT;
            } else {
                return Position.COLLIDES;
            }
        } else if (b == 0) {
            // Vertical line
            if (p.x < -(c / a)) {
                return Position.LEFT;
            }
            if (p.x > -(c / a)) {
                return Position.RIGHT;
            } else {
                return Position.COLLIDES;
            }
        } else {
            // Oblique line
            System.out.println("p.y: " + p.y + "   y:" + (slope * p.x + yIntercept));
            if (slope < 0) {
                if (p.y > slope * p.x + yIntercept) {
                    return Position.RIGHT;
                } else if (p.y < slope * p.x + yIntercept) {
                    return Position.LEFT;
                } else {
                    return Position.COLLIDES;
                }
            } else {
                // Slope > 0
                if (p.y < slope * p.x + yIntercept) {
                    return Position.RIGHT;
                } else if (p.y > slope * p.x + yIntercept) {
                    return Position.LEFT;
                } else {
                    return Position.COLLIDES;
                }
            }
        }
    }

    public Point2D.Double testIntersection(Line l) {
        if (l.isVertical()) {
            if (this.isVertical()) {

                // Check x value
                double x = this.calculateX(0);
                if (x == l.calculateX(0)) {
                    System.out.println("Warning: intersection is a line.");
                    return new Point2D.Double(x, 0);
                }
            } else if (this.isHorizontal()) {
                // l vertical, this horizontal
                // Trivial intersection
                return new Point2D.Double(-l.c / l.a, -this.c / this.b);
            } else {
                // vertical line vs oblique line
                double x = l.calculateX(0);
                return new Point2D.Double(x, this.calculateY(x));
            }
        } else if (l.isHorizontal()) {
            if (this.isVertical()) {
                // l horizontal, this vertical
                // Trivial intersection
                return new Point2D.Double(this.calculateX(0), l.calculateY(0));
            } else if (this.isHorizontal()) {
                // Both lines horizontal
                // Check y value
                double y = this.calculateY(0);
                if (y == l.calculateY(0)) {
                    System.out.println("Warning: intersection is a line.");
                    return new Point2D.Double(0, y);
                }
            } else {
                // horizontal line vs oblique line
                double y = l.calculateY(0);
                return new Point2D.Double(this.calculateX(y), y);
            }
        } else {
            // l is oblique
            if (this.isVertical()) {
                // vertical line vs oblique line
                double x = this.calculateX(0);
                return new Point2D.Double(x, l.calculateY(x));
            } else if (this.isHorizontal()) {
                // horizontal line vs oblique line
                double y = this.calculateY(0);
                return new Point2D.Double(l.calculateX(y), y);
            } else {
                // Both lines are oblique
                if (this.slope == l.slope) {
                    // Lines are parallel
                    if (this.yIntercept == l.yIntercept) {
                        // Lines are equal
                        System.out.println("Warning: intersection is a line.");
                        return new Point2D.Double(0, this.calculateY(0));
                    }
                } else {
                    /*
                     Line l1: y = m*x + q
                     Line l2: y = n*x + r
                     Intersection x: m*x + q = n*x + r
                     --> x = (r - q)/(m-n)
                     */
                    double x = (l.yIntercept - this.yIntercept) / (this.slope - l.slope);
                    return new Point2D.Double(x, this.calculateY(x));
                }
            }
        }
        return null;
    }

    public double calculateY(double x) {
        if (a != 0 && b != 0) {
            return (x * slope) + yIntercept;
        } else if (a == 0) {
            return -c / b;
        }
        throw new RuntimeException("Cannot calculate Y of a vertical line!");
    }

    public double calculateX(double y) {
        if (a != 0 && b != 0) {
            return (-b / a) * y + (-c / a);
        } else if (b == 0) {
            return -c / a;
        }
        throw new RuntimeException("Cannot calculate Y of an horizontal line!");
    }

    /**
     * Test pending!
     *
     * @param p
     */
    public void traslate(Point2D p) {
        if (isVertical()) {
            a = 1;
            c = -p.getX();
            yIntercept = -c / b;
        } else if (isHorizontal()) {
            b = 1;
            c = -p.getY();
        } else {
            double y = calculateY(1);
            y -= calculateY(0);
            double p1X = p.getX(), p1Y = p.getY();
            double p2X = p1X + 1, p2Y = p1Y + y;
            a = p1Y - p2Y;
            b = p2X - p1X;
            c = p1Y * (p1X - p2X) + p1X * (p2Y - p1Y);
            slope = (-a) / b;
            yIntercept = (-c) / b;
        }
    }

    public double getSlope() {
        return slope;
    }

    public double getYIntercept() {
        return yIntercept;
    }

    public double getA() {
        return a;
    }

    public double getB() {
        return b;
    }

    public double getC() {
        return c;
    }

    public boolean isVertical() {
        return b == 0;
    }

    public boolean isHorizontal() {
        return a == 0;
    }

    public boolean isOblique() {
        return a != 0 && b != 0;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Line) {
            Line l = (Line) o;
            if (this.a == l.a && this.b == l.b && this.c == l.c) {
                return true;
            }
            double a1 = (this.a - l.a) / 2, b1 = (this.b - l.b) / 2,
                    c1 = (this.c - l.c) / 2;
            double TOLLERANCE = 0.001;
            if ((a1 > this.a - TOLLERANCE && a1 < this.a + TOLLERANCE)
                    && (b1 > this.b - TOLLERANCE && b1 < this.b + TOLLERANCE)
                    && (c1 > this.c - TOLLERANCE && c1 < this.c + TOLLERANCE)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Line: ");
        sb.append(a).append("x + ").append(b).append("y + ").append(c).append(" = 0 , ")
                .append("y = ").append(slope).append("x + ").append(yIntercept);
        return sb.toString();
    }

}
