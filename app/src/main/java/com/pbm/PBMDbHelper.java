package com.pbm;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.pbm.PBMContract.ConditionContract;
import static com.pbm.PBMContract.LocationContract;
import static com.pbm.PBMContract.LocationMachineXrefContract;
import static com.pbm.PBMContract.LocationTypeContract;
import static com.pbm.PBMContract.MachineContract;
import static com.pbm.PBMContract.MachineScoreContract;
import static com.pbm.PBMContract.OperatorContract;
import static com.pbm.PBMContract.ZoneContract;
import static com.pbm.PBMContract.RegionContract;

class PBMDbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "pbm.db";

    PBMDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(LocationContract.CREATE_TABLE);
        db.execSQL(LocationTypeContract.CREATE_TABLE);
        db.execSQL(LocationMachineXrefContract.CREATE_TABLE);
        db.execSQL(ConditionContract.CREATE_TABLE);
        db.execSQL(MachineContract.CREATE_TABLE);
        db.execSQL(MachineScoreContract.CREATE_TABLE);
        db.execSQL(OperatorContract.CREATE_TABLE);
        db.execSQL(ZoneContract.CREATE_TABLE);
        db.execSQL(RegionContract.CREATE_TABLE);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(LocationContract.DELETE_TABLE);
        db.execSQL(LocationTypeContract.DELETE_TABLE);
        db.execSQL(LocationMachineXrefContract.DELETE_TABLE);
        db.execSQL(ConditionContract.DELETE_TABLE);
        db.execSQL(MachineContract.DELETE_TABLE);
        db.execSQL(MachineScoreContract.DELETE_TABLE);
        db.execSQL(OperatorContract.DELETE_TABLE);
        db.execSQL(ZoneContract.DELETE_TABLE);
        db.execSQL(RegionContract.DELETE_TABLE);

        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    void removeAll() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(LocationContract.TABLE_NAME, null, null);
        db.delete(LocationTypeContract.TABLE_NAME, null, null);
        db.delete(LocationMachineXrefContract.TABLE_NAME, null, null);
        db.delete(ConditionContract.TABLE_NAME, null, null);
        db.delete(MachineContract.TABLE_NAME, null, null);
        db.delete(MachineScoreContract.TABLE_NAME, null, null);
        db.delete(OperatorContract.TABLE_NAME, null, null);
        db.delete(ZoneContract.TABLE_NAME, null, null);
        db.delete(RegionContract.TABLE_NAME, null, null);
    }
}
