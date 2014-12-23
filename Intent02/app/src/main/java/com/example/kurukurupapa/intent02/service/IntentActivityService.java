package com.example.kurukurupapa.intent02.service;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.kurukurupapa.intent02.helper.db.DbHelper;
import com.example.kurukurupapa.intent02.model.IntentActivity;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.ArrayList;
import java.util.List;

/**
 * 受信したインテントを、受け渡し可能なアクティビティを検索するサービスクラスです。
 */
public class IntentActivityService {
    private static final String TAG = IntentActivityService.class.getSimpleName();

    private Context mContext;
    private PackageManager mPackageManager;
    private DbHelper mDbHelper;

    public IntentActivityService(Context context) {
        mContext = context;
        mPackageManager = mContext.getPackageManager();
        mDbHelper = new DbHelper(context);
    }

    /**
     * インテント受信可能なアクティビティ一覧を取得します。
     * @param srcPackage インテント呼び出し元アプリのパッケージ
     * @param srcIntent インテント
     * @return リスト
     */
    public List<IntentActivity> find(String srcPackage, Intent srcIntent) {
        List<IntentActivity> resultIntentActivityList = new ArrayList<IntentActivity>();

        // PackageMangerからアクティビティ一覧を取得します。
        List<ResolveInfo> tmpResolveInfoList = getResolveInfoList(srcIntent);

        // DBからアクティビティ使用履歴を取得します。
        List<IntentActivity> intentActivityList = query(srcPackage, srcIntent);

        // 優先順位を考慮しながら結果リストを作成します。
        // 優先１：DB登録されている（過去に使用された）アクティビティを結果リストに追加します。
        for (IntentActivity intentActivity : intentActivityList) {
            String packageName = intentActivity.getDestPackage();
            String activity = intentActivity.getDestActivity();

            // DB登録されていたアクティビティが、PackageManagerのアクティビティ一覧に存在することを確認します。
            ResolveInfo targetResolveInfo = null;
            for (ResolveInfo resolveInfo : tmpResolveInfoList) {
                if (resolveInfo.activityInfo.packageName.equals(packageName) &&
                        resolveInfo.activityInfo.name.equals(activity)) {
                    targetResolveInfo = resolveInfo;
                    break;
                }
            }
            if (targetResolveInfo == null) {
                // DBのアクティビティ一覧に存在し、PackageManagerの一覧に存在しなかったアクティビティは、アンインストールされたと考えます。
                // DBの該当データは不要なので、削除します。
                delete(intentActivity);
            } else {
                // 結果リストへ登録します。
                intentActivity.setResolveInfo(targetResolveInfo);
                resultIntentActivityList.add(intentActivity);
                tmpResolveInfoList.remove(targetResolveInfo);
            }
        }
        // 優先２：使われたことのないアクティビティを結果リストに追加します。
        // TODO ソートを検討する。
        for (ResolveInfo resolveInfo : tmpResolveInfoList) {
            if (resolveInfo.activityInfo.packageName.equals(mContext.getPackageName())) {
                // 当アプリのアクティビティは無視します。
                continue;
            }
            IntentActivity intentActivity = new IntentActivity(srcPackage, srcIntent, resolveInfo);
            resultIntentActivityList.add(intentActivity);
        }

        return resultIntentActivityList;
    }

    /**
     * インテント受信可能なアクティビティ一覧を取得します。
     * @param srcIntent
     * @return アクティビティ名のリスト
     */
    public ArrayList<String> getRecvActivityStringList(Intent srcIntent) {
        ArrayList<String> activityList = new ArrayList<String>();
        for (ResolveInfo info : getResolveInfoList(srcIntent)) {
            String str = info.activityInfo.packageName + "/" + info.activityInfo.name;
            activityList.add(str);
        }
        return activityList;
    }

    /**
     * インテント受信可能なアクティビティ一覧を取得します。
     * @return ResolveInfoオブジェクトのリスト
     */
    private List<ResolveInfo> getResolveInfoList(Intent srcIntent) {
        Intent intent = new Intent(srcIntent);
        intent.setComponent(null);
        List<ResolveInfo> list = mPackageManager.queryIntentActivities(intent, 0);
        Log.d(TAG, "queryIntentActivities結果 件数=" + list.size());
        return list;
    }

    public void updateOrInsert(IntentActivity intentActivity) {
        // 更新日時を更新します。
        intentActivity.modifyTimestamp();

        // DBに保存します。
        if (intentActivity.isNew()) {
            insert(intentActivity);
        } else {
            update(intentActivity);
        }
    }

    private List<IntentActivity> query(String srcPackage, Intent srcIntent) {
        List<IntentActivity> list = new ArrayList<IntentActivity>();
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        try {
            String selection =
                    IntentActivity.COLUMN_SRC_PACKAGE + "='" + srcPackage + "' and " +
                    IntentActivity.COLUMN_ACTION + "='" + srcIntent.getAction() + "' and " +
                    IntentActivity.COLUMN_TYPE + "='" + srcIntent.getType() + "'";
            Log.d(TAG, "IntentActivityテーブル検索 selection=[" + selection + "]");
            Cursor cursor = db.query(IntentActivity.TABLE_NAME, null, selection,
                    null, null, null, IntentActivity.COLUMN_TIMESTAMP + " desc, _id");
            Log.d(TAG, "IntentActivityテーブル検索結果 件数=" + cursor.getCount());
            boolean loop = cursor.moveToFirst();
            int idIndex = cursor.getColumnIndex(IntentActivity._ID);
            int srcPackageIndex = cursor.getColumnIndex(IntentActivity.COLUMN_SRC_PACKAGE);
            int actionIndex = cursor.getColumnIndex(IntentActivity.COLUMN_ACTION);
            int typeIndex = cursor.getColumnIndex(IntentActivity.COLUMN_TYPE);
            int destPackageIndex = cursor.getColumnIndex(IntentActivity.COLUMN_DEST_PACKAGE);
            int destActivityIndex = cursor.getColumnIndex(IntentActivity.COLUMN_DEST_ACTIVITY);
            int timestampIndex = cursor.getColumnIndex(IntentActivity.COLUMN_TIMESTAMP);
            int timestamp1Index = cursor.getColumnIndex(IntentActivity.COLUMN_TIMESTAMP1);
            int timestamp2Index = cursor.getColumnIndex(IntentActivity.COLUMN_TIMESTAMP2);
            int timestamp3Index = cursor.getColumnIndex(IntentActivity.COLUMN_TIMESTAMP3);
            while (loop) {
                IntentActivity intentActivity = new IntentActivity(
                        cursor.getLong(idIndex),
                        cursor.getString(srcPackageIndex),
                        cursor.getString(actionIndex),
                        cursor.getString(typeIndex),
                        cursor.getString(destPackageIndex),
                        cursor.getString(destActivityIndex),
                        cursor.getLong(timestampIndex),
                        cursor.getLong(timestamp1Index),
                        cursor.getLong(timestamp2Index),
                        cursor.getLong(timestamp3Index),
                        null
                );
                list.add(intentActivity);
                loop = cursor.moveToNext();
            }
        }
        finally {
            db.close();
        }
        return list;
    }

    private void insert(IntentActivity intentActivity) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        try {
            ContentValues values = intentActivity.createContentValues();
            Log.d(TAG, "IntentActivityテーブル登録 values=[" + ToStringBuilder.reflectionToString(values, ToStringStyle.SHORT_PREFIX_STYLE) + "]");
            long id = db.insert(IntentActivity.TABLE_NAME, null, values);
            Log.d(TAG, "IntentActivityテーブル登録結果 id=" + id);
            if (id < 0) {
                throw new RuntimeException("IntentActivityレコード登録に失敗しました。");
            }
            intentActivity.setId(id);
        }
        finally {
            db.close();
        }
    }

    private void update(IntentActivity intentActivity) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        try {
            ContentValues values = intentActivity.createContentValues();
            Log.d(TAG, "IntentActivityテーブル更新 values=[" + ToStringBuilder.reflectionToString(values, ToStringStyle.SHORT_PREFIX_STYLE) + "]");
            int count = db.update(IntentActivity.TABLE_NAME, values, IntentActivity._ID + "=" + intentActivity.getId(), null);
            Log.d(TAG, "IntentActivityテーブル更新結果 count=" + count);
            if (count != 1) {
                throw new RuntimeException("IntentActivityレコード更新に失敗しました。count=" + count);
            }
        }
        finally {
            db.close();
        }
    }

    private void delete(IntentActivity intentActivity) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        try {
            Log.d(TAG, "IntentActivityテーブル削除 intentActivity=[" + ToStringBuilder.reflectionToString(intentActivity, ToStringStyle.SHORT_PREFIX_STYLE) + "]");
            int count = db.delete(IntentActivity.TABLE_NAME, IntentActivity._ID + "=" + intentActivity.getId(), null);
            Log.d(TAG, "IntentActivityテーブル削除結果 count=" + count);
            if (count != 1) {
                throw new RuntimeException("IntentActivityレコード削除に失敗しました。count=" + count);
            }
        }
        finally {
            db.close();
        }
    }
}
