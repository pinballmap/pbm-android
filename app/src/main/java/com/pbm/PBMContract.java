package com.pbm;

import android.provider.BaseColumns;

import static com.pbm.PBMContract.MachineContract.COLUMN_EXISTS_IN_REGION;

public final class PBMContract {
    private PBMContract() {}

    static class ConditionContract implements BaseColumns {
        static final String TABLE_NAME = "location_machine_conditions";

        public static final String COLUMN_ID = "id";
        static final String COLUMN_LOCATION_MACHINE_XREF_ID = "location_machine_xref_id";

        static final String COLUMN_DATE = "date_created";
        static final String COLUMN_DESCRIPTION = "description";
        static final String COLUMN_USERNAME = "username";

        static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_ID + " INTEGER, " +
                COLUMN_DATE + " TEXT, " +
                COLUMN_DESCRIPTION + " TEXT, " +
                COLUMN_LOCATION_MACHINE_XREF_ID + " INTEGER, " +
                COLUMN_USERNAME + " TEXT" + ")";

        static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

        static final String[] PROJECTION = {
            COLUMN_ID,
            COLUMN_LOCATION_MACHINE_XREF_ID,
            COLUMN_DATE,
            COLUMN_DESCRIPTION,
            COLUMN_USERNAME
        };
    }

    static class LocationContract implements BaseColumns {
        static final String TABLE_NAME = "locations";

        public static final String COLUMN_ID = "id";
        static final String COLUMN_ZONE_ID = "zone_id";
        static final String COLUMN_LOCATION_TYPE_ID = "location_type_id";
        static final String COLUMN_OPERATOR_ID = "operator_id";

        static final String COLUMN_NAME = "name";
        static final String COLUMN_STREET = "street";
        static final String COLUMN_CITY = "city";
        static final String COLUMN_STATE = "state";
        static final String COLUMN_ZIP = "zip";
        static final String COLUMN_PHONE = "phone";
        static final String COLUMN_LAT = "lat";
        static final String COLUMN_LON = "lon";
        static final String COLUMN_WEBSITE = "website";
        static final String COLUMN_MILES_INFO = "miles_info";
        static final String COLUMN_LAST_UPDATED_BY_USERNAME = "last_updated_by_username";
        static final String COLUMN_DATE_LAST_UPDATED = "date_last_updated";
        static final String COLUMN_DESCRIPTION = "description";
        static final String COLUMN_DISTANCE_FROM_YOU = "distance_from_you";
        static final String COLUMN_NUM_MACHINES = "num_machines";

        static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_ID + " INTEGER, " +
                COLUMN_ZONE_ID + " INTEGER, " +
                COLUMN_LOCATION_TYPE_ID + " INTEGER, " +
                COLUMN_OPERATOR_ID + " INTEGER, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_STREET + " TEXT, " +
                COLUMN_CITY + " TEXT, " +
                COLUMN_STATE + " TEXT, " +
                COLUMN_ZIP + " TEXT, " +
                COLUMN_PHONE + " TEXT, " +
                COLUMN_LAT + " TEXT, " +
                COLUMN_LON + " TEXT, " +
                COLUMN_WEBSITE + " TEXT, " +
                COLUMN_MILES_INFO + " TEXT, " +
                COLUMN_LAST_UPDATED_BY_USERNAME + " TEXT, " +
                COLUMN_DESCRIPTION + " TEXT, " +
                COLUMN_DATE_LAST_UPDATED + " DATE, " +
                COLUMN_NUM_MACHINES + " TEXT, " +
                COLUMN_DISTANCE_FROM_YOU + " FLOAT" + ")";

        static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

        static final String[] PROJECTION = {
            COLUMN_ID,
            COLUMN_ZONE_ID,
            COLUMN_LOCATION_TYPE_ID,
            COLUMN_OPERATOR_ID,
            COLUMN_NAME,
            COLUMN_STREET,
            COLUMN_CITY,
            COLUMN_STATE,
            COLUMN_ZIP,
            COLUMN_PHONE,
            COLUMN_LAT,
            COLUMN_LON,
            COLUMN_WEBSITE,
            COLUMN_MILES_INFO,
            COLUMN_LAST_UPDATED_BY_USERNAME,
            COLUMN_DESCRIPTION,
            COLUMN_DATE_LAST_UPDATED,
            COLUMN_NUM_MACHINES,
            COLUMN_DISTANCE_FROM_YOU
        };
    }

    static class LocationTypeContract implements BaseColumns {
        static final String TABLE_NAME = "location_types";

        public static final String COLUMN_ID = "id";

        static final String COLUMN_NAME = "name";

        static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_ID + " INTEGER, " +
                COLUMN_NAME + " TEXT" + ")";

        static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

        static final String[] PROJECTION = {
            COLUMN_ID,
            COLUMN_NAME
        };
    }

    static class LocationMachineXrefContract implements BaseColumns {
        static final String TABLE_NAME = "location_machine_xrefs";

        public static final String COLUMN_ID = "id";
        static final String COLUMN_MACHINE_ID = "machine_id";
        static final String COLUMN_LOCATION_ID = "location_id";

        static final String COLUMN_CONDITION = "condition";
        static final String COLUMN_CONDITION_DATE = "condition_date";
        static final String COLUMN_LAST_UPDATED_BY_USERNAME = "last_updated_by_username";

        static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_ID + " INTEGER, " +
                COLUMN_MACHINE_ID + " INTEGER, " +
                COLUMN_LOCATION_ID + " INTEGER, " +
                COLUMN_CONDITION + " TEXT, " +
                COLUMN_CONDITION_DATE + " DATE, " +
                COLUMN_LAST_UPDATED_BY_USERNAME + " TEXT" + ")";

        static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

        static final String[] PROJECTION = {
            COLUMN_ID,
            COLUMN_MACHINE_ID,
            COLUMN_LOCATION_ID,
            COLUMN_CONDITION,
            COLUMN_CONDITION_DATE,
            COLUMN_LAST_UPDATED_BY_USERNAME
        };
    }

    static class MachineContract implements BaseColumns {
        static final String TABLE_NAME = "machines";

        public static final String COLUMN_ID = "id";
        static final String COLUMN_NAME = "name";
        static final String COLUMN_YEAR = "year";
        static final String COLUMN_MANUFACTURER = "manufacturer";
        static final String COLUMN_GROUP_ID = "group_id";
        static final String COLUMN_NUM_LOCATIONS = "num_locations";
        static final String COLUMN_EXISTS_IN_REGION = "exists_in_region";

        static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_ID + " INTEGER, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_YEAR + " TEXT, " +
                COLUMN_MANUFACTURER + " TEXT, " +
                COLUMN_GROUP_ID + " TEXT, " +
                COLUMN_NUM_LOCATIONS + " INTEGER, " +
                COLUMN_EXISTS_IN_REGION + " BOOLEAN" + ")";

        static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

        static final String[] PROJECTION = {
            COLUMN_ID,
            COLUMN_NAME,
            COLUMN_YEAR,
            COLUMN_MANUFACTURER,
            COLUMN_GROUP_ID,
            COLUMN_NUM_LOCATIONS,
            COLUMN_EXISTS_IN_REGION
        };
    }

    static class MachineScoreContract implements BaseColumns {
        static final String TABLE_NAME = "machine_scores_xrefs";

        public static final String COLUMN_ID = "id";
        static final String COLUMN_LOCATION_MACHINE_XREF_ID = "location_machine_xref_id";

        static final String COLUMN_USERNAME = "username";
        static final String COLUMN_SCORE = "score";
        static final String COLUMN_DATE_CREATED = "date_created";


        static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_ID + " INTEGER, " +
                COLUMN_LOCATION_MACHINE_XREF_ID + " INTEGER, " +
                COLUMN_USERNAME + " TEXT, " +
                COLUMN_DATE_CREATED + " TEXT, " +
                COLUMN_SCORE + " TEXT, " +
                COLUMN_EXISTS_IN_REGION + " TEXT" + ")";

        static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

        static final String[] PROJECTION = {
            COLUMN_ID,
            COLUMN_LOCATION_MACHINE_XREF_ID,
            COLUMN_USERNAME,
            COLUMN_DATE_CREATED,
            COLUMN_SCORE,
            COLUMN_EXISTS_IN_REGION
        };
    }

    static class OperatorContract implements BaseColumns {
        static final String TABLE_NAME = "operators";

        public static final String COLUMN_ID = "id";

        static final String COLUMN_NAME = "name";

        static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_ID + " INTEGER, " +
                COLUMN_NAME + " TEXT" + ")";

        static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

        static final String[] PROJECTION = {
            COLUMN_ID,
            COLUMN_NAME
        };
    }

    static class RegionContract implements BaseColumns {
        static final String TABLE_NAME = "regions";

        public static final String COLUMN_ID = "id";

        static final String COLUMN_NAME = "name";
        static final String COLUMN_FORMAL_NAME = "formal_name";
        static final String COLUMN_MOTD = "motd";
        static final String COLUMN_LAT = "lat";
        static final String COLUMN_LON = "lon";
        static final String COLUMN_DISTANCE_FROM_YOU = "distance_from_you";

        static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_ID + " INTEGER, " +
                COLUMN_FORMAL_NAME + " TEXT, " +
                COLUMN_MOTD + " TEXT, " +
                COLUMN_LAT + " TEXT, " +
                COLUMN_LON + " TEXT, " +
                COLUMN_DISTANCE_FROM_YOU + " TEXT, " +
                COLUMN_NAME + " TEXT" + ")";

        static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

        static final String[] PROJECTION = {
            COLUMN_ID,
            COLUMN_FORMAL_NAME,
            COLUMN_MOTD,
            COLUMN_LAT,
            COLUMN_LON,
            COLUMN_DISTANCE_FROM_YOU,
            COLUMN_NAME
        };
    }

    static class ZoneContract implements BaseColumns {
        static final String TABLE_NAME = "zones";

        public static final String COLUMN_ID = "id";

        static final String COLUMN_NAME = "name";
        static final String COLUMN_IS_PRIMARY = "is_primary";

        static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_ID + " INTEGER, " +
                COLUMN_IS_PRIMARY + " BOOLEAN, " +
                COLUMN_NAME + " TEXT" + ")";

        static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

        static final String[] PROJECTION = {
            COLUMN_ID,
            COLUMN_IS_PRIMARY,
            COLUMN_NAME
        };
    }
}
