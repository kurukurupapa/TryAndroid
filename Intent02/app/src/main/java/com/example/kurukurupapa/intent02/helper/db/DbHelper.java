package com.example.kurukurupapa.intent02.helper.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.kurukurupapa.intent02.model.IntentActivity;

/**
 * データベース操作のヘルパークラス
 */
public class DbHelper extends SQLiteOpenHelper {
    private static final String DB_FILE_NAME = "db";
    private static final int DB_VERSION = 1;

    public DbHelper(Context context) {
        super(context, DB_FILE_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + IntentActivity.TABLE_NAME + " (" +
            IntentActivity._ID + " integer primary key autoincrement, " +
            IntentActivity.COLUMN_SRC_PACKAGE + " text, " +
            IntentActivity.COLUMN_ACTION + " text, " +
            IntentActivity.COLUMN_TYPE + " text, " +
            IntentActivity.COLUMN_DEST_PACKAGE + " text, " +
            IntentActivity.COLUMN_DEST_ACTIVITY + " text, " +
            IntentActivity.COLUMN_TIMESTAMP + " integer not null, " +
            IntentActivity.COLUMN_TIMESTAMP1 + " integer, " +
            IntentActivity.COLUMN_TIMESTAMP2 + " integer, " +
            IntentActivity.COLUMN_TIMESTAMP3 + " integer" +
            ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
