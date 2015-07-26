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

import org.altervista.prezisland.geometry.shapes.Polygon;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author EPresident <prez_enquiry@hotmail.com>
 */
public class GeomGUI extends javax.swing.JFrame implements MouseListener {

    private static final int POINT_HALFWIDTH = 2;
    private Polygon activePoly;
    private Point2D.Double origin;
    private final LinkedList<Polygon> shapes;
    private final LinkedList<Point2D.Double> points;
    private final LinkedList<Point2D.Double> vectors;
    private final LinkedList<Line> lines;

    /**
     * Creates new form GeomGUI
     */
    public GeomGUI() {
        initComponents();
        shapes = new LinkedList<>();
        activePoly = null;
        points = new LinkedList<>();
        origin = new Point2D.Double(0, drawPanel.getHeight());
        vectors = new LinkedList<>();
        lines = new LinkedList<>();
        //  controlsPanel.setVisible(false);
    }

    public Graphics2D getDrawPanelGraphics() {
        return (Graphics2D) drawPanel.getGraphics();
    }

    @Override
    public void paint(Graphics g) {
        /*System.out.println("window size: " + getWidth() + "," + getHeight());
         System.out.println("dpanel size: " + drawPanel.getWidth() + "," + drawPanel.getHeight());*/
        origin.setLocation(drawPanel.getLocation().getX() + 100, drawPanel.getHeight() - 100);
        // System.out.println("origin: " + origin);
        g.clearRect(0, 0, this.getWidth(), getHeight());
        this.paintComponents(g);
        drawGrid(drawPanel.getGraphics());
        for (Point2D.Double p : vectors) {
            drawVector(drawPanel.getGraphics(), (int) p.x, (int) p.y);
        }
        for (Line l : lines) {
            drawLine(l, drawPanel.getGraphics());
        }
        for (Polygon s : shapes) {
            drawShape(s, drawPanel.getGraphics());
        }
        for (Point2D.Double p : points) {
            drawPoint(p, drawPanel.getGraphics());
        }
        // drawOrigin();
    }

    public void addShape(Polygon s) {
        shapes.add(s);
    }

    public void addPoint(Point2D.Double p) {
        points.add(p);
    }

    public void addVector(Point2D.Double p) {
        vectors.add(p);
    }

    public void addLine(Line l) {
        lines.add(l);
    }

    private CartesianVector getPenetrationVector(Polygon s1, Polygon s2) {
        Graphics g = drawPanel.getGraphics();
        g.setColor(Color.GREEN);
        LinkedList<CartesianVector> sepAxes = new LinkedList<>();
        //<editor-fold desc="Collect all potentially separating axes">
        //Heuristic: eliminate duplicates of X and Y axis straight away
        int nx = 0, ny = 0;
        for (CartesianVector v : s1.getSeparatingAxes()) {
            if (v.equals(CartesianVector.X_AXIS)) {
                if (++nx > 1) {
                    sepAxes.add(v);
                }
            } else if (v.equals(CartesianVector.Y_AXIS)) {
                if (++ny > 1) {
                    sepAxes.add(v);
                }
            } else {
                sepAxes.add(v);
            }
        }
        for (CartesianVector v : s2.getSeparatingAxes()) {
            if (v.equals(CartesianVector.X_AXIS)) {
                if (++nx > 1) {
                    sepAxes.add(v);
                }
            } else if (v.equals(CartesianVector.Y_AXIS)) {
                if (++ny > 1) {
                    sepAxes.add(v);
                }
            } else {
                sepAxes.add(v);
            }
        }
        //</editor-fold>
        // Project the shapes onto the axes
        LinkedList<CartesianVector> penVectors = new LinkedList<>();
        for (CartesianVector axis : sepAxes) {
            double len1 = 0, len2 = 0, dist = -1;
            ArrayList<CartesianVector> edges1 = new ArrayList<>(Arrays.asList(s1.getEdges())),
                    edges2 = new ArrayList<>(Arrays.asList(s2.getEdges()));
            for (int i = 0; i < edges1.size(); i++) {
                for (int j = 0; j < edges1.size() && j != i; j++) {
                    CartesianVector e1 = edges1.get(i);
                    CartesianVector e2 = edges1.get(j);
                    if (Math.abs(e1.getDotProduct(e2.makeUnit())) == e1.getLength()) {
                        edges1.remove(i);
                        i--;
                        j--;
                    }
                }
            }
            for (int i = 0; i < edges2.size(); i++) {
                for (int j = 0; j < edges2.size() && j != i; j++) {
                    CartesianVector e1 = edges2.get(i);
                    CartesianVector e2 = edges2.get(j);
                    if (Math.abs(e1.getDotProduct(e2.makeUnit())) == e1.getLength()) {
                        edges2.remove(i);
                        i--;
                        j--;
                    }
                }
            }
            for (CartesianVector e : edges1) {
                len1 += e.projectOnto(axis).getLength();
                //System.out.println("1. projecting " + e + " onto " + axis + " :" + e.projectOnto(axis));
            }
            for (CartesianVector e : edges2) {
                len2 += e.projectOnto(axis).getLength();
                //System.out.println("2. projecting " + e + " onto " + axis + " :" + e.projectOnto(axis));
            }
            dist = new CartesianVector(s1.getCenter(), s2.getCenter()).getDotProduct(axis);
            double penLen = (Math.abs(len1) + Math.abs(len2) - Math.abs(dist) * 2) / 2;
            //System.out.println("axis" + axis + " len1: " + len1 + " len2: " + len2 + " dist: " + dist + " penLen: " + penLen); //FIXME
            if (penLen <= 0) {
                return new CartesianVector(0, 0);
            }
            penVectors.add(getPenDirection(s1, s2, axis).scalarProduct(penLen));
        }
        // get minimum penetration vector
        if (penVectors.isEmpty()) {
            return new CartesianVector(0, 0);
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

    private CartesianVector getPenDirection(Polygon s1, Polygon s2, CartesianVector axis) {
        if (!s1.getCenter().equals(s2.getCenter())) {
            CartesianVector v = new CartesianVector(s1.getCenter(), s2.getCenter()).projectOnto(axis)
                    .makeUnit();
            return new CartesianVector(-v.getVx(), -v.getVy());
        }
        return new CartesianVector(0, 0);
    }

    private void drawShape(Polygon s, Graphics g) {
        List<Point2D.Double> pts = s.getPoints();
        if (pts.size() > 1) {
            for (int i = 0; i < pts.size(); i++) {
                Point2D.Double p1 = normalizePoint(pts.get(i)),
                        p2 = normalizePoint(pts.get((i + 1) % pts.size()));
                Color c = g.getColor();
                g.setColor(Color.CYAN);
                g.drawLine((int) p1.x, (int) p1.y, (int) p2.x, (int) p2.y);
                g.setColor(c);
            }
            for (Point2D.Double p : pts) {
                Point2D.Double pp = normalizePoint(p);
                Color c = g.getColor();
                g.setColor(Color.BLUE);
                g.fillRect((int) pp.x - POINT_HALFWIDTH, (int) pp.y - POINT_HALFWIDTH,
                        POINT_HALFWIDTH * 2, POINT_HALFWIDTH * 2);
                //      System.out.println("drawing point ("+p.x+","+p.y+")");
                g.setColor(c);
            }
            Color c = g.getColor();
            g.setColor(Color.black);
            // System.out.println("drawing " + s.getCenter());
            g.fillRect((int) normalizePoint(s.getCenter()).x - POINT_HALFWIDTH,
                    (int) normalizePoint(s.getCenter()).y - POINT_HALFWIDTH,
                    POINT_HALFWIDTH * 2, POINT_HALFWIDTH * 2);
            g.setColor(c);
        }
    }

    private void drawPoint(Point2D.Double p, Graphics g) {
        Color c = g.getColor();
        g.setColor(Color.BLUE);
        g.fillRect((int) normalizePoint(p).x - POINT_HALFWIDTH,
                (int) normalizePoint(p).y - POINT_HALFWIDTH,
                POINT_HALFWIDTH * 2, POINT_HALFWIDTH * 2);
        g.setColor(c);
    }

    private void drawOrigin() {
        Graphics g = drawPanel.getGraphics();
        Color cc = g.getColor();
        g.setColor(Color.RED);
        g.fillRect((int) origin.x - POINT_HALFWIDTH,
                (int) origin.y - POINT_HALFWIDTH,
                POINT_HALFWIDTH * 2, POINT_HALFWIDTH * 2);
        g.setColor(cc);
    }

    private void drawPoint(Point2D.Double p, Graphics g, Color c) {
        Color cc = g.getColor();
        g.setColor(c);
        g.fillRect((int) normalizePoint(p).x - POINT_HALFWIDTH,
                (int) normalizePoint(p).y - POINT_HALFWIDTH,
                POINT_HALFWIDTH * 2, POINT_HALFWIDTH * 2);
        g.setColor(cc);
    }

    private void drawLine(Line l, Graphics g) {
        int maxX = 500, minX = -100;
        Color c = g.getColor();
        g.setColor(Color.GREEN);
   /*     System.out.println("origin: " + origin);
        System.out.println("draw: " + (minX) + "," + normalizeY(l.calculateY(minX)) + " - " + (maxX) + "," + normalizeY(l.calculateY(maxX)));*/
        g.drawLine((int)normalizeX(minX), (int) normalizeY(l.calculateY(minX)), 
                (int)normalizeX(maxX), (int) normalizeY(l.calculateY(maxX)));
        g.setColor(c);
    }

    private void drawGrid(Graphics g) {
        /*   g.setColor(Color.lightGray);
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
         }*/
        g.setColor(Color.lightGray);
        for (int i = 0; i < drawPanel.getWidth(); i += 50) {
            for (int j = (int) origin.y; j > -50; j -= 50) {
                g.drawLine(i, j, i + drawPanel.getWidth(), j);
                g.drawLine(i, j, i, j + drawPanel.getHeight());
            }
        }
        g.setColor(Color.gray);
        for (int i = 0; i < drawPanel.getWidth(); i += 100) {
            for (int j = (int) origin.y; j > -100; j -= 100) {
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

    private void drawVector(Graphics g, int x, int y) {
        int vectTipLen = 5;
        Point2D.Double p = normalizePoint(new Point2D.Double(x, y));
        g.setColor(Color.red);
        g.drawLine((int) origin.x, (int) origin.y, (int) p.x, (int) p.y);
        /*if (p.x < x) {
         g.drawLine(x, y, x - vectTipLen, y - vectTipLen);
         g.drawLine(x, y, x - vectTipLen, y + vectTipLen);
         } else if (p.x > x) {
         g.drawLine(x, y, x + vectTipLen, y - vectTipLen);
         g.drawLine(x, y, x + vectTipLen, y + vectTipLen);
         } else if (p.y < y) {
         g.drawLine(x, y, x + vectTipLen, y - vectTipLen);
         g.drawLine(x, y, x - vectTipLen, y - vectTipLen);
         } else {
         g.drawLine(x, y, x - vectTipLen, y + vectTipLen);
         g.drawLine(x, y, x + vectTipLen, y + vectTipLen);
         }*/
    }

    private Point2D.Double normalizePoint(final Point2D.Double p) {
        double pX = origin.x + p.x, pY = origin.y - p.y;
        return new Point2D.Double(pX, pY);
    }

    private double normalizeY(double y) {
        return origin.y - y;
    }
    
    private double normalizeX(double x) {
        return origin.x + x;
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
        controlsPanel = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Geometry GUI");
        setBackground(new java.awt.Color(255, 0, 51));
        setBounds(new java.awt.Rectangle(0, 0, 800, 600));
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setMinimumSize(new java.awt.Dimension(200, 150));
        setPreferredSize(new java.awt.Dimension(800, 600));
        setSize(new java.awt.Dimension(800, 600));

        drawPanel.setBackground(new java.awt.Color(255, 255, 212));
        drawPanel.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        drawPanel.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
        drawPanel.setMinimumSize(new java.awt.Dimension(200, 200));
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
            .addGap(0, 0, Short.MAX_VALUE)
        );

        controlsPanel.setBackground(drawPanel.getBackground());
        controlsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Controls", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));
        controlsPanel.setMaximumSize(new java.awt.Dimension(2147483647, 200));
        controlsPanel.setMinimumSize(new java.awt.Dimension(200, 100));
        controlsPanel.setPreferredSize(new java.awt.Dimension(800, 100));

        javax.swing.GroupLayout controlsPanelLayout = new javax.swing.GroupLayout(controlsPanel);
        controlsPanel.setLayout(controlsPanelLayout);
        controlsPanelLayout.setHorizontalGroup(
            controlsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 788, Short.MAX_VALUE)
        );
        controlsPanelLayout.setVerticalGroup(
            controlsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 78, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(controlsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(drawPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(drawPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addComponent(controlsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
    private javax.swing.JPanel controlsPanel;
    private javax.swing.JPanel drawPanel;
    // End of variables declaration//GEN-END:variables

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        Point2D p = e.getPoint();
        if (activePoly != null) {
            ArrayList<Point2D.Double> pts = activePoly.getPoints();
            double offsetX = p.getX() - pts.get(0).x,
                    offsetY = p.getY() - pts.get(0).y;
            activePoly.traslate(offsetX, offsetY);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
