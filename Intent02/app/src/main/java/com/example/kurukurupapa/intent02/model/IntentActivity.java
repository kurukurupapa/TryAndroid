package com.example.kurukurupapa.intent02.model;

import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.provider.BaseColumns;

import org.apache.commons.lang3.StringUtils;

/**
 * 受信したインテントを、別アプリのアクティビティへ受け渡すためのデータを表すクラスです。
 */
public class IntentActivity implements BaseColumns {
    public static final String TABLE_NAME = "intent_activity";
    public static final String COLUMN_SRC_PACKAGE = "src_package";
    public static final String COLUMN_ACTION = "action";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_DEST_PACKAGE = "dest_package";
    public static final String COLUMN_DEST_ACTIVITY = "dest_activity";
    public static final String COLUMN_TIMESTAMP = "timestamp";
    public static final String COLUMN_TIMESTAMP1 = "timestamp1";
    public static final String COLUMN_TIMESTAMP2 = "timestamp2";
    public static final String COLUMN_TIMESTAMP3 = "timestamp3";
    public static final int NO_ID = -1;

    /** ID */
    private long mId;

    /** インテント呼び出し元アプリ */
    private String mSrcPackage;

    /** インテントのアクション */
    private String mAction;
    /** インテントのタイプ */
    private String mType;

    /** 受け渡し先アプリパッケージ名 */
    private String mDestPackage;
    /** 受け渡し先アプリアクティビティ名 */
    private String mDestActivity;

    /** 更新日時（平均） */
    private long mTimestamp;
    /** 更新日時（前回） */
    private long mTimestamp1;
    /** 更新日時（2回前） */
    private long mTimestamp2;
    /** 更新日時（3回前） */
    private long mTimestamp3;

    /** ResolveInfoオブジェクト */
    private ResolveInfo mResolveInfo;

    public IntentActivity(String srcPackage, Intent intent, ResolveInfo resolveInfo) {
        this(NO_ID, srcPackage, intent.getAction(), intent.getType(),
                resolveInfo.activityInfo.packageName, resolveInfo.activityInfo.name,
                0, 0, 0, 0, resolveInfo);
    }

    public IntentActivity(long id,
                          String srcPackage, String action, String type,
                          String destPackage, String destActivity,
                          long timestamp, long timestamp1, long timestamp2, long timestamp3, ResolveInfo resolveInfo) {
        mId = id;
        mSrcPackage = srcPackage;
        mAction = action;
        mType = type;
        mDestPackage = destPackage;
        mDestActivity = destActivity;
        mTimestamp = timestamp;
        mTimestamp1 = timestamp1;
        mTimestamp2 = timestamp2;
        mTimestamp3 = timestamp3;
        mResolveInfo = resolveInfo;
    }

    public long getId() {
        return mId;
    }
    public void setId(long id) {
        mId = id;
    }

    public String getSrcPackage() {
        return mSrcPackage;
    }

    public void setSrcPackage(String srcPackage) {
        this.mSrcPackage = srcPackage;
    }

    public String getAction() {
        return mAction;
    }

    public void setAction(String action) {
        this.mAction = action;
    }

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        this.mType = type;
    }

    public String getDestPackage() {
        return mDestPackage;
    }

    public void setDestPackage(String destPackage) {
        this.mDestPackage = destPackage;
    }

    public String getDestActivity() {
        return mDestActivity;
    }

    public void setDestActivity(String destActivity) {
        this.mDestActivity = destActivity;
    }

    public long getTimestamp() {
        return mTimestamp;
    }
    public long getTimestamp1() {
        return mTimestamp1;
    }
    public long getTimestamp2() {
        return mTimestamp2;
    }
    public long getTimestamp3() {
        return mTimestamp3;
    }

    public void modifyTimestamp() {
        mTimestamp3 = mTimestamp2;
        mTimestamp2 = mTimestamp1;
        mTimestamp1 = System.currentTimeMillis();

        // 平均を求める
        // ※桁あふれしない？
        // 　long型の最大値： 9,223,372,036,854,775,807（19桁）
        // 　日時（ミリ秒）：2000*365*24*60*60*1000=63,072,000,000,000（15桁）
        mTimestamp = (mTimestamp1 + mTimestamp2 + mTimestamp3) / 3;
    }

    public ResolveInfo getResolveInfo() {
        return mResolveInfo;
    }

    public void setResolveInfo(ResolveInfo resolveInfo) {
        mResolveInfo = resolveInfo;
    }

    public boolean isNew() {
        return mId == NO_ID;
    }

    public ContentValues createContentValues() {
        ContentValues values = new ContentValues();
        values.put(COLUMN_SRC_PACKAGE, mSrcPackage);
        values.put(COLUMN_ACTION, mAction);
        values.put(COLUMN_TYPE, StringUtils.defaultString(mType, "null"));
        values.put(COLUMN_DEST_PACKAGE, mDestPackage);
        values.put(COLUMN_DEST_ACTIVITY, mDestActivity);
        values.put(COLUMN_TIMESTAMP, mTimestamp);
        values.put(COLUMN_TIMESTAMP1, mTimestamp1);
        values.put(COLUMN_TIMESTAMP2, mTimestamp2);
        values.put(COLUMN_TIMESTAMP3, mTimestamp3);
        return values;
    }
}
