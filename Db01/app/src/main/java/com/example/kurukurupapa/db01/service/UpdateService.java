package com.example.kurukurupapa.db01.service;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.kurukurupapa.db01.entity.Entity;

/**
 * エンティティ1件を更新/削除するサービスクラスです。
 */
public class UpdateService {
    private static final String TAG = UpdateService.class.getSimpleName();

    public Entity insertOrUpdate(SQLiteDatabase db, Entity entity) {
        if (entity.isNew()) {
            return insert(db, entity);
        } else {
            return update(db, entity);
        }
    }

    public Entity insert(SQLiteDatabase db, Entity entity) {
        entity.setTimestamp(System.currentTimeMillis());
        ContentValues values = createContentValues(entity);
        long id = db.insertOrThrow(Entity.TABLE, null, values);
        Log.d(TAG, "データ挿入結果 id=" + id);
        entity.setId(id);
        return entity;
    }

    public Entity update(SQLiteDatabase db, Entity entity) {
        entity.setTimestamp(System.currentTimeMillis());
        ContentValues values = createContentValues(entity);
        int count = db.update(Entity.TABLE, values, Entity._ID + "=" + entity.getId(), null);
        Log.d(TAG, "データ更新結果 count=" + count);
        if (count != 1) {
            throw new RuntimeException("データ更新に失敗しました。id=" + entity.getId());
        }
        return entity;
    }

    public void delete(SQLiteDatabase db, Entity entity) {
        int count = db.delete(Entity.TABLE, Entity._ID + "=" + entity.getId(), null);
        Log.d(TAG, "データ削除結果 count=" + count);
        if (count != 1) {
            throw new RuntimeException("データ削除に失敗しました。id=" + entity.getId());
        }
    }

    private ContentValues createContentValues(Entity entity) {
        ContentValues values = new ContentValues();
        values.put(Entity.NAME, entity.getName());
        values.put(Entity.TEXT, entity.getText());
        values.put(Entity.NUMBER, entity.getNumber());
        values.put(Entity.TIMESTAMP, entity.getTimestamp());
        return values;
    }
}
