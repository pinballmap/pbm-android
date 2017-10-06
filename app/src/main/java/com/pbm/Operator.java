package com.pbm;

import android.database.Cursor;

import java.io.Serializable;

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

    public static Operator newFromDBCursor(Cursor cursor) {
        return new Operator(
                cursor.getInt(cursor.getColumnIndexOrThrow(PBMContract.OperatorContract.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(PBMContract.OperatorContract.COLUMN_NAME))
        );
    }
}