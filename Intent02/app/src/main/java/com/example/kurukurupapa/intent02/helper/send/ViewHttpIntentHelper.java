package com.example.kurukurupapa.intent02.helper.send;

import android.content.Intent;
import android.net.Uri;

/**
 * VIEWアクション（http）のインテントを扱うヘルパークラスです。
 */
public class ViewHttpIntentHelper extends IntentHelper {

    public ViewHttpIntentHelper(String kind) {
        super(kind, true, "http://www.google.co.jp/");
    }

    public Intent createIntent(String text) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(text));
        return intent;
    }
}
