package com.example.kurukurupapa.intent02.helper.send;

import android.content.Context;
import android.content.Intent;

import com.example.kurukurupapa.intent02.R;

import java.util.ArrayList;

/**
 * EvernoteのSEARCH_NOTESアクションのインテントを扱うヘルパークラスです。
 *
 * ※Evernote for Android 3.0 以降で利用できます。
 * ※参考：https://dev.evernote.com/intl/jp/doc/articles/android_intents.php
 */
public class EvernoteCreateNewNoteIntentHelper extends IntentHelper {
    private Context mContext;

    public EvernoteCreateNewNoteIntentHelper(String kind, Context context) {
        super(kind, true, "新規ノート作成のテストです。");
        mContext = context;
    }

    public Intent createIntent(String text) {
        String appName = mContext.getString(R.string.app_name);
        ArrayList<String> tagNameList = new ArrayList<String>();
        tagNameList.add(appName);

        Intent intent = new Intent();
        intent.setAction("com.evernote.action.CREATE_NEW_NOTE");
        intent.putExtra(Intent.EXTRA_TITLE, "テスト");
        intent.putExtra(Intent.EXTRA_TEXT, text);
        intent.putStringArrayListExtra("TAG_NAME_LIST", tagNameList);
        intent.putExtra("SOURCE_APP", appName);
        return intent;
    }
}
