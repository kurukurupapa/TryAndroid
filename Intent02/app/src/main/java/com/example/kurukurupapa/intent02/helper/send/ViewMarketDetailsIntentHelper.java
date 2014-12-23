package com.example.kurukurupapa.intent02.helper.send;

import android.content.Intent;
import android.net.Uri;

/**
 * VIEWアクション（market://details）のインテントを扱うヘルパークラスです。
 */
public class ViewMarketDetailsIntentHelper extends IntentHelper {

    public ViewMarketDetailsIntentHelper(String kind) {
        super(kind, true, "market://details?id=com.google.android.gm");
    }

    public Intent createIntent(String text) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(text));
        return intent;
    }
}
