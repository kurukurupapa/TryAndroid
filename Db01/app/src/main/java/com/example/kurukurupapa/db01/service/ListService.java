package com.example.kurukurupapa.db01.service;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.kurukurupapa.db01.entity.Entity;

import java.util.ArrayList;

/**
 * エンティティ一覧を取得するサービスクラスです。
 */
public class ListService {
    public static final String TAG = ListService.class.getSimpleName();

    private ArrayList<Entity> mEntityList;
    private ArrayList<String> mStringList;

    public void run(SQLiteDatabase db) {
        mEntityList = new ArrayList<Entity>();
        Cursor cursor = db.query(Entity.TABLE, null, null, null, null, null, Entity._ID);
        Log.d(TAG, "データ検索結果 count=" + cursor.getCount());
        try {
            boolean loop = cursor.moveToFirst();
            while (loop) {
                Entity entity = new Entity();
                entity.setId(cursor.getLong(cursor.getColumnIndex(Entity._ID)));
                entity.setName(cursor.getString(cursor.getColumnIndex(Entity.NAME)));
                entity.setText(cursor.getString(cursor.getColumnIndex(Entity.TEXT)));
                entity.setNumber(cursor.getInt(cursor.getColumnIndex(Entity.NUMBER)));
                entity.setTimestamp(cursor.getLong(cursor.getColumnIndex(Entity.TIMESTAMP)));
                mEntityList.add(entity);
                loop = cursor.moveToNext();
            }
        }
        finally {
            cursor.close();
        }

        // 画面表示用のリストを作成
        mStringList = new ArrayList<String>();
        for (Entity entity : mEntityList) {
            mStringList.add(entity.getIdString() + ", " + entity.getName());
        }
    }

    public ArrayList<String> getStringList() {
        return mStringList;
    }

    public long getId(int index) {
        long id;
        if (0 <= index && index < mEntityList.size()) {
            id = mEntityList.get(index).getId();
        } else {
            id = Entity.NO_ID;
        }
        return id;
    }
}
