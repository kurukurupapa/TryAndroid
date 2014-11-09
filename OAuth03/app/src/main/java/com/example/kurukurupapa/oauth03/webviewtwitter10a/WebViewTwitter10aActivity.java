package com.example.kurukurupapa.oauth03.webviewtwitter10a;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import com.example.kurukurupapa.oauth03.R;

public class WebViewTwitter10aActivity extends Activity {
    private static final String TAG = WebViewTwitter10aActivity.class.getSimpleName();

    private WebViewTwitter10aOAuthHelper mOAuthHelper;
    private TextView mResultTextView;
    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view_twitter10a);

        mWebView = (WebView) findViewById(R.id.web_view);
        mResultTextView = (TextView) findViewById(R.id.result_text_view);
        mOAuthHelper = new WebViewTwitter10aOAuthHelper(this, mWebView, new Runnable() {
            @Override
            public void run() {
                onOAuthOk();
            }
        });

        showWebView();
    }

    /**
     * 開始ボタンクリック時の処理です。
     * @param v 開始ボタン
     */
    public void onStartButtonClick(View v) {
        Log.v(TAG, "onStartButtonClick called");
        mOAuthHelper.clear();
        mOAuthHelper.start();
        showWebView();
    }

    /**
     * 戻るボタンクリック時の処理です。
     * @param v 戻るボタン
     */
    public void onBackButtonClick(View v) {
        Log.v(TAG, "onBackButtonClick called");
        setResult(RESULT_CANCELED);
        finish();
    }

    /**
     * OAuth正常終了時の処理です。
     */
    private void onOAuthOk() {
        Log.v(TAG, "onOAuthOk called");
        showResult();
    }

    /**
     * 認証画面を表示します。
     */
    private void showWebView() {
        mResultTextView.setText("");
        mWebView.setVisibility(View.VISIBLE);
        mResultTextView.setVisibility(View.GONE);
    }

    /**
     * 結果を表示します。
     */
    private void showResult() {
        mResultTextView.setText(mOAuthHelper.getResult());
        mWebView.setVisibility(View.GONE);
        mResultTextView.setVisibility(View.VISIBLE);
    }
}
