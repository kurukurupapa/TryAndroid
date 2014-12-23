package com.example.kurukurupapa.intent02.service;

import android.app.ActivityManager;
import android.content.ClipData;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.util.Log;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.ArrayList;
import java.util.List;

/**
 * インテント受信に関するサービスクラスです。
 */
public class RecvService {
    private static final String TAG = RecvService.class.getSimpleName();

    private Context mContext;
    private Intent mIntent;
    private boolean intentChangedFlag;
    private PackageManager mPackageManager;
    private ApplicationInfo mSrcAppInfo;

    public RecvService(Context context) {
        mContext = context;
    }

    public Intent getIntent() {
        return mIntent;
    }

    public void setIntent(Intent intent) {
        // インテントを保持
        mIntent = intent;
        intentChangedFlag = true;

        // インテント呼び出し元アプリを取得
        // 「android.permission.GET_TASKS」が必要
        ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RecentTaskInfo> recentTaskInfoList = activityManager.getRecentTasks(2, ActivityManager.RECENT_WITH_EXCLUDED);
//        Log.d(TAG, "recentTaskInfoList.size()=" + recentTaskInfoList.size());
//        for (ActivityManager.RecentTaskInfo recentTaskInfo : recentTaskInfoList) {
//            // 1件目：自アクティビティ
//            // 2件目：呼び出し元アクティビティ
//            String className = recentTaskInfo.baseIntent.getComponent().getClassName().toString();
//            Log.d(TAG, "className=" + className);
//        }
        if (recentTaskInfoList.size() >= 2) {
            ComponentName componentName = recentTaskInfoList.get(1).baseIntent.getComponent();
            String packageName = componentName.getPackageName();
            mPackageManager = mContext.getPackageManager();
            try {
                mSrcAppInfo = mPackageManager.getApplicationInfo(packageName, 0);
            } catch (PackageManager.NameNotFoundException e) {
                Log.w(TAG, "インテント呼び出し元のApplicationInfoの取得に失敗しました。packageName=" + packageName, e);
                mSrcAppInfo = null;
            }
        } else {
            Log.w(TAG, "インテント呼び出し元のApplicationInfoの取得に失敗しました。recentTaskInfoList.size()=" + recentTaskInfoList.size());
            mSrcAppInfo = null;
        }
    }

    public boolean isIntentChanged() {
        return intentChangedFlag;
    }

    public void setIntentNoChanged() {
        intentChangedFlag = false;
    }

    public String getShortIntentStr() {
        ArrayList<String> lines = new ArrayList<String>();
        if (mIntent.getClipData() != null) {
            lines.add(getClipDataStr(mIntent));
        }
        if (mIntent.getData() != null) {
            lines.add(mIntent.getDataString());
        }
        return StringUtils.join(lines, "\n");
    }

    public String getLongIntentStr() {
        return ""
                + ToStringBuilder.reflectionToString(mIntent, ToStringStyle.MULTI_LINE_STYLE) + "\n"
                ;
    }

    private String getClipDataStr(Intent intent) {
        // APIレベル16以上
        ClipData clipData = intent.getClipData();
        String clipDataStr = ToStringBuilder.reflectionToString(clipData, ToStringStyle.SIMPLE_STYLE);
        Log.d(TAG, "ClipData=" + clipDataStr);
        if (clipData != null) {
            Log.d(TAG, "ClipData Description=" + ToStringBuilder.reflectionToString(clipData.getDescription()));
            for (int i = 0; i < clipData.getItemCount(); i++) {
                ClipData.Item item = clipData.getItemAt(i);
                Log.d(TAG, "ClipData Item " + i + "=" + ToStringBuilder.reflectionToString(item));
            }
        }
        return clipDataStr;
    }

    public boolean isValidSrcAppInfo() {
        return mSrcAppInfo != null;
    }

    public String getSrcPackageName() {
        return mSrcAppInfo.packageName;
    }

    public Drawable getSrcAppIcon() {
        return mSrcAppInfo.loadIcon(mPackageManager);
    }

    public CharSequence getSrcAppLabel() {
        return mSrcAppInfo.loadLabel(mPackageManager);
    }

    public Intent createIntent() {
        Intent intent = new Intent(mIntent);
        intent.setComponent(null);
        return intent;
    }

}
