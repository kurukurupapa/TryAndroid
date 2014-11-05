package com.example.kurukurupapa.oauth03.webviewtwitter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;

import com.example.kurukurupapa.oauth03.R;
import com.example.kurukurupapa.oauth03.menu.MyActivity;

public class WebViewActivity extends Activity {
    private static final String TAG = WebViewActivity.class.getSimpleName();

    private WebViewOAuthHelper mOAuthHelper;
    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        mWebView = (WebView) findViewById(R.id.web_view);
        mOAuthHelper = new WebViewOAuthHelper(this, mWebView, new Runnable() {
            @Override
            public void run() {
                onOAuthOk();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(TAG, "onResume called");
        mOAuthHelper.clear();
        mOAuthHelper.start();
    }

    public void onBackButtonClick(View v) {
        Log.v(TAG, "onBackButtonClick called");
        setResult(RESULT_CANCELED);
        finish();
    }

    private void onOAuthOk() {
        Log.v(TAG, "onOAuthOk called");
        Intent intent = new Intent();
        intent.putExtra(MyActivity.KEY_RESULT, mOAuthHelper.getResult());
        setResult(RESULT_OK, intent);
        finish();
    }
}
