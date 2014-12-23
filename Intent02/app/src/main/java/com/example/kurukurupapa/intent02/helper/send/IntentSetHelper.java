package com.example.kurukurupapa.intent02.helper.send;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * Actionの集まりを操作するヘルパークラスです。
 */
public class IntentSetHelper {
    private IntentHelper[] mArr;
    private String[] mKindArr;

    public IntentSetHelper(Context context) {
        mArr = new IntentHelper[] {
            new SendTextIntentHelper("テキスト共有"),
            new SendMultipleImageIntentHelper("画像共有（複数件）"),
            new DialIntentHelper("ダイヤル画面へ"),
            new SendtoMailtoIntentHelper("メーラーへ"),
            new ViewHttpIntentHelper("ブラウザへ"),
            new ViewGeoIntentHelper("地図へ"),
            new ViewMarketSearchIntentHelper("Playストア 検索へ"),
            new ViewMarketDetailsIntentHelper("Playストア アプリ詳細へ"),
            new WebSearchIntentHelper("Web検索へ"),
            // Android機能
            new AndroidSettingsIntentHelper("Android設定へ"),
            new PowerUsageSummaryIntentHelper("電池使用サマリ画面へ"),
            // 各アプリ
            new EvernoteCreateNewNoteIntentHelper("Evernote 新規ノート作成へ", context),
            new EvernoteSearchNotesIntentHelper("Evernote 検索へ"),
        };

        List<String> list = new ArrayList<String>();
        for (IntentHelper obj : mArr) {
            list.add(obj.getKind());
        }
        mKindArr = list.toArray(new String[]{});
    }

    public String[] getKindArr() {
        return mKindArr;
    }

    public IntentHelper getWithIndex(int index) {
        return mArr[index];
    }
}
