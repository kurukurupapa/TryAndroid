package com.example.kurukurupapa.intent02.helper.send;

import android.content.Intent;
import android.net.Uri;

/**
 * VIEWアクション（market://search）のインテントを扱うヘルパークラスです。
 */
public class ViewMarketSearchIntentHelper extends IntentHelper {

    public ViewMarketSearchIntentHelper(String kind) {
        super(kind, true, "market://search?q=Gmail");
    }

    public Intent createIntent(String text) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(text));
        return intent;
    }
}
