# Overview

This is a small library that allows to compute the penetration depth between two convex polygons in logaritmic time. It is based on an algorithm by Guibas and Stolfi, adapted following the indications of Dobkin, Hershberger, Kirkpatrick and Suri. This work was originally made for the Computational Geometry course at the University of Udine (Italy), academic year 2014/2015.

## How to use

To run the algorithm you need to draw two convex polygons and a movement direction for the first one:

1. Launch GeomGUI;
2. Draw the first polygon (the one that "moves")
  1. Click "draw polygon";
  2. Draw the points of the first convex polygon **in counter-clockwise order** (this is the "moving" polygon);
  3. Click "draw polygon" again to stop drawing.
3. Repeat steps 2.1-2.3 to draw the second polygon (the "passive" one);
4. Draw the movement direction for the first polygon
  1. Click "draw direction";
  2. Draw two points on the GUI (the drawing order matters);
  3. Direction drawing will stop automatically once two points are drawn.
5. Run the algorithm until completion.
  1. Click "Run" to start running;
  2. Click "Step" to move one step forward in the computation, until done.
  
  
## More info

A brief presentation (in Italian) can be found under the `doc/` folder; it also contains the bibliography.
