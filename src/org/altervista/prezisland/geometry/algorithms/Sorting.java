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

/**
 * Sorting algorithms here.
 *
 * @author EPresident <prez_enquiry@hotmail.com>
 */
public class Sorting {

    public static void mergeSort(Comparable[] V) {
        mergeSort(V, 0, V.length - 1);
    }

    public static void mergeSort(Comparable[] V, int p, int r) {
        if (p < r) {
            int q = (int) Math.floor((p + r) / 2);
            mergeSort(V, p, q);
            mergeSort(V, q + 1, r);
            merge(V, p, q, r);
        }
    }

    public static void merge(Comparable[] V, int p, int q, int r) {
        Comparable[] T = new Comparable[q - p + 2];
        for (int c = 0; c < T.length - 1; c++) {
            T[c] = V[p + c];
        }
        Comparable[] U = new Comparable[r - q + 1];
        for (int c = 0; c < U.length - 1; c++) {
            U[c] = V[q + 1 + c];
        }
        T[T.length - 1] = 999999;
        U[U.length - 1] = 999999;
        int i = 0, j = 0;
        for (int k = p; k <= r; k++) {
            if (T[i].compareTo(U[j]) <= 0) {
                V[k] = T[i];
                i++;
            } else {
                V[k] = U[j];
                j++;
            }
        }
    }
}
