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
import java.util.List;
import org.altervista.prezisland.geometry.shapes.Polygon;

/**
 *
 * @author EPresident <prez_enquiry@hotmail.com>
 */
public class CollisionDetection {

    /*  private Point2D.Double getEndpoints(Polygon poly, boolean upper) {
     // Minimum Y point must be first
     ArrayList<Point2D.Double> pts = poly.getPoints();
     double maxY = pts.get(0).y;
     int l = 0, r = pts.size() - 1, m = -1;
     do {
            
     } while (l-r>0);
     return null;
     }*/
    public double getPenetrationAmount(Polygon P1, Polygon P2, int pMin, int pMax, int qMin, int qMax) {
        Polygon P = new Polygon(P1.getPoints()), Q = new Polygon(P2.getPoints());
        // Reference point for P
        Point2D.Double x = P.getPoints().get(0);
        // Reference point for Q
        Point2D.Double y = Q.getPoints().get(0);

        // Normalize polygons to the origin
        P.traslate(-x.x, -x.y);
        Q.traslate(-y.x, -y.y);

        /* ---------------------------
            Algorithm begins here
         ---------------------------*/
        
        // Left shadow of P
        List<Point2D.Double> A = P.getPoints().subList(pMin, pMax+1);
        // Right shadow of Q (inverted)
        List<Point2D.Double> B = Q.getPoints().subList(qMax, Q.getPointsNumber());
        B.addAll(B.subList(0, qMin+1));
        // z = y - x   <- the point to test the shadows' convolution against
        Point2D.Double z = new Point2D.Double(y.x - x.x, y.y - x.y);

        return 0;
    }
    
    /**
     * Given two left shadows A and B, discriminate w agains A*B
     * Time O(log n)
     * @param A left shadow
     * @param B left shadow (reversed right shadow)
     * @param w point
     * @return pen amount
     */
    public double guibasStolfi(Polygon A, Polygon B, Point2D.Double w){
        Point2D.Double a1=A.getPoints().get(0), 
                a2=A.getPoints().get(A.getPoints().size()-1),
                b1=A.getPoints().get(0), 
                b2=A.getPoints().get(A.getPoints().size()-1);
        int f = A.getPoints().size()/2, g = B.getPoints().size()/2;
        
        return 0;
    }

}
