package com.boguzhai.logic.dao;

import java.util.ArrayList;

/**
 * Created by danster on 3/2/15.
 */

public class Address_1 {
    public String id = "";
    public String name = "";
    public ArrayList<Address_2> child = new ArrayList<Address_2>();

    public Address_1() {}

    public Address_1(String id, String name, ArrayList<Address_2> child) {
        this.id = id; this.name = name; this.child = child;
    }

}