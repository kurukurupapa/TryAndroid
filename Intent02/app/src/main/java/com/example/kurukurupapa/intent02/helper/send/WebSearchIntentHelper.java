package com.example.kurukurupapa.intent02.helper.send;

import android.app.SearchManager;
import android.content.Intent;

/**
 * WEB_SEARCHアクションのインテントを扱うヘルパークラスです。
 */
public class WebSearchIntentHelper extends IntentHelper {

    public WebSearchIntentHelper(String kind) {
        super(kind, true, "サンプル 検索語");
    }

    public Intent createIntent(String text) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_WEB_SEARCH);
        intent.putExtra(SearchManager.QUERY, text);
        return intent;
    }
}
