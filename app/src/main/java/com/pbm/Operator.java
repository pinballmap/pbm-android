package com.pbm;

import java.io.Serializable;
import java.lang.String;

public class Operator implements Serializable {
    private static final long serialVersionUID = 1L;
    public int id;
    public String name;

    public Operator(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static Operator blankOperator() {
        return new Operator(0, "");
    }

    public String toString() {
        return name;
    }
}