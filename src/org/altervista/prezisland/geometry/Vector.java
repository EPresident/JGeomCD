/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.altervista.prezisland.geometry;

import java.awt.geom.Point2D;

/**
 * Vector starting from the origin.
 *
 * @author EPresident <prez_enquiry@hotmail.com>
 */
public class Vector {

    private double vx, vy;
    private double length = -1;
    public static final Vector X_AXIS = new Vector(1, 0);
    public static final Vector Y_AXIS = new Vector(0, 1);

    public Vector(Point2D p1, Point2D p2) {
        vx = getVectorX(p1, p2);
        vy = getVectorY(p1, p2);
    }

    public Vector(double x1, double y1, double x2, double y2) {
        vx = x2 - x1;
        vy = -(y2 - y1);
    }

    public Vector(double vx, double vy) {
        this.vx = vx;
        this.vy = vy;
    }

    private double getVectorX(Point2D p1, Point2D p2) {
        return p2.getX() - p1.getX();
    }

    private double getVectorY(Point2D p1, Point2D p2) {
        return p2.getY() - p1.getY();
    }

    /**
     * @return the length of the Vector.
     */
    public double getLength() {
        if (length < 0) {
            length = Math.sqrt(vx * vx + vy * vy);
        }
        return length;
    }

    /**
     * @return The direction vector for this Vector, i.e. a Vector with the same
     * direction and heading, but with unitary length.
     */
    public Vector getDirectionVector() {
        double len = getLength();
        return new Vector(vx / getLength(), vy / getLength());
    }

    /**
     * @return The left-hand normal vector for this Vector, i.e. a Vector with
     * direction perpendicular to that of the current one, facing "left".
     */
    public Vector getLefthandNormal() {
        return new Vector(-vy, vy);
    }

    /**
     * @return The right-hand normal vector for this Vector, i.e. a Vector with
     * direction perpendicular to that of the current one, facing "right".
     */
    public Vector getRighthandNormal() {
        return new Vector(vx, -vx);
    }

    /**
     * Calculates the dot product of two vectors. The dot product can be used to
     * project a vector on another. If Vector v is unitary, the result is the
     * length of the projection of this Vector on v.
     *
     * @param v The vector acting as second operand of the dot product.
     * @return The dot product of this Vector and Vector v.
     */
    public double getDotProduct(Vector v) {
        //FIXME  System.out.println(""+this.vx+"*"+ v.vx +" + "+ this.vy +"*"+ v.vy);
        return (this.vx * v.vx + this.vy * v.vy);
    }

    /**
     * Get the projection of this Vector onto the axis described by another
     * Vector.
     *
     * @param axis The <u>unit</u> Vector defining the projection axis.
     * @return The projected Vector.
     */
    public Vector projectOnto(Vector axis) {
        double dotpr = getDotProduct(axis);
        //FIXME  System.out.println("DotPr: " + dotpr);
        if (axis.isUnit()) {
            //System.out.println(vx+" "+vy+" "+dotpr);
            return new Vector(axis.vx * dotpr, axis.vy * dotpr);
        } else {
            //FIXME  System.out.println("" + dotpr / axis.getDotProduct(axis) * axis.vx + "," + dotpr / axis.getDotProduct(axis) * axis.vy);
            return new Vector(dotpr / axis.getDotProduct(axis) * axis.vx, dotpr / axis.getDotProduct(axis) * axis.vy);
        }
    }

    public boolean isUnit() {
        double len = getLength();
        return len > 0.99 && len < 1.01;
    }

    /**
     * @return The unit-length version of this vector;
     */
    public Vector makeUnit() {
        double len = getLength();
        return new Vector(vx / len, vy / len);
    }
    
    public void invertVy(){
        vy = -vy;
    }

    public Vector scalarProduct(double d){
        return new Vector(vx*d,vy*d);
    }
    
    @Override
    public boolean equals(Object o) {
        if (o instanceof Vector) {
            Vector v = (Vector) o;
            return vx == v.vx && vy == v.vy;
        }
        return false;
    }

    @Override
    public String toString(){
        return new StringBuilder("(").append(vx).append(",").append(vy)
                .append(")").toString();
    }
    
    public double getVx() {
        return vx;
    }

    public double getVy() {
        return vy;
    }

}
