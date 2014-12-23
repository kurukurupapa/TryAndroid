package com.example.kurukurupapa.intent02.helper.send;

import android.app.SearchManager;
import android.content.Intent;

/**
 * EvernoteのSEARCH_NOTESアクションのインテントを扱うヘルパークラスです。
 *
 * ※Evernote for Android 2.5.1 以降で利用できます。
 * ※参考：https://dev.evernote.com/intl/jp/doc/articles/android_intents.php
 */
public class EvernoteSearchNotesIntentHelper extends IntentHelper {

    public EvernoteSearchNotesIntentHelper(String kind) {
        super(kind, true, "サンプル");
    }

    public Intent createIntent(String text) {
        Intent intent = new Intent();
        intent.setAction("com.evernote.action.SEARCH_NOTES");
        intent.putExtra(SearchManager.QUERY, text);
        return intent;
    }
}
