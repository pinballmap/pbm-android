package com.pbm;

import android.provider.BaseColumns;

import static com.pbm.PBMContract.MachineContract.COLUMN_EXISTS_IN_REGION;

public final class PBMContract {
    private PBMContract() {}

    public static class ConditionContract implements BaseColumns {
        public static final String TABLE_NAME = "location_machine_conditions";

        public static final String COLUMN_ID = "id";
        public static final String COLUMN_LOCATION_MACHINE_XREF_ID = "location_machine_xref_id";

        public static final String COLUMN_DATE = "date_created";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_USERNAME = "username";

        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_ID + " INTEGER, " +
                COLUMN_DATE + " TEXT, " +
                COLUMN_DESCRIPTION + " TEXT, " +
                COLUMN_LOCATION_MACHINE_XREF_ID + " INTEGER, " +
                COLUMN_USERNAME + " TEXT" + ")";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

        public static final String[] PROJECTION = {
                COLUMN_ID,
                COLUMN_LOCATION_MACHINE_XREF_ID,
                COLUMN_DATE,
                COLUMN_DESCRIPTION,
                COLUMN_USERNAME
        };
    }

    public static class LocationContract implements BaseColumns {
        public static final String TABLE_NAME = "locations";

        public static final String COLUMN_ID = "id";
        public static final String COLUMN_ZONE_ID = "zone_id";
        public static final String COLUMN_LOCATION_TYPE_ID = "location_type_id";
        public static final String COLUMN_OPERATOR_ID = "operator_id";

        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_STREET = "street";
        public static final String COLUMN_CITY = "city";
        public static final String COLUMN_STATE = "state";
        public static final String COLUMN_ZIP = "zip";
        public static final String COLUMN_PHONE = "phone";
        public static final String COLUMN_LAT = "lat";
        public static final String COLUMN_LON = "lon";
        public static final String COLUMN_WEBSITE = "website";
        public static final String COLUMN_MILES_INFO = "miles_info";
        public static final String COLUMN_LAST_UPDATED_BY_USERNAME = "last_updated_by_username";
        public static final String COLUMN_DATE_LAST_UPDATED = "date_last_updated";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_DISTANCE_FROM_YOU = "distance_from_you";

        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
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
                COLUMN_DISTANCE_FROM_YOU + " FLOAT" + ")";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

        public static final String[] PROJECTION = {
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
                COLUMN_DISTANCE_FROM_YOU
        };
    }

    public static class LocationTypeContract implements BaseColumns {
        public static final String TABLE_NAME = "location_types";

        public static final String COLUMN_ID = "id";

        public static final String COLUMN_NAME = "name";

        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_ID + " INTEGER, " +
                COLUMN_NAME + " TEXT" + ")";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

        public static final String[] PROJECTION = {
                COLUMN_ID,
                COLUMN_NAME
        };
    }

    public static class LocationMachineXrefContract implements BaseColumns {
        public static final String TABLE_NAME = "location_machine_xrefs";

        public static final String COLUMN_ID = "id";
        public static final String COLUMN_MACHINE_ID = "machine_id";
        public static final String COLUMN_LOCATION_ID = "location_id";

        public static final String COLUMN_CONDITION = "condition";
        public static final String COLUMN_CONDITION_DATE = "condition_date";
        public static final String COLUMN_LAST_UPDATED_BY_USERNAME = "last_updated_by_username";

        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_ID + " INTEGER, " +
                COLUMN_MACHINE_ID + " INTEGER, " +
                COLUMN_LOCATION_ID + " INTEGER, " +
                COLUMN_CONDITION + " TEXT, " +
                COLUMN_CONDITION_DATE + " DATE, " +
                COLUMN_LAST_UPDATED_BY_USERNAME + " TEXT" + ")";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

        public static final String[] PROJECTION = {
                COLUMN_ID,
                COLUMN_MACHINE_ID,
                COLUMN_LOCATION_ID,
                COLUMN_CONDITION,
                COLUMN_CONDITION_DATE,
                COLUMN_LAST_UPDATED_BY_USERNAME
        };
    }

    public static class MachineContract implements BaseColumns {
        public static final String TABLE_NAME = "machines";

        public static final String COLUMN_ID = "id";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_YEAR = "year";
        public static final String COLUMN_MANUFACTURER = "manufacturer";
        public static final String COLUMN_GROUP_ID = "group_id";
        public static final String COLUMN_NUM_LOCATIONS = "num_locations";
        public static final String COLUMN_EXISTS_IN_REGION = "exists_in_region";

        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_ID + " INTEGER, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_YEAR + " TEXT, " +
                COLUMN_MANUFACTURER + " TEXT, " +
                COLUMN_GROUP_ID + " TEXT, " +
                COLUMN_NUM_LOCATIONS + " INTEGER, " +
                COLUMN_EXISTS_IN_REGION + " BOOLEAN" + ")";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

        public static final String[] PROJECTION = {
                COLUMN_ID,
                COLUMN_NAME,
                COLUMN_YEAR,
                COLUMN_MANUFACTURER,
                COLUMN_GROUP_ID,
                COLUMN_NUM_LOCATIONS,
                COLUMN_EXISTS_IN_REGION
        };
    }

    public static class MachineScoreContract implements BaseColumns {
        public static final String TABLE_NAME = "machine_scores_xrefs";

        public static final String COLUMN_ID = "id";
        public static final String COLUMN_LOCATION_MACHINE_XREF_ID = "location_machine_xref_id";

        public static final String COLUMN_USERNAME = "username";
        public static final String COLUMN_SCORE = "score";
        public static final String COLUMN_DATE_CREATED = "date_created";


        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_ID + " INTEGER, " +
                COLUMN_LOCATION_MACHINE_XREF_ID + " INTEGER, " +
                COLUMN_USERNAME + " TEXT, " +
                COLUMN_DATE_CREATED + " TEXT, " +
                COLUMN_EXISTS_IN_REGION + " TEXT" + ")";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

        public static final String[] PROJECTION = {
                COLUMN_ID,
                COLUMN_LOCATION_MACHINE_XREF_ID,
                COLUMN_USERNAME,
                COLUMN_DATE_CREATED,
                COLUMN_EXISTS_IN_REGION
        };
    }

    public static class OperatorContract implements BaseColumns {
        public static final String TABLE_NAME = "operators";

        public static final String COLUMN_ID = "id";

        public static final String COLUMN_NAME = "name";

        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_ID + " INTEGER, " +
                COLUMN_NAME + " TEXT" + ")";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

        public static final String[] PROJECTION = {
                COLUMN_ID,
                COLUMN_NAME
        };
    }

    public static class RegionContract implements BaseColumns {
        public static final String TABLE_NAME = "regions";

        public static final String COLUMN_ID = "id";

        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_FORMAL_NAME = "formal_name";
        public static final String COLUMN_MOTD = "motd";
        public static final String COLUMN_LAT = "lat";
        public static final String COLUMN_LON = "lon";
        public static final String COLUMN_DISTANCE_FROM_YOU = "distance_from_you";

        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_ID + " INTEGER, " +
                COLUMN_FORMAL_NAME + " TEXT, " +
                COLUMN_MOTD + " TEXT, " +
                COLUMN_LAT + " TEXT, " +
                COLUMN_LON + " TEXT, " +
                COLUMN_DISTANCE_FROM_YOU + " TEXT, " +
                COLUMN_NAME + " TEXT" + ")";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

        public static final String[] PROJECTION = {
                COLUMN_ID,
                COLUMN_FORMAL_NAME,
                COLUMN_MOTD,
                COLUMN_LAT,
                COLUMN_LON,
                COLUMN_DISTANCE_FROM_YOU,
                COLUMN_NAME
        };
    }

    public static class ZoneContract implements BaseColumns {
        public static final String TABLE_NAME = "zones";

        public static final String COLUMN_ID = "id";

        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_IS_PRIMARY = "is_primary";

        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_ID + " INTEGER, " +
                COLUMN_IS_PRIMARY + " BOOLEAN, " +
                COLUMN_NAME + " TEXT" + ")";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

        public static final String[] PROJECTION = {
                COLUMN_ID,
                COLUMN_IS_PRIMARY,
                COLUMN_NAME
        };
    }
}
