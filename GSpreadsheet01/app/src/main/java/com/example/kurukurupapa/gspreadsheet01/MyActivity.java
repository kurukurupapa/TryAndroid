package com.example.kurukurupapa.gspreadsheet01;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

/**
 * Google Data Java Client Library、Google Sheets API version 3.0 を使用して、
 * Googleスプレッドシートにアクセスしてみます。
 *
 * 認証は、ClientLogin方式（アプリ用ID,パスワードを使用）で行っています。
 * ※OAuth未使用です。
 *
 * 使用ライブラリ：
 * gdata-java-client - Google Data Java Client Library - Google Project Hosting
 * https://code.google.com/p/gdata-java-client/
 * ※gdata-samples.java-1.47.1.zipを使用しました。
 * 　2012年3月で開発が止まっている？
 *
 * Google Sheets API version 3.0 - Google Apps Platform — Google Developers
 * https://developers.google.com/google-apps/spreadsheets/
 */
public class MyActivity extends Activity {
    private static final String TAG = MyActivity.class.getSimpleName();

    private TextView mTextView;
    private Button mQueryButton;
    private QueryAsyncTask mTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_my);

        // UIオブジェクト取得
        mTextView = (TextView) findViewById(R.id.msg_text_view);
        mQueryButton = (Button) findViewById(R.id.query_button);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // ボタン表示制御
        String username = PreferenceManager.getDefaultSharedPreferences(this).getString("user_text", "");
        boolean validUsername = !username.equals("");
        mQueryButton.setEnabled(validUsername);
    }

    /**
     * 設定ボタンクリック時の処理です。
     * @param v 設定ボタン
     */
    public void onSettingsButtonClick(View v) {
        Intent intent = new Intent();
        intent.setClass(this, SettingsActivity.class);
        startActivity(intent);
    }

    /**
     * 検索ボタンクリック時の処理です。
     * @param v 検索ボタン
     */
    public void onQueryButtonClick(View v) {
        // クリア
        clearMsg();

        // Google接続情報を取得
        String username = PreferenceManager.getDefaultSharedPreferences(this).getString("user_text", "");
        String password = PreferenceManager.getDefaultSharedPreferences(this).getString("password_text", "");

        // Googleスプレッドシート検索開始
        mTask = new QueryAsyncTask(username, password, new Runnable() {
            @Override
            public void run() {
                // 画面反映
                setQueryResult(mTask.getResult());
            }
        });
        mTask.execute();

        // インジケータ表示
        setProgressBarIndeterminateVisibility(true);
    }

    /**
     * 前回の検索結果をクリアします。
     */
    protected void clearMsg() {
        mTextView.setText("");
    }

    /**
     * 検索結果を表示します。
     * @param result 検索結果
     */
    public void setQueryResult(String result) {
        // 結果表示
        mTextView.setText(result);

        // インジケータ非表示
        setProgressBarIndeterminateVisibility(false);
    }

}
