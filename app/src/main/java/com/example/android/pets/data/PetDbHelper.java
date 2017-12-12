package com.example.android.pets.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by pcvincenzo on 10/12/17.
 */

public class PetDbHelper extends SQLiteOpenHelper {



    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "shelter.db";

    //Class constructor
    public PetDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //SQLite TABLE creation
    private static final String SQL_CREATE_PETS_TABLE =
            "CREATE TABLE " + PetContract.PetEntry.TABLE_NAME + " (" +
                    PetContract.PetEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    PetContract.PetEntry.COLUMN_PET_NAME + " TEXT NOT NULL, " +
                    PetContract.PetEntry.COLUMN_PET_BREED + " TEXT, " +
                    PetContract.PetEntry.COLUMN_PET_GENDER + " INTEGER NOT NULL, " +
                    PetContract.PetEntry.COLUMN_PET_WEIGHT + " INTEGER NOT NULL DEFAULT 0)";

    //SQLite TABLE deleting
    private static final String SQL_DELETE_PETS_TABLE =
            "DROP TABLE IF EXISTS " + PetContract.PetEntry.TABLE_NAME;

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_PETS_TABLE);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_PETS_TABLE);
        onCreate(db);
    }

}
