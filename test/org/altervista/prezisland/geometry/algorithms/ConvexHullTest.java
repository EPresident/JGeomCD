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
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author EPresident <prez_enquiry@hotmail.com>
 */
public class ConvexHullTest {

    public ConvexHullTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    /**
     * Test of grahamConvexHull method, of class ConvexHull.
     */
    @Test
    public void testGrahamConvexHull() {
        System.out.println("grahamConvexHull");
        List<Point2D.Double> pts = new ArrayList<>();
        pts.add(new Point2D.Double(0, 0));
        pts.add(new Point2D.Double(0, 4));
        pts.add(new Point2D.Double(1, 2));
        pts.add(new Point2D.Double(2, 3));
        pts.add(new Point2D.Double(3, 1));
        pts.add(new Point2D.Double(4, 0));
        pts.add(new Point2D.Double(4, 4));
        Geometry.sortLexicographicallyX(pts);
        ArrayList<Point2D.Double> result = ConvexHull.grahamConvexHull(pts);
        System.out.println("Result: "+result.toString());
        assertTrue(pts.get(0).equals(new Point2D.Double(0,0)));
        assertTrue(pts.get(0).equals(new Point2D.Double(0,4)));
        assertTrue(pts.get(0).equals(new Point2D.Double(4,0)));
        assertTrue(pts.get(0).equals(new Point2D.Double(4,4)));
    }

}
