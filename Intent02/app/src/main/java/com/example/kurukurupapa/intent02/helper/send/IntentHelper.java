package com.example.kurukurupapa.intent02.helper.send;

import android.content.Intent;

/**
 * インテントを扱うヘルパー抽象クラスです。
 */
public abstract class IntentHelper {
    protected static final String TYPE_TEXT_PLAIN = "text/plain";
    protected static final String TYPE_IMAGE_JPG = "image/jpg";

    private String mKind;
    private boolean mUseTextFlag;
    private String mDefaultText;

    public IntentHelper(String kind) {
        this(kind, false);
    }

    public IntentHelper(String kind, boolean useTextFlag) {
        this(kind, useTextFlag, "");
    }
    public IntentHelper(String kind, boolean useTextFlag, String defaultText) {
        mKind = kind;
        mUseTextFlag = useTextFlag;
        mDefaultText = defaultText;
    }

    public String getKind() {
        return mKind;
    }

    public boolean useText() {
        return mUseTextFlag;
    }

    public String getDefaultText() {
        return mDefaultText;
    }

    public abstract Intent createIntent(String text);
}
