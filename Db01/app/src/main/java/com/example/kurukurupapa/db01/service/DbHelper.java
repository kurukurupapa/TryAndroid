package com.example.kurukurupapa.db01.service;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.kurukurupapa.db01.entity.Entity;

/**
 * データベース管理のヘルパークラスです。
 */
public class DbHelper extends SQLiteOpenHelper {
    private static final String DB_FILE_NAME = "db01";
    private static final int DB_VERSION = 1;

    public DbHelper(Context context) {
        super(context, DB_FILE_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table " + Entity.TABLE + " ("
                        + Entity._ID + " integer primary key autoincrement, "
                        + Entity.NAME + " text not null, "
                        + Entity.TEXT + " text, "
                        + Entity.NUMBER + " integer, "
                        + Entity.TIMESTAMP + " integer not null"
                + ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // テーブルレイアウトが変わったら記述します。
    }
}
