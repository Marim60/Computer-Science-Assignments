/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package PositionalIndex;

import java.util.TreeSet;

/**
 *
 * @author ehab
 */
 
public class Posting {

    public Posting next = null;
    public TreeSet<Integer> positions;
    int docId;
    int dtf = 1;

    Posting(int id, int t) {
        docId = id;
        dtf=t;
        positions = new TreeSet<>();
    }
    
    Posting(int id) {
        docId = id;
        positions = new TreeSet<>();
    }
}