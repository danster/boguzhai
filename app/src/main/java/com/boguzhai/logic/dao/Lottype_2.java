package com.boguzhai.logic.dao;

import java.util.ArrayList;

/**
 * Created by danster on 3/2/15.
 */

public class Lottype_2 {
    public String id = "";
    public String name = "";
    public ArrayList<Lottype_3> child = new ArrayList<Lottype_3>();

    public Lottype_2() {}
    public Lottype_2(String id, String name, ArrayList<Lottype_3> child) {
        this.id = id; this.name = name; this.child = child;
    }

}