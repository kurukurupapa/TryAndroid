package com.example.kurukurupapa.intent02.helper.send;

import android.content.Intent;
import android.net.Uri;

/**
 * SENDTOアクション（mailto）のインテントを扱うヘルパークラスです。
 */
public class SendtoMailtoIntentHelper extends IntentHelper {

    public SendtoMailtoIntentHelper(String kind) {
        super(kind, true, "テストメールです。");
    }

    public Intent createIntent(String text) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:xxxxx@google.com"));
        intent.putExtra(Intent.EXTRA_SUBJECT, "テストメール");
        intent.putExtra(Intent.EXTRA_TEXT, text);
        return intent;
    }
}
