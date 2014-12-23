package com.example.kurukurupapa.intent02.helper.send;

import android.content.Intent;

/**
 * POWER_USAGE_SUMMARYアクションのインテントを扱うヘルパークラスです。
 */
public class PowerUsageSummaryIntentHelper extends IntentHelper {

    public PowerUsageSummaryIntentHelper(String kind) {
        super(kind);
    }

    public Intent createIntent(String text) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_POWER_USAGE_SUMMARY);
        return intent;
    }
}
