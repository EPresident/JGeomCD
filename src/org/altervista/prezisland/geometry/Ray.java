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
 *
 * @author EPresident <prez_enquiry@hotmail.com>
 */
public class Ray {

    Point2D.Double p;
    Line l;
    /**
     * If true, the direction of this ray goes from negative coordinates to
     * positive coordinates (from negative x to positive x, or from negative y
     * to positive y if vertical).
     */
    boolean negToPos;

    public Ray(Point2D.Double p1, Point2D.Double p2) {
        p = p1;
        l = new Line(p1, p2);
        if (Geometry.compareLexicographicallyX(p1, p2) < 0) {
            negToPos = true;
        } else if (Geometry.compareLexicographicallyX(p1, p2) > 0) {
            negToPos = false;
        } else {
            // degenerates into a Point
            throw new RuntimeException("Points (" + p1.x + "," + p1.y + ") and "
                    + "(" + p2.x + "," + p2.y + ") are equal! Invalid Line.");
        }
    }

    public Ray(double p1X, double p1Y, double p2X, double p2Y) {
        this(new Point2D.Double(p1X, p1Y), new Point2D.Double(p2X, p2Y));
    }

    public Ray(Line l, Point2D.Double p, boolean dir) {
        if (l.testAgainst(p) != Line.Position.COLLIDES) {
            throw new RuntimeException("Point " + p + " does not lie on Line " + l + ".");
        } else {
            this.l = l;
            this.p = p;
            negToPos = dir;
        }
    }

    public RelativePosition testAgainst(Point2D.Double p) {
        return l.testAgainst(p);
    }

    public Point2D.Double testIntersection(Line l) {
        return this.l.testIntersection(l);
    }

    public double calculateY(double x) {
        return l.calculateY(x);
    }

    public double calculateX(double y) {
        return l.calculateX(y);
    }

    public boolean isVertical() {
        return l.isVertical();
    }

    public boolean isHorizontal() {
        return l.isHorizontal();
    }

    public boolean isOblique() {
        return l.isOblique();
    }

    
}
