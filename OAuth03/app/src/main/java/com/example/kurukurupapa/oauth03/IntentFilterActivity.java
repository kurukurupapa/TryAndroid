package com.example.kurukurupapa.oauth03;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class IntentFilterActivity extends Activity {
    private static final String TAG = IntentFilterActivity.class.getSimpleName();

    private IntentFilterOAuthHelper mOAuthHelper;
    private Uri mUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intent_filter);

        mOAuthHelper = new IntentFilterOAuthHelper(this, new Runnable() {
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

        if (mUri == null) {
            mOAuthHelper.clear();
            mOAuthHelper.start();
        } else {
            mOAuthHelper.setOAuthVerifier(mUri);
            mOAuthHelper.start();
            mUri = null;
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.v(TAG, "onNewIntent called");
        Log.v(TAG, "data=" + intent.getDataString());
        mUri = intent.getData();
    }

    public void onBackButtonClick(View v) {
        Log.v(TAG, "onBackButtonClick called");
        setResult(RESULT_CANCELED);
        finish();
    }

    private void onOAuthOk() {
        Log.v(TAG, "onOAuthOk called");
        finish();

        // 当アクティビティには、launchMode="singleInstance"を設定したため、呼び元のアクティビティとタスクが分かれています。
        // 上記のfinishメソッド呼び出しだけでは、呼び元のアクティビティに戻れませんので、次の処理を行います。
        Intent intent = new Intent();
        intent.setClass(this, MyActivity.class);
        intent.putExtra(MyActivity.KEY_RESULT, mOAuthHelper.getResult());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
