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
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author EPresident <prez_enquiry@hotmail.com>
 */
public class LineTest {

    public LineTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of testAgainst method, of class Line.
     */
    @Test
    public void testTestAgainst() {
        System.out.println(" --- testAgainst --- ");
        Line instance = new Line(new Point2D.Double(0, 0), new Point2D.Double(1, 1));
        Point2D.Double[] ps = {new Point2D.Double(2, 0), new Point2D.Double(-2, 0),
            new Point2D.Double(0, 2), new Point2D.Double(0, -2), new Point2D.Double(0, 0)};
        Line.Position[] expResults = {Line.Position.RIGHT, Line.Position.LEFT,
            Line.Position.LEFT, Line.Position.RIGHT, Line.Position.COLLIDES};
        for (int i = 0; i < ps.length; i++) {
            Line.Position result = (Line.Position)instance.testAgainst(ps[i]);   
            System.out.println("Testing against "+ps[i]);
            assertEquals(expResults[i], result);
        }
    }

    /**
     * Test of testIntersection method, of class Line.
     */
    @Test
    public void testTestIntersection() {
        System.out.println(" --- testIntersection --- ");
        /*
         x = 10
         y = -10
         y = x
         */
        Line[] l1s = {new Line(new Point2D.Double(10, 0), new Point2D.Double(10, 10)),
            new Line(new Point2D.Double(0, -10), new Point2D.Double(10, -10)),
            new Line(new Point2D.Double(0, 0), new Point2D.Double(10, 10))};
        /*
         x = 10
         y = 30
         y = -x
         */
        Line[] l2s = {new Line(new Point2D.Double(10, 0), new Point2D.Double(10, 10)),
            new Line(new Point2D.Double(0, 30), new Point2D.Double(30, 30)),
            new Line(new Point2D.Double(10, -10), new Point2D.Double(0, 0))};
        Point2D.Double[] expResults
                = {new Point2D.Double(10, 0), new Point2D.Double(10, 30), new Point2D.Double(10, -10),
                    new Point2D.Double(10, -10), null, new Point2D.Double(10, -10),
                    new Point2D.Double(10, 10), new Point2D.Double(30, 30), new Point2D.Double(0, 0)};
        int k = 0;
        for (int i = 0; i < l1s.length; i++) {
            for (int j = 0; j < l2s.length; j++) {
                System.out.println("Testing "+l1s[i]+"\nagainst "+l2s[j]+"\nexpected: "+expResults[k]);
                Point2D.Double result = l1s[i].testIntersection(l2s[j]);
                assertEquals(expResults[k++], result);
                System.out.println("");
            }
        }

    }

    /**
     * Test of isVertical method, of class Line.
     */
    @Test
    public void testIsVertical() {

    }

    /**
     * Test of isHorizontal method, of class Line.
     */
    @Test
    public void testIsHorizontal() {

    }

    /**
     * Test of isOblique method, of class Line.
     */
    @Test
    public void testIsOblique() {

    }

    /**
     * Test of calculateY method, of class Line.
     */
    @Test
    public void testCalculateY() {
       /* System.out.println("calculateY");
        double x = 0.0;
        Line instance = null;
        double expResult = 0.0;
        double result = instance.calculateY(x);
        assertEquals(expResult, result, 0.0);*/
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of calculateX method, of class Line.
     */
    @Test
    public void testCalculateX() {
      /*  System.out.println("calculateX");
        double y = 0.0;
        Line instance = null;
        double expResult = 0.0;
        double result = instance.calculateX(y);
        assertEquals(expResult, result, 0.0);*/
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of equals method, of class Line.
     */
    @Test
    public void testEquals() {
       /* System.out.println("equals");
        Object o = null;
        Line instance = null;
        boolean expResult = false;
        boolean result = instance.equals(o);
        assertEquals(expResult, result);*/
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

}
