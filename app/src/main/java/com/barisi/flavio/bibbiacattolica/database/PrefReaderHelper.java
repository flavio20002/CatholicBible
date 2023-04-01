package com.barisi.flavio.bibbiacattolica.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class PrefReaderHelper extends SQLiteOpenHelper {

    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + VersettiPreferitiEntry.TABLE_NAME + " (" +
                    VersettiPreferitiEntry.COLUMN_NAME_ID_CAPITOLO + TEXT_TYPE + COMMA_SEP +
                    VersettiPreferitiEntry.COLUMN_NAME_VERSETTO + TEXT_TYPE + COMMA_SEP +
                    VersettiPreferitiEntry.COLUMN_NAME_NOTA + TEXT_TYPE + " )";

    private static final String SQL_CREATE_INDEX =
            "CREATE INDEX versetti_preferiti_id on " + VersettiPreferitiEntry.TABLE_NAME + "(" + VersettiPreferitiEntry.COLUMN_NAME_ID_CAPITOLO + ")";

    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "Preferenze.db";

    PrefReaderHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
        db.execSQL(SQL_CREATE_INDEX);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion == 1) {
            db.execSQL("ALTER TABLE " + VersettiPreferitiEntry.TABLE_NAME + " ADD COLUMN " + VersettiPreferitiEntry.COLUMN_NAME_NOTA + " TEXT");
        }
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}