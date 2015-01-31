package com.example.kurukurupapa.navigationdrawer01;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

/**
 * アクションバーを操作するヘルパーです。
 */
public class ActionBarHelper {
    /**
     * プリファレンス設定に応じて、アクションバーのタイトル表示を行います。
     * @param context
     * @param actionBar
     */
    public static void setDisplay(Context context, android.support.v7.app.ActionBar actionBar) {
        setDisplay(actionBar, getTitleTypeStr(context));
    }

    /**
     * プリファレンス設定に応じて、アクションバーのタイトル表示を行います。
     * @param actionBar
     * @param titleTypeValueStr
     */
    public static void setDisplay(android.app.ActionBar actionBar, String titleTypeValueStr) {
        int titleTypeValue = Integer.parseInt(titleTypeValueStr);
        switch (titleTypeValue) {
            case 1:
                // テキストのみ
                actionBar.setDisplayShowTitleEnabled(true);
                actionBar.setDisplayShowHomeEnabled(false);
                actionBar.setDisplayShowCustomEnabled(false);
                break;
            case 2:
                // アイコン付きテキスト
                actionBar.setDisplayShowTitleEnabled(true);
                actionBar.setDisplayShowHomeEnabled(true);
                actionBar.setDisplayShowCustomEnabled(false);
                actionBar.setIcon(R.drawable.ic_launcher);
                actionBar.setDisplayUseLogoEnabled(false);
                break;
            case 3:
                // ロゴ
                actionBar.setDisplayShowTitleEnabled(false);
                actionBar.setDisplayShowHomeEnabled(true);
                actionBar.setDisplayShowCustomEnabled(true);
                actionBar.setLogo(R.drawable.logo);
                actionBar.setDisplayUseLogoEnabled(true);
                break;
        }
    }

    /**
     * プリファレンス設定に応じて、アクションバーのタイトル表示を行います。
     * @param actionBar
     * @param titleTypeValueStr
     */
    public static void setDisplay(android.support.v7.app.ActionBar actionBar, String titleTypeValueStr) {
        int titleTypeValue = Integer.parseInt(titleTypeValueStr);
        switch (titleTypeValue) {
            case 1:
                actionBar.setDisplayShowTitleEnabled(true);
                actionBar.setDisplayShowHomeEnabled(false);
                actionBar.setDisplayShowCustomEnabled(false);
                break;
            case 2:
                actionBar.setDisplayShowTitleEnabled(true);
                actionBar.setDisplayShowHomeEnabled(true);
                actionBar.setDisplayShowCustomEnabled(false);
                actionBar.setIcon(R.drawable.ic_launcher);
                actionBar.setDisplayUseLogoEnabled(false);
                break;
            case 3:
                actionBar.setDisplayShowTitleEnabled(false);
                actionBar.setDisplayShowHomeEnabled(true);
                actionBar.setDisplayShowCustomEnabled(true);
                actionBar.setLogo(R.drawable.logo);
                actionBar.setDisplayUseLogoEnabled(true);
                break;
        }
    }

    private static String getTitleTypeStr(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString("title_type", "1");
    }
}
