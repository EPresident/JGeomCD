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

import java.awt.BasicStroke;
import org.altervista.prezisland.geometry.shapes.Polygon;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import org.altervista.prezisland.geometry.algorithms.CollisionDetection;

/**
 *
 * @author EPresident <prez_enquiry@hotmail.com>
 */
public class GeomGUI extends javax.swing.JFrame implements MouseListener {

    private static final int POINT_HALFWIDTH = 2;
    private static final double ORIGIN_X_OFFSET = 300, ORIGIN_Y_OFFSET = 200;
    private static final Color TOGGLE_BTN_COLOR = new Color(153, 204, 255),
            TOGGLE_BTN_RUNNING_COLOR = new Color(151, 247, 175),
            TOGGLE_BTN_SELECTED_COLOR = Color.GRAY;
    private Point2D.Double origin;
    private final LinkedList<Polygon> shapes;
    private final Stack<LinkedList<Polygon>> shapeStack;
    private final LinkedList<Point2D.Double> points;
    private final LinkedList<Point2D.Double> vectors;
    private final LinkedList<Point2D.Double> pointBuffer; // used to draw on the gui   
    private final LinkedList<Line> lines;
    private Line direction;
    private boolean dirOrientation;
    private boolean drawPoly, drawDir, stepMode;
    private volatile boolean stepAvailable;

    /**
     * Creates new form GeomGUI
     */
    public GeomGUI() {
        initComponents();
        shapes = new LinkedList<>();
        shapeStack = new Stack<>();
        points = new LinkedList<>();
        origin = new Point2D.Double(0, drawPanel.getHeight());
        vectors = new LinkedList<>();
        pointBuffer = new LinkedList<>();
        lines = new LinkedList<>();
        drawPoly = false;
        drawDir = false;
        stepMode = false;
        stepAvailable = false;
        direction = null;
        dirOrientation = true;
        drawPanel.addMouseListener(this);
        stepBtn.setEnabled(false);

        //  controlsPanel.setVisible(false);
    }

    public Graphics2D getDrawPanelGraphics() {
        return (Graphics2D) drawPanel.getGraphics();
    }

    @Override
    public void paint(Graphics g) {
        /*System.out.println("window size: " + getWidth() + "," + getHeight());
         System.out.println("dpanel size: " + drawPanel.getWidth() + "," + drawPanel.getHeight());*/
        origin.setLocation(drawPanel.getLocation().getX() + ORIGIN_X_OFFSET,
                drawPanel.getHeight() - ORIGIN_Y_OFFSET);
        // System.out.println("origin: " + origin);
        g.clearRect(0, 0, this.getWidth(), getHeight());
        this.paintComponents(g);
        drawGrid(drawPanel.getGraphics());
        for (Line l : lines) {
            drawLine(l, drawPanel.getGraphics());
        }
        if (direction != null && !stepMode) {
            drawLine(direction, drawPanel.getGraphics());
        }
        for (Polygon s : shapes) {
            drawShape(s, drawPanel.getGraphics());
        }
        for (Point2D.Double p : vectors) {
            drawVector(drawPanel.getGraphics(), (int) p.x, (int) p.y);
        }
        for (Point2D.Double p : points) {
            drawPoint(p, drawPanel.getGraphics());
        }
        for (Point2D.Double p : pointBuffer) {
            drawPoint(p, drawPanel.getGraphics());
        }
        drawOrigin();
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

    public void pushStack() {
        shapeStack.push(new LinkedList<>(shapes));
        shapes.clear();
    }

    public void popStack() {
        shapes.clear();
        shapes.addAll(shapeStack.pop());
    }

    public void clearPoints() {
        points.clear();
    }

    public void clearLines() {
        lines.clear();
    }

    public void clearVectors() {
        vectors.clear();
    }

    public void clearShapes() {
        shapes.clear();
    }

    public void clearAll() {
        shapes.clear();
        points.clear();
        vectors.clear();
        lines.clear();
        direction = null;
        pointBuffer.clear();
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
        int maxX = 1000, minX = -1000;
        Color c = g.getColor();
        g.setColor(Color.GREEN);
        /*     System.out.println("origin: " + origin);
         System.out.println("draw: " + (minX) + "," + normalizeY(l.calculateY(minX)) + " - " + (maxX) + "," + normalizeY(l.calculateY(maxX)));*/
        if (l.isVertical()) {
            double x = l.calculateX(0);
            g.drawLine((int) normalizeX(x), (int) normalizeY(minX),
                    (int) normalizeX(x), (int) normalizeY(maxX));
        } else {
            g.drawLine((int) normalizeX(minX), (int) normalizeY(l.calculateY(minX)),
                    (int) normalizeX(maxX), (int) normalizeY(l.calculateY(maxX)));
        }
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
        for (int i = 0; i < drawPanel.getWidth(); i += 10) {
            for (int j = drawPanel.getHeight(); j > -50; j -= 10) {
                g.drawLine(i, j, i + drawPanel.getWidth(), j);
                g.drawLine(i, j, i, j + drawPanel.getHeight());
            }
        }

        g.setColor(Color.gray);
        for (int i = 0; i < drawPanel.getWidth(); i += 50) {
            for (int j = drawPanel.getHeight(); j > -50; j -= 50) {
                g.drawLine(i, j, i + drawPanel.getWidth(), j);
                g.drawLine(i, j, i, j + drawPanel.getHeight());
            }
        }

        g.setColor(Color.black);
        for (int i = 0; i < drawPanel.getWidth(); i += 100) {
            for (int j = drawPanel.getHeight(); j > -100; j -= 100) {
                g.drawLine(i, j, i + drawPanel.getWidth(), j);
                g.drawLine(i, j, i, j + drawPanel.getHeight());
            }
        }

        Graphics2D g2d = (Graphics2D) g;
        Stroke strk = g2d.getStroke();
        g2d.setStroke(new BasicStroke(2));
        g2d.drawLine(-100, (int) origin.y, drawPanel.getWidth() + 10, (int) origin.y);
        g2d.drawLine((int) origin.x, -100, (int) origin.x, drawPanel.getHeight() + 10);
        g2d.setStroke(strk);
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

    private double swingToCartX(double x) {
        return x - origin.x;
    }

    private double swingToCartY(double y) {
        return origin.y - y;
    }

    public synchronized boolean step() {
        //   if (stepMode) {
        if (stepAvailable) {
            stepAvailable = false;           
            return true;
        }
        //}
        return false;
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
        drawPolyTBtn = new javax.swing.JToggleButton();
        resetBtn = new javax.swing.JButton();
        drawDirTBtn = new javax.swing.JButton();
        runBtn = new javax.swing.JButton();
        stepBtn = new javax.swing.JButton();

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

        drawPolyTBtn.setBackground(new java.awt.Color(153, 204, 255));
        drawPolyTBtn.setForeground(new java.awt.Color(0, 0, 0));
        drawPolyTBtn.setText("Draw Polygon");
        drawPolyTBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                drawPolyTBtnActionPerformed(evt);
            }
        });

        resetBtn.setBackground(new java.awt.Color(255, 153, 153));
        resetBtn.setForeground(new java.awt.Color(0, 0, 0));
        resetBtn.setText("RESET");
        resetBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetBtnActionPerformed(evt);
            }
        });

        drawDirTBtn.setBackground(new java.awt.Color(153, 204, 255));
        drawDirTBtn.setForeground(new java.awt.Color(0, 0, 0));
        drawDirTBtn.setText("Draw Direction");
        drawDirTBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                drawDirTBtnActionPerformed(evt);
            }
        });

        runBtn.setBackground(new java.awt.Color(153, 204, 255));
        runBtn.setForeground(new java.awt.Color(0, 0, 0));
        runBtn.setText("Run");
        runBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                runBtnActionPerformed(evt);
            }
        });

        stepBtn.setBackground(new java.awt.Color(204, 255, 204));
        stepBtn.setText("Step");
        stepBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stepBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout controlsPanelLayout = new javax.swing.GroupLayout(controlsPanel);
        controlsPanel.setLayout(controlsPanelLayout);
        controlsPanelLayout.setHorizontalGroup(
            controlsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(controlsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(controlsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(drawDirTBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(drawPolyTBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(controlsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(runBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(resetBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(stepBtn)
                .addContainerGap(534, Short.MAX_VALUE))
        );
        controlsPanelLayout.setVerticalGroup(
            controlsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(controlsPanelLayout.createSequentialGroup()
                .addGroup(controlsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(drawPolyTBtn)
                    .addComponent(resetBtn))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(controlsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(drawDirTBtn)
                    .addComponent(runBtn)
                    .addComponent(stepBtn))
                .addGap(0, 23, Short.MAX_VALUE))
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

    private void drawPolyTBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_drawPolyTBtnActionPerformed
        if (drawPoly) {
            drawPolyTBtn.setBackground(TOGGLE_BTN_COLOR);
            if (pointBuffer.size() > 2) {
                Polygon poly = new Polygon(pointBuffer);
                if (poly.isConvex()) {
                    shapes.add(poly);
                } else {
                    System.err.println("The polygon is not convex.");
                }
            }
            pointBuffer.clear();
            repaint();
        } else {
            drawPolyTBtn.setBackground(TOGGLE_BTN_RUNNING_COLOR);
            pointBuffer.clear();
            if (drawDir) {
                drawDir = false;
            }
        }

        drawPoly = !drawPoly;
    }//GEN-LAST:event_drawPolyTBtnActionPerformed

    private void drawDirTBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_drawDirTBtnActionPerformed
        drawDir = !drawDir;
        drawDirTBtn.setBackground(TOGGLE_BTN_RUNNING_COLOR);
        pointBuffer.clear();
        if (drawPoly) {
            drawPoly = false;
        }
    }//GEN-LAST:event_drawDirTBtnActionPerformed

    private void resetBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetBtnActionPerformed
        clearAll();
        drawDir = false;
        drawPoly = false;
        drawDirTBtn.setSelected(false);
        drawPolyTBtn.setSelected(false);
        drawDirTBtn.setBackground(TOGGLE_BTN_COLOR);
        drawPolyTBtn.setBackground(TOGGLE_BTN_COLOR);
        repaint();
    }//GEN-LAST:event_resetBtnActionPerformed

    private void runBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_runBtnActionPerformed
        if (shapes.size() >= 2) {
            if (shapes.size() > 2) {
                System.out.println("Warning: more than two polygons are present."
                        + " The first two will be used for the algorithm");
            }
            final Polygon p1 = shapes.get(0),
                    p2 = shapes.get(1);
            if (direction != null) {
                if (p1.isConvex() && p2.isConvex()) {
                    System.out.println("Running");
                    stepMode = true;
                    System.out.println("enabled");
                    stepBtn.setEnabled(true);
                    repaint();
                    // All is set: run the algorithm
                    final GeomGUI gui = this;

                    SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {

                        @Override
                        protected Void doInBackground() throws Exception {
                            Point2D.Double ret = CollisionDetection.getPenetrationVectorStep(p1, p2, direction, dirOrientation, gui);
                            Point2D.Double base = p1.getPoints().get(0);
                            direction.traslate(base);
                            double length
                                    = /*CollisionDetection.getPenetrationDepth(
                                            p1, p2, direction, dirOrientation, gui)*/ 
                                    Math.sqrt(Math.pow(ret.x, 2)+Math.pow(ret.y, 2));
                            System.out.println("Pen vect: " + ret + "; length: " + (Math.sqrt(Math.pow(ret.x, 2) + Math.pow(ret.y, 2))));
                            System.out.println("Penetration depth: " + length);
                            Point2D.Double shift = direction.shiftAlongLine(base, length);

                            clearLines();
                            clearVectors();
                            clearPoints();
                            popStack();
                            addVector(ret);
                            while(!step()){
                                Thread.sleep(200);
                            }
                            clearVectors();

                            if (dirOrientation) {
                                p1.traslate(-shift.x + base.x, -shift.y + base.y);
                            } else {
                                p1.traslate(shift.x - base.x, shift.y - base.y);
                            }
                            System.out.println("finalizing.");
                            repaint();
                            stepMode = false;
                            stepAvailable = false;
                            stepBtn.setEnabled(false);
                            System.out.println("Done.\n---------------------------------------------------------------");
                            return null;
                        }
                    };

                    worker.execute();

                    //   Point2D.Double vect = CollisionDetection.getPenetrationVector(p1, p2, direction, dirOrientation, this);
                    //    System.out.println("\n\nPenetration vector: " + vect);
                    // Resolve the collision
                   /* Point2D.Double base = p1.getPoints().get(0);
                     direction.traslate(base);
                     double length = 
                     CollisionDetection.getPenetrationDepth(
                     p1, p2, direction, dirOrientation, this);
                     System.out.println("Penetration depth: " + length);
                     Point2D.Double shift = direction.shiftAlongLine(base, length);
                     System.out.println("Shift: " + shift);
                     if (dirOrientation) {
                     p1.traslate(-shift.x + base.x, -shift.y + base.y);
                     } else {
                     p1.traslate(shift.x - base.x, shift.y - base.y);
                     }*/
                } else {
                    System.err.println("At least one polygon is not convex.");
                }
            } else {
                System.err.println("The algorithm requires a (movement) direction to run.");
            }
        } else {
            System.err.println("The algorithm requires two polygons to run.");
        }

        //resetBtn.doClick();
    }//GEN-LAST:event_runBtnActionPerformed

    private void stepBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stepBtnActionPerformed
        synchronized (this) {
            stepAvailable = true;
        }
    }//GEN-LAST:event_stepBtnActionPerformed

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
    private javax.swing.JButton drawDirTBtn;
    private javax.swing.JPanel drawPanel;
    private javax.swing.JToggleButton drawPolyTBtn;
    private javax.swing.JButton resetBtn;
    private javax.swing.JButton runBtn;
    private javax.swing.JButton stepBtn;
    // End of variables declaration//GEN-END:variables

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (drawPoly || drawDir) {
            pointBuffer.add(new Point2D.Double(swingToCartX(e.getX()), swingToCartY(e.getY())));
            repaint();
        }
        if (drawDir && pointBuffer.size() == 2) {
            // Build direction
            Point2D.Double p1 = pointBuffer.get(0),
                    p2 = pointBuffer.get(1);
            if (p1 != p2) {
                direction = new Line(p1, p2);
                dirOrientation = Geometry.compareLexicographicallyX(p1, p2) < 0;
                //  System.out.println("Line drawn: "+direction+", "+dirOrientation);
                repaint();
            } else {
                System.err.println("Error while drawing a direction: the points "
                        + "inputted are equal.");
            }

            drawDirTBtn.setSelected(false);
            drawDir = false;
            pointBuffer.clear();
            drawDirTBtn.setBackground(TOGGLE_BTN_COLOR);
        }

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
