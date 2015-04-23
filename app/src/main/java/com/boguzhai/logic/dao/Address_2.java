package com.boguzhai.logic.dao;

import java.util.ArrayList;

/**
 * Created by danster on 3/2/15.
 */

public class Address_2 {
    public String id = "";
    public String name = "";
    public ArrayList<Address_3> child = new ArrayList<Address_3>();

    public Address_2() {}
    public Address_2(String id, String name, ArrayList<Address_3> child) {
        this.id = id; this.name = name; this.child = child;
    }

}