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
 * Class representing an infinite, continuous line.
 *
 * @author EPresident <prez_enquiry@hotmail.com>
 */
public class Line {

    private double slope, yIntercept;
    public static final double INFINITY = Double.MAX_VALUE;

    public static enum Position {

        LEFT, ABOVE, RIGHT, BELOW
    };

    public Line(Point2D.Double p1, Point2D.Double p2) {
        /*  if (p1.x != p2.x) {
         if (p1.y != p2.y) {
         slope = (p2.y - p1.y) / (p2.x - p1.x);
         yIntercept = (p2.x * p1.y - p1.x * p2.y) / (p2.x - p1.x);
         } else {
         throw new RuntimeException("Points are equal!");
         }
         } else {
         slope = 1;
         yIntercept = 0;
         }*/
        this(p1.x, p1.y, p2.x, p2.y);
    }

    public Line(double p1X, double p1Y, double p2X, double p2Y) {
        if (p1X != p2X) {
            if (p1Y != p2Y) {
                // Oblique line
                slope = (p2Y - p1Y) / (p2X - p1X);
                yIntercept = (p2X * p1Y - p1X * p2Y) / (p2X - p1X);
            } else {
                // Horizontal line
                slope = 0;
                yIntercept = p1Y;
            }
        } else if (p1Y != p2Y) {
            // Vertical line
            slope = INFINITY;
            yIntercept = p1X;
        } else {
            // Line degenerates into a Point
            throw new RuntimeException("Points are equal!");
        }
    }

    public Line(double m, double q) {
        this.slope = m;
        this.yIntercept = q;
    }

    public Position testAgainst(Point2D.Double p) {

    }

    public double getSlope() {
        return slope;
    }

    public double getYIntercept() {
        return yIntercept;
    }

}
