package com.example.kurukurupapa.intent02.helper.send;

import android.content.Intent;
import android.net.Uri;

import java.util.ArrayList;

/**
 * SEND_MULTIPLEアクション（画像）のインテントを扱うヘルパークラスです。
 */
public class SendMultipleImageIntentHelper extends IntentHelper {

    public SendMultipleImageIntentHelper(String kind) {
        super(kind);
    }

    public Intent createIntent(String text) {
        ArrayList<Uri> uris = new ArrayList<Uri>();
        uris.add(Uri.parse("android.resource://com.example.kurukurupapa.intent02/raw/stocker_jp_01.jpg"));
        uris.add(Uri.parse("android.resource://com.example.kurukurupapa.intent02/raw/stocker_jp_02.jpg"));

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND_MULTIPLE);
        intent.setType(TYPE_IMAGE_JPG);
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
        return intent;
    }
}
