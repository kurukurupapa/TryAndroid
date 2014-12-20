package com.example.kurukurupapa.db01.entity;

import android.content.Context;
import android.provider.BaseColumns;
import android.text.format.DateFormat;

import java.util.Date;

/**
 * エンティティ1件分を保持するクラスです。
 */
public class Entity implements BaseColumns {
    public static final String TABLE = "sample";
    public static final String NAME = "name";
    public static final String TEXT = "text";
    public static final String NUMBER = "number";
    public static final String TIMESTAMP = "timestamp";

    public static final long NO_ID = -1;

    private long id;
    private String name;
    private String text;
    private int number;
    private long timestamp;

    public Entity() {
        id = NO_ID;
    }

    public long getId() {
        return id;
    }
    public String getIdString() {
        return id == NO_ID ? "New" : String.valueOf(id);
    }
    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }

    public int getNumber() {
        return number;
    }
    public String getNumberString() {
        return String.valueOf(number);
    }
    public void setNumber(int number) {
        this.number = number;
    }
    public void setNumber(String numberString) {
        this.number = Integer.parseInt(numberString);
    }

    public long getTimestamp() {
        return timestamp;
    }
    public String getTimestampString(Context context) {
        if (timestamp > 0) {
            Date date = new Date(timestamp);
            return DateFormat.getDateFormat(context).format(date) + " " + DateFormat.getTimeFormat(context).format(date);
        } else {
            return "(なし)";
        }
    }
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isNew() {
        return id == NO_ID;
    }
}
