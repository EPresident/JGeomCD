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
package org.altervista.prezisland.geometry.shapes;

import java.awt.geom.Point2D;
import org.altervista.prezisland.geometry.CartesianVector;

/**
 * Axis Aligned Bounding Box
 *
 * @author EPresident <prez_enquiry@hotmail.com>
 */
public class AABB extends Polygon {

    private final double width, height, halfWidth, halfHeight;
    private final static CartesianVector[] aabbAxes = {new CartesianVector(1, 0),
        new CartesianVector(0, 1)};

    /**
     *
     * @param ctr Center of the box.
     * @param w Width of the box (pixels)
     * @param h Heigth of the box (pixels)
     */
    public AABB(Point2D.Double ctr, double w, double h) {
        super(new Point2D.Double[]{new Point2D.Double(ctr.x - w / 2, ctr.y + h / 2),
            new Point2D.Double(ctr.x + w / 2, ctr.y + h / 2),
            new Point2D.Double(ctr.x + w / 2, ctr.y - h / 2),
            new Point2D.Double(ctr.x - w / 2, ctr.y - h / 2)}, ctr);
        width = w;
        height = h;
        halfWidth = w / 2;
        halfHeight = h / 2;
        separatingAxes = aabbAxes;
    }

    /**
     *
     * @param cx X coordinate of the center.
     * @param cy Y coordinate of the center.
     * @param w Width of the box (pixels)
     * @param h Heigth of the box (pixels)
     */
    public AABB(double cx, double cy, double w, double h) {
        this(new Point2D.Double(cx, cy), w, h);
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    @Override
    public CartesianVector[] getSeparatingAxes() {
        return separatingAxes;
    }

}
