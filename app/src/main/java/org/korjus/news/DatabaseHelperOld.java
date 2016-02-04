package org.korjus.news;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

// Uses Cupboard to handle SQLite database
// Documentation: https://bitbucket.org/littlerobots/cupboard/wiki/Home
class DatabaseHelperOld extends SQLiteOpenHelper {
    private static final String TAG = "u8i9 DatabaseOld";
    public static final String DATABASE_NAME = "oldNews.db";
    private static final int DATABASE_VERSION = 1;

    static {
        // Register our models
        cupboard().register(OldNews.class);
    }

    public DatabaseHelperOld(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // This will ensure that all tables are created
        cupboard().withDatabase(db).createTables();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This will upgrade tables, adding columns and new tables.
        // Note that existing columns will not be converted
        cupboard().withDatabase(db).upgradeTables();
    }
}
