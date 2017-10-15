package com.pbm;

import android.database.Cursor;

import java.io.Serializable;

public class Operator implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    private String name;

    public Operator(int id, String name) {
        this.id = id;
        this.name = name;
    }

    static Operator blankOperator() {
        return new Operator(0, "");
    }

    public String toString() {
        return name;
    }

    static Operator newFromDBCursor(Cursor cursor) {
        return new Operator(
            cursor.getInt(cursor.getColumnIndexOrThrow(PBMContract.OperatorContract.COLUMN_ID)),
            cursor.getString(cursor.getColumnIndexOrThrow(PBMContract.OperatorContract.COLUMN_NAME))
        );
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}