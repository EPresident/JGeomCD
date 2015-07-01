/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.altervista.prezisland.geometry;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

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

    /**
     * Test of getAngle method, of class Segment.
     */
    @Test
    public void testGetAngle() {
        System.out.println("Testing getAngle");
        assertEquals(0, new Segment(0, 0, 1, 0).getAngle(), 0);
        assertEquals(180, new Segment(0, 0, -1, 0).getAngle(), 0);
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

}
