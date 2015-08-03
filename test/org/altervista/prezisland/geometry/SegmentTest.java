/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.altervista.prezisland.geometry;

import java.awt.geom.Point2D;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 * @author EPresident <prez_enquiry@hotmail.com>
 */
public class SegmentTest {

    public SegmentTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    /**
     * Test of getLength method, of class Segment.
     */
    @Test
    public void testGetLength() {
        System.out.println("Testing getLength");
        assertEquals(1, new Segment(0, 0, 1, 0).getLength(), 0.0);
        assertEquals(5, new Segment(0, 0, 0, 5).getLength(), 0.0);
    }

    /**
     * Test of testAgainst method, of class Segment.
     */
    @Test
    public void testTestAgainst() {
        System.out.println(" --- testAgainst --- ");
        Segment instance = new Segment(-1, -1, 1, 1);
        Point2D.Double[] points = {new Point2D.Double(0, 1), new Point2D.Double(0, -1),
            new Point2D.Double(1, 1), new Point2D.Double(2, 2), new Point2D.Double(-2, -2)};
        Segment.Position[] expResults = {Segment.Position.LEFT, Segment.Position.RIGHT,
            Segment.Position.COLLIDES, Segment.Position.COLLINEAR_ABOVE,
            Segment.Position.COLLINEAR_BELOW};
        for (int i = 0; i < points.length; i++) {
            System.out.println("\ni: " + i + " " + instance);
            System.out.println("point: " + points[i]);
            Segment.Position result = (Segment.Position) instance.testAgainst(points[i]);
            System.out.println("expected: " + expResults[i] + "; result: " + result);
            assertEquals(expResults[i], result);
        }

        instance = new Segment(-1, 0, 1, 0);
        points = new Point2D.Double[]{new Point2D.Double(0, 1), new Point2D.Double(0, -1),
            new Point2D.Double(-1, 0), new Point2D.Double(2, 0), new Point2D.Double(-2, 0)};
        expResults = new Segment.Position[]{Segment.Position.LEFT, Segment.Position.RIGHT,
            Segment.Position.COLLIDES, Segment.Position.COLLINEAR_ABOVE,
            Segment.Position.COLLINEAR_BELOW};
        for (int i = 0; i < points.length; i++) {
            System.out.println("\ni: " + i + " " + instance);
            System.out.println("point: " + points[i]);
            Segment.Position result = (Segment.Position) instance.testAgainst(points[i]);
            System.out.println("expected: " + expResults[i] + "; result: " + result);
            assertEquals(expResults[i], result);
        }

        instance = new Segment(0, 1, 0, -1);
        points = new Point2D.Double[]{new Point2D.Double(-1, 0), new Point2D.Double(1, 0),
            new Point2D.Double(0, 0), new Point2D.Double(0, 2), new Point2D.Double(0, -2)};
        expResults = new Segment.Position[]{Segment.Position.LEFT, Segment.Position.RIGHT,
            Segment.Position.COLLIDES, Segment.Position.COLLINEAR_ABOVE,
            Segment.Position.COLLINEAR_BELOW};
        for (int i = 0; i < points.length; i++) {
            System.out.println("\ni: " + i + " " + instance);
            System.out.println("point: " + points[i]);
            Segment.Position result = (Segment.Position) instance.testAgainst(points[i]);
            System.out.println("expected: " + expResults[i] + "; result: " + result);
            assertEquals(expResults[i], result);
        }

        instance = new Segment(1, -1, -1, 1);
        points = new Point2D.Double[]{new Point2D.Double(0, -11), new Point2D.Double(0, 11),
            new Point2D.Double(-1, 1), new Point2D.Double(2, -2), new Point2D.Double(-2, 2)};
        expResults = new Segment.Position[]{Segment.Position.LEFT, Segment.Position.RIGHT,
            Segment.Position.COLLIDES, Segment.Position.COLLINEAR_ABOVE,
            Segment.Position.COLLINEAR_BELOW};
        for (int i = 0; i < points.length; i++) {
            System.out.println("\ni: " + i + " " + instance);
            System.out.println("point: " + points[i]);
            Segment.Position result = (Segment.Position) instance.testAgainst(points[i]);
            System.out.println("expected: " + expResults[i] + "; result: " + result);
            assertEquals(expResults[i], result);
        }

        // Tollerance test
        instance = new Segment(0, 0, 1, 0);
        Segment.Position result = (Segment.Position) instance.testAgainst(
                new Point2D.Double(1.0001, 0));
        assertEquals(Segment.Position.COLLIDES, result);

        System.out.println("\n");
    }

    /**
     * Test of toString method, of class Segment.
     */
    @Test
    public void testToString() {
        System.out.println("toString");
        Segment instance = null;
        String expResult = "";
        String result = instance.toString();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of equals method, of class Segment.
     */
    @Test
    public void testEquals() {
        System.out.println("equals");
        Object o = null;
        Segment instance = null;
        boolean expResult = false;
        boolean result = instance.equals(o);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of calculateX method, of class Segment.
     */
    @Test
    public void testCalculateX() {
        System.out.println("calculateX");
        double y = 0.0;
        Segment instance = null;
        double expResult = 0.0;
        double result = instance.calculateX(y);
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of calculateY method, of class Segment.
     */
    @Test
    public void testCalculateY() {
        System.out.println("calculateY");
        double x = 0.0;
        Segment instance = null;
        double expResult = 0.0;
        double result = instance.calculateY(x);
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of testIntersection method, of class Segment.
     */
    @Test
    public void testTestIntersection() {
        System.out.println("testIntersection");
        Line l = null;
        Segment instance = null;
        Point2D.Double expResult = null;
        Point2D.Double result = instance.testIntersection(l);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getP1 method, of class Segment.
     */
    @Test
    public void testGetP1() {
        System.out.println("getP1");
        Segment instance = null;
        Point2D.Double expResult = null;
        Point2D.Double result = instance.getP1();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getP2 method, of class Segment.
     */
    @Test
    public void testGetP2() {
        System.out.println("getP2");
        Segment instance = null;
        Point2D.Double expResult = null;
        Point2D.Double result = instance.getP2();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

}
