/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.altervista.prezisland.geometry;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

/**
 * Class representing 2D segments with double precision.
 *
 * @author EPresident <prez_enquiry@hotmail.com>
 */
public class Segment extends Line2D.Double {

    public Segment(double x1, double y1, double x2, double y2) {
        super(x1, y1, x2, y2);
    }

    public Segment(Point2D.Double p1, Point2D.Double p2) {
        super(p1, p2);
    }

    /**
     * @return Angle in radians
     */
    public double getAngle() {
        return Math.toDegrees(Math.atan2(y2 - y1, x2 - x1));
    }

    public double getAngleDeg() {
        return Math.toDegrees(Math.atan2(y2 - y1, x2 - x1));
    }

    public double getLength() {
        return Math.sqrt(Math.pow(Math.abs(x1 - x2), 2) + Math.pow(Math.abs(y1 - y2), 2));
    }
}
