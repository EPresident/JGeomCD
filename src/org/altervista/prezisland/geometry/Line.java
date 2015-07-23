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
 * Class representing an infinite, continuous line. a*y + b*x + c = 0
 *
 * @author EPresident <prez_enquiry@hotmail.com>
 */
public class Line {

    private double slope, yIntercept;
    private double a, b, c;
    public static final double INFINITY = Double.MAX_VALUE;

    public static enum Position {

        LEFT, //ABOVE, 
        RIGHT, //BELOW, 
        COLLIDES
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
        a = p2X - p1X;
        b = p1Y - p2Y;
        c = p1Y * (p1X - p2X) + p1X * (p2Y - p1Y);
        if (a == 0 && b == 0) {
            throw new RuntimeException("Points are equal! Invalid Line.");
        }
        slope = (-a)/b;
        yIntercept = (-c)/b;
        /* if (p1X != p2X) {
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
         yIntercept = INFINITY;
         } else {
         // Line degenerates into a Point
         throw new RuntimeException("Points are equal! Invalid Line.");
         }*/
    }

    public Line(double m, double q) {
        this.slope = m;
        this.yIntercept = q;
    }

    public Position testAgainst(Point2D.Double p) {
        if (a == 0) {
            // Vertical line
            if(p.x < - (c/b)){
                return Position.LEFT;
            }if(p.x > - (c/b)){
                return Position.RIGHT;
            }else{
                return Position.COLLIDES;
            }
        } else if (b == 0) {
            // Horizontal line
            if(p.y < - (c/a)){
                return Position.RIGHT;
            }if(p.y > - (c/a)){
                return Position.LEFT;
            }else{
                return Position.COLLIDES;
            }
        } else {
            // Oblique line
            if(p.y > slope * p.x + yIntercept){
                return Position.RIGHT;
            }else  if(p.y < slope * p.x + yIntercept){
                return Position.LEFT;
            }else{
                return Position.COLLIDES;
            }
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

}
