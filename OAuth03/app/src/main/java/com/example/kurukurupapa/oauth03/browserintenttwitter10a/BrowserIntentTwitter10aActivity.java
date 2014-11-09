package com.example.kurukurupapa.oauth03.browserintenttwitter10a;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.kurukurupapa.oauth03.R;
import com.example.kurukurupapa.oauth03.menu.MyActivity;

public class BrowserIntentTwitter10aActivity extends Activity {
    private static final String TAG = BrowserIntentTwitter10aActivity.class.getSimpleName();

    private BrowserIntentTwitter10aOAuthHelper mOAuthHelper;
    private Uri mUri;
    private TextView mResultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser_intent_twitter10a);

        mOAuthHelper = new BrowserIntentTwitter10aOAuthHelper(this, new Runnable() {
            @Override
            public void run() {
                onOAuthOk();
            }
        });
        mResultTextView = (TextView) findViewById(R.id.result_text_view);
    }

    public void onStartButtonClick(View v) {
        Log.v(TAG, "onStartButtonClick called");
        mResultTextView.setText("");
        mOAuthHelper.clear();
        mOAuthHelper.start();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.v(TAG, "onNewIntent called");
        Log.v(TAG, "data=" + intent.getDataString());
        mUri = intent.getData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(TAG, "onResume called");

        if (mUri != null) {
            // ブラウザアプリから戻ってきた場合
            mOAuthHelper.setOAuthVerifier(mUri);
            mOAuthHelper.start();
            mUri = null;
        }
    }

    private void onOAuthOk() {
        Log.v(TAG, "onOAuthOk called");
        mResultTextView.setText(mOAuthHelper.getResult());
    }

    public void onBackButtonClick(View v) {
        Log.v(TAG, "onBackButtonClick called");
        finish();

        // 当アクティビティには、launchMode="singleInstance"を設定したため、呼び元のアクティビティとタスクが分かれています。
        // 上記のfinishメソッド呼び出しだけでは、呼び元のアクティビティに戻れませんので、次の処理を行います。

        // アプリ二重起動について - 肉になるメモ
        // http://kazumeat.hatenablog.com/entry/20110125/1295941612
        //
        // Y.A.M の 雑記帳: Android launchMode の違い
        // http://y-anz-m.blogspot.jp/2011/02/androidlauchmode.html
        //
        // IntentのCategoryとExtraとFlagの一覧表を作ってみたよ - hyoromoのブログ
        // http://hyoromo.hatenablog.com/entry/20091003/1254590170

        Intent intent = new Intent();
        intent.setClass(this, MyActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
