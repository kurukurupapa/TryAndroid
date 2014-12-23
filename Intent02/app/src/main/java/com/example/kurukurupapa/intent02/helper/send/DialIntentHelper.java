package com.example.kurukurupapa.intent02.helper.send;

import android.content.Intent;

/**
 * DIALアクションのインテントを扱うヘルパークラスです。
 */
public class DialIntentHelper extends IntentHelper {

    public DialIntentHelper(String kind) {
        super(kind);
    }

    public Intent createIntent(String text) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_DIAL);
        return intent;
    }
}
