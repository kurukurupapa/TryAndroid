package com.example.kurukurupapa.db01.service;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.kurukurupapa.db01.entity.Entity;

/**
 * エンティティ1件を取得するサービスクラスです。
 */
public class QueryService {
    public static final String TAG = QueryService.class.getSimpleName();

    public Entity queryOrCreate(SQLiteDatabase db, long id) {
        Entity entity = new Entity();
        Cursor cursor = db.query(Entity.TABLE, null, Entity._ID + "=" + id, null, null, null, null);
        Log.d(TAG, "データ検索結果 count=" + cursor.getCount());
        try {
            if (cursor.moveToFirst()) {
                entity.setId(cursor.getLong(cursor.getColumnIndex(Entity._ID)));
                entity.setName(cursor.getString(cursor.getColumnIndex(Entity.NAME)));
                entity.setText(cursor.getString(cursor.getColumnIndex(Entity.TEXT)));
                entity.setNumber(cursor.getInt(cursor.getColumnIndex(Entity.NUMBER)));
                entity.setTimestamp(cursor.getLong(cursor.getColumnIndex(Entity.TIMESTAMP)));
            } else {
                entity.setName("エンティティ1");
                entity.setText("テキスト1行目\n2行目\n3行目");
                entity.setNumber(123);
            }
        }
        finally {
            cursor.close();
        }
        return entity;
    }
}
