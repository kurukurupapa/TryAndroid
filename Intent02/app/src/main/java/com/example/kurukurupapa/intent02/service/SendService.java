package com.example.kurukurupapa.intent02.service;

import android.content.Context;
import android.content.Intent;

import com.example.kurukurupapa.intent02.helper.send.IntentHelper;
import com.example.kurukurupapa.intent02.helper.send.IntentSetHelper;

/**
 * インテント送信に関するサービスクラスです。
 */
public class SendService {
    private IntentSetHelper mIntentSetHelper;
    private IntentHelper mIntentHelper;

    public SendService(Context context) {
        mIntentSetHelper = new IntentSetHelper(context);
    }

    public String[] getKindArr() {
        return mIntentSetHelper.getKindArr();
    }

    public void setSelectedKind(int index) {
        mIntentHelper = mIntentSetHelper.getWithIndex(index);
    }

    public String getDefaultText() {
        return mIntentHelper.getDefaultText();
    }

    public boolean useText() {
        return mIntentHelper.useText();
    }

    public Intent createIntent(String text) {
        return mIntentHelper.createIntent(text);
    }
}
