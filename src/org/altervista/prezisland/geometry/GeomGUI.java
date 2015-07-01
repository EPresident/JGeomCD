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

import org.altervista.prezisland.geometry.shapes.Shape;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author EPresident <prez_enquiry@hotmail.com>
 */
public class GeomGUI extends javax.swing.JFrame {

    private static final int POINT_HALFWIDTH = 2;
    private final LinkedList<Shape> shapes;

    /**
     * Creates new form GeomGUI
     */
    public GeomGUI() {
        initComponents();
        shapes = new LinkedList<>();
    }

    public Graphics2D getDrawPanelGraphics() {
        return (Graphics2D) drawPanel.getGraphics();
    }

    @Override
    public void paint(Graphics g) {
        g.clearRect(0, 0, this.getWidth(), getHeight());
        this.paintComponents(g);
        drawGrid(drawPanel.getGraphics());
        for (Shape s : shapes) {
            drawShape(s, drawPanel.getGraphics());
        }
        for (int i = 0; i < shapes.size() - 1; i++) {
            g.setColor(Color.red);
            Vector pv = getPenetrationVector(shapes.get(i), shapes.get(i + 1));
            System.out.println(pv);
            drawVector(drawPanel.getGraphics(), (int) shapes.get(i).getPoints().get(0).x,
                    (int) shapes.get(i).getPoints().get(0).y,
                    (int) (shapes.get(i).getPoints().get(0).x + pv.getVx()),
                    (int) (shapes.get(i).getPoints().get(0).y + pv.getVy()));
        }
    }

    public void addShape(Shape s) {
        shapes.add(s);
    }

    private Vector getPenetrationVector(Shape s1, Shape s2) {
        Graphics g = drawPanel.getGraphics();
        g.setColor(Color.GREEN);
        LinkedList<Vector> sepAxes = new LinkedList<>();
        //<editor-fold desc="Collect all potentially separating axes">
        //Heuristic: eliminate duplicates of X and Y axis straight away
        int nx = 0, ny = 0;
        for (Vector v : s1.getSeparatingAxes()) {
            if (v.equals(Vector.X_AXIS)) {
                if (++nx > 1) {
                    sepAxes.add(v);
                }
            } else if (v.equals(Vector.Y_AXIS)) {
                if (++ny > 1) {
                    sepAxes.add(v);
                }
            } else {
                sepAxes.add(v);
            }
        }
        for (Vector v : s2.getSeparatingAxes()) {
            if (v.equals(Vector.X_AXIS)) {
                if (++nx > 1) {
                    sepAxes.add(v);
                }
            } else if (v.equals(Vector.Y_AXIS)) {
                if (++ny > 1) {
                    sepAxes.add(v);
                }
            } else {
                sepAxes.add(v);
            }
        }
        //</editor-fold>
        // Project the shapes onto the axes
        LinkedList<Vector> penVectors = new LinkedList<>();
        for (Vector axis : sepAxes) {
            double len1 = 0, len2 = 0, dist = -1;
            ArrayList<Vector> edges1 = new ArrayList<>(Arrays.asList(s1.getEdges())),
                    edges2 = new ArrayList<>(Arrays.asList(s2.getEdges()));
            for (int i = 0; i < edges1.size(); i++) {
                for (int j = 0; j < edges1.size() && j != i; j++) {
                    Vector e1 = edges1.get(i), e2 = edges1.get(j);
                    if (Math.abs(e1.getDotProduct(e2.makeUnit())) == e1.getLength()) {
                        edges1.remove(i);
                        i--;
                        j--;
                    }
                }
            }
            for (int i = 0; i < edges2.size(); i++) {
                for (int j = 0; j < edges2.size() && j != i; j++) {
                    Vector e1 = edges2.get(i), e2 = edges2.get(j);
                    if (Math.abs(e1.getDotProduct(e2.makeUnit())) == e1.getLength()) {
                        edges2.remove(i);
                        i--;
                        j--;
                    }
                }
            }
            for (Vector e : edges1) {
                len1 += e.projectOnto(axis).getLength();
                //System.out.println("1. projecting " + e + " onto " + axis + " :" + e.projectOnto(axis));
            }
            for (Vector e : edges2) {
                len2 += e.projectOnto(axis).getLength();
                //System.out.println("2. projecting " + e + " onto " + axis + " :" + e.projectOnto(axis));
            }
            dist = new Vector(s1.getCenter(), s2.getCenter()).getDotProduct(axis);
            double penLen = (Math.abs(len1) + Math.abs(len2) - Math.abs(dist) * 2) / 2;
            //System.out.println("axis" + axis + " len1: " + len1 + " len2: " + len2 + " dist: " + dist + " penLen: " + penLen); //FIXME
            if (penLen <= 0) {
                return new Vector(0, 0);
            }
            penVectors.add(getPenDirection(s1, s2, axis).scalarProduct(penLen));
        }
        // get minimum penetration vector
        if (penVectors.isEmpty()) {
            return new Vector(0, 0);
        } else {
            int index = 0;
            for (int i = 1; i < penVectors.size(); i++) {
                if (penVectors.get(i).getLength() < penVectors.get(index).getLength()) {
                    index = i;
                }
            }
            return penVectors.get(index);
        }
    }

    private Vector getPenDirection(Shape s1, Shape s2, Vector axis) {
        if (!s1.getCenter().equals(s2.getCenter())) {
            Vector v = new Vector(s1.getCenter(), s2.getCenter()).projectOnto(axis)
                    .makeUnit();
            return new Vector(-v.getVx(), -v.getVy());
        }
        return new Vector(0,0);
    }

    private void drawShape(Shape s, Graphics g) {
        List<Point2D.Double> pts = s.getPoints();
        if (pts.size() > 1) {
            for (int i = 0; i < pts.size(); i++) {
                Point2D.Double p1 = pts.get(i), p2 = pts.get((i + 1) % pts.size());
                Color c = g.getColor();
                g.setColor(Color.CYAN);
                g.drawLine((int) p1.x, (int) p1.y, (int) p2.x, (int) p2.y);
                g.setColor(c);
            }
            for (Point2D.Double p : pts) {
                Color c = g.getColor();
                g.setColor(Color.BLUE);
                g.fillRect((int) p.x - POINT_HALFWIDTH, (int) p.y - POINT_HALFWIDTH,
                        POINT_HALFWIDTH * 2, POINT_HALFWIDTH * 2);
                //      System.out.println("drawing point ("+p.x+","+p.y+")");
                g.setColor(c);
            }
            Color c = g.getColor();
            g.setColor(Color.black);
            g.fillRect((int) s.getCenter().x - POINT_HALFWIDTH, (int) s.getCenter().y - POINT_HALFWIDTH,
                    POINT_HALFWIDTH * 2, POINT_HALFWIDTH * 2);
            g.setColor(c);
        }
    }

    private void drawGrid(Graphics g) {
        g.setColor(Color.lightGray);
        for (int i = 0; i < drawPanel.getWidth(); i += 50) {
            for (int j = 0; j < drawPanel.getHeight(); j += 50) {
                g.drawLine(i, j, i + drawPanel.getWidth(), j);
                g.drawLine(i, j, i, j + drawPanel.getHeight());
            }
        }
        g.setColor(Color.gray);
        for (int i = 0; i < drawPanel.getWidth(); i += 100) {
            for (int j = 0; j < drawPanel.getHeight(); j += 100) {
                g.drawLine(i, j, i + drawPanel.getWidth(), j);
                g.drawLine(i, j, i, j + drawPanel.getHeight());
            }
        }
    }

    private void drawVector(Graphics g, int x1, int y1, int x2, int y2) {
        int vectTipLen = 5;
        g.setColor(Color.red);
        g.drawLine(x1, y1, x2, y2);
        if (x1 < x2) {
            g.drawLine(x2, y2, x2 - vectTipLen, y2 - vectTipLen);
            g.drawLine(x2, y2, x2 - vectTipLen, y2 + vectTipLen);
        } else if (x1 > x2) {
            g.drawLine(x2, y2, x2 + vectTipLen, y2 - vectTipLen);
            g.drawLine(x2, y2, x2 + vectTipLen, y2 + vectTipLen);
        } else if (y1 < y2) {
            g.drawLine(x2, y2, x2 + vectTipLen, y2 - vectTipLen);
            g.drawLine(x2, y2, x2 - vectTipLen, y2 - vectTipLen);
        } else {
            g.drawLine(x2, y2, x2 - vectTipLen, y2 + vectTipLen);
            g.drawLine(x2, y2, x2 + vectTipLen, y2 + vectTipLen);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        drawPanel = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Geometry GUI");
        setBackground(new java.awt.Color(255, 0, 51));
        setBounds(new java.awt.Rectangle(0, 0, 800, 600));
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setMinimumSize(new java.awt.Dimension(200, 150));
        setSize(new java.awt.Dimension(800, 600));

        drawPanel.setBackground(new java.awt.Color(255, 255, 212));
        drawPanel.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        drawPanel.setName("drawPanel"); // NOI18N
        drawPanel.setPreferredSize(new java.awt.Dimension(800, 500));
        drawPanel.setVerifyInputWhenFocusTarget(false);

        javax.swing.GroupLayout drawPanelLayout = new javax.swing.GroupLayout(drawPanel);
        drawPanel.setLayout(drawPanelLayout);
        drawPanelLayout.setHorizontalGroup(
            drawPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        drawPanelLayout.setVerticalGroup(
            drawPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 497, Short.MAX_VALUE)
        );

        jPanel1.setBackground(drawPanel.getBackground());
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Controls", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));
        jPanel1.setMinimumSize(new java.awt.Dimension(100, 50));
        jPanel1.setPreferredSize(new java.awt.Dimension(800, 100));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 788, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 74, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(drawPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(drawPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 497, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 97, Short.MAX_VALUE))
        );

        getAccessibleContext().setAccessibleDescription("");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(GeomGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(GeomGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(GeomGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(GeomGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new GeomGUI().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel drawPanel;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables
}
