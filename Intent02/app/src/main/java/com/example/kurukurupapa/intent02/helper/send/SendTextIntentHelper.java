package com.example.kurukurupapa.intent02.helper.send;

import android.content.Intent;

/**
 * SENDアクション（text/plain）のインテントを扱うヘルパークラスです。
 */
public class SendTextIntentHelper extends IntentHelper {

    public SendTextIntentHelper(String kind) {
        super(kind, true, "サンプルテキストです。");
    }

    public Intent createIntent(String text) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType(TYPE_TEXT_PLAIN);
        intent.putExtra(Intent.EXTRA_TEXT, text);
        return intent;
    }
}
