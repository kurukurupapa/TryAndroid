package com.example.kurukurupapa.intent02.helper.send;

import android.content.Intent;
import android.net.Uri;

/**
 * VIEWアクション（geo）のインテントを扱うヘルパークラスです。
 */
public class ViewGeoIntentHelper extends IntentHelper {

    public ViewGeoIntentHelper(String kind) {
        super(kind, true, "geo:0,0?q=東京");
    }

    public Intent createIntent(String text) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(text));
        return intent;
    }
}
