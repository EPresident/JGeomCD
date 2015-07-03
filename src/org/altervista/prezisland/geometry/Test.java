/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.altervista.prezisland.geometry;

import java.awt.geom.Point2D;
import org.altervista.prezisland.geometry.algorithms.Sorting;

/**
 *
 * @author EPresident <prez_enquiry@hotmail.com>
 */
public class Test {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
       /* Integer[] nums = {3,90,23,1,32,465,63};
        Sorting.mergeSort(nums);
        for (Integer num : nums) {
            System.out.print(num+",");
        }
        
        Nomber[] noms = {new Nomber(45,"quarantacinque"),new Nomber(1,"uno"),new Nomber(3,"tre"),new Nomber(10200,"diecimiladuecento")};
        Sorting.mergeSort(noms);
        for (Nomber nom : noms) {
            System.out.print(nom+",");
        }*/       
        System.out.println(Math.toDegrees(Geometry.getAngle(new Point2D.Double(0,0),new Point2D.Double(0, 1))));
        System.out.println(Math.toDegrees(Geometry.getAngle(new Point2D.Double(0,1),new Point2D.Double(0, 0))));
        System.out.println(Math.toDegrees(Geometry.getAngle(new Point2D.Double(0,0),new Point2D.Double(-1, 1))));
    }
    
    private static class Nomber implements Comparable<Nomber>{
        int num;
        String name;
        
        Nomber(int n, String nn){
            num=n;
            name=nn;
        }

        @Override
        public int compareTo(Nomber o) {
            Integer I1=num, I2=o.num;
            return I1.compareTo(I2);
        }
        
        public String toString(){
            return "("+num+","+name+")";
        }
    }

}
