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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author EPresident <prez_enquiry@hotmail.com>
 */
public class GeometryTest {
    
    public GeometryTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }

    /**
     * Test of isLeftTurn method, of class Geometry.
     */
    @Test
    public void testIsLeftTurn() {
        System.out.println("isLeftTurn");
        assertTrue(Geometry.isLeftTurn(new Point2D.Double(0, 0), new Point2D.Double(1, 0), new Point2D.Double(2, 1)));
        assertFalse(Geometry.isLeftTurn(new Point2D.Double(0, 0), new Point2D.Double(1, 0), new Point2D.Double(2, 0)));
        assertFalse(Geometry.isLeftTurn(new Point2D.Double(0, 0), new Point2D.Double(1, 0), new Point2D.Double(2, -1)));
        assertTrue(Geometry.isLeftTurn(new Point2D.Double(0, 0), new Point2D.Double(1, 0), new Point2D.Double(0, 1)));
        assertFalse(Geometry.isLeftTurn(new Point2D.Double(0, 0), new Point2D.Double(1, 0), new Point2D.Double(0, -1)));
        assertFalse(Geometry.isLeftTurn(new Point2D.Double(0, 0), new Point2D.Double(0, 0), new Point2D.Double(0, 0)));
        // Degenerate case
        //assertFalse(Geometry.isLeftTurn(new Point2D.Double(0,0), new Point2D.Double(1,0), new Point2D.Double(0,0)));
    }

    /**
     * Test of getAngle method, of class Geometry.
     */
    @Test
    public void testGetAngle() {
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getLength method, of class Geometry.
     */
    @Test
    public void testGetLength() {
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of compareLexicographicallyX method, of class Geometry.
     */
    @Test
    public void testCompareLexicographicallyX() {
        assertEquals(-1, Geometry.compareLexicographicallyX(new Point2D.Double(0, 1), new Point2D.Double(1, 1)));
        assertEquals(1, Geometry.compareLexicographicallyX(new Point2D.Double(2, 1), new Point2D.Double(0, 0)));
        assertEquals(1, Geometry.compareLexicographicallyX(new Point2D.Double(0, 1), new Point2D.Double(0, 0)));
        assertEquals(-1, Geometry.compareLexicographicallyX(new Point2D.Double(3, 2), new Point2D.Double(3, 5)));
        assertEquals(0, Geometry.compareLexicographicallyX(new Point2D.Double(0, 1), new Point2D.Double(0, 1)));
    }


    /**
     * Test of compareLexicographicallyY method, of class Geometry.
     */
    @Test
    public void testCompareLexicographicallyY() {
        System.out.println("compareLexicographicallyY");
        Point2D.Double p1 = null;
        Point2D.Double p2 = null;
        int expResult = 0;
        int result = Geometry.compareLexicographicallyY(p1, p2);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of sortLexicographicallyX method, of class Geometry.
     */
    @Test
    public void testSortLexicographicallyX() {
        System.out.println("sortLexicographicallyX");
        Point2D.Double[] pts = {new Point2D.Double(0, 1), new Point2D.Double(1, 1),
            new Point2D.Double(2, 1), new Point2D.Double(0, 0),
            new Point2D.Double(0, 1), new Point2D.Double(0, 0),
            new Point2D.Double(3, 2), new Point2D.Double(3, 5),
            new Point2D.Double(0, 1), new Point2D.Double(0, 1)};
        ArrayList<Point2D.Double> lpts=new ArrayList<>();
        for(Point2D.Double p : pts){
            lpts.add(p);
        }
        System.out.println(lpts.size());
        Geometry.sortLexicographicallyX(pts);
        Geometry.sortLexicographicallyX(lpts);
        System.out.println("dik");
        for(int i = 0; i<pts.length-1; i++){
            assertTrue(Geometry.compareLexicographicallyX(pts[i], pts[i+1])<=0);         
            assertTrue(Geometry.compareLexicographicallyX(lpts.get(i), lpts.get(i+1))<=0);
        }   
    }

}
