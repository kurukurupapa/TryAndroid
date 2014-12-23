package com.example.kurukurupapa.intent02.helper.send;

import android.content.Intent;

/**
 * Android設定のインテントを扱うヘルパークラスです。
 */
public class AndroidSettingsIntentHelper extends IntentHelper {

    public AndroidSettingsIntentHelper(String kind) {
        super(kind);
    }

    public Intent createIntent(String text) {
        Intent intent = new Intent();
        intent.setAction("android.settings.SETTINGS");
        return intent;
    }
}
