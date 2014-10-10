package com.example.kurukurupapa.oauth03;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.kurukurupapa.oauth03.browsertwitter.BrowserActivity;
import com.example.kurukurupapa.oauth03.intentfiltergoogle.IntentFilterGoogleActivity;
import com.example.kurukurupapa.oauth03.intentfiltertwitter.IntentFilterActivity;
import com.example.kurukurupapa.oauth03.webviewtwitter.WebViewActivity;

/**
 * Androidで、TwitterのOAuth認証をしてみます。
 *
 * Scribeライブラリを使用します。
 *
 * scribe-java - シンプルなOAuth Javaライブラリ - オープンソース・ソフトウェア
 * http://www.syboos.jp/oss/doc/scribe-java.html
 * ※ほぼ、こちらのページのコピペです。
 *
 * REST APIs | Twitter Developers
 * https://dev.twitter.com/rest/public
 */
public class MyActivity extends Activity {
    private static final String TAG = MyActivity.class.getSimpleName();
    private static final int REQUEST_CODE_WEB_VIEW = 1;
    private static final int REQUEST_CODE_BROWSER = 2;
    private static final int REQUEST_CODE_INTENT_FILTER = 3;
    private static final int REQUEST_CODE_INTENT_FILTER_GOOGLE = 4;
    public static final String KEY_RESULT = "KEY_RESULT";

    private RadioGroup mRadioGroup;
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        mRadioGroup = (RadioGroup) findViewById(R.id.radio_group);
        mTextView = (TextView) findViewById(R.id.text_view);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.my, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//        if (id == R.id.action_settings) {
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(TAG, "onResume called");

        Intent intent = getIntent();
        String result = intent.getStringExtra(KEY_RESULT);
        if (result != null) {
            setResult(result);
        }
    }

    public void onStartButtonClick(View v) {
        Log.v(TAG, "onStartButtonClick called");

        Class activityClass = null;
        int requestCode = -1;
        switch (mRadioGroup.getCheckedRadioButtonId()) {
            case R.id.web_view_radio_button:
                activityClass = WebViewActivity.class;
                requestCode = REQUEST_CODE_WEB_VIEW;
                break;
            case R.id.browser_radio_button:
                activityClass = BrowserActivity.class;
                requestCode = REQUEST_CODE_BROWSER;
                break;
            case R.id.intent_filter_radio_button:
                activityClass = IntentFilterActivity.class;
                requestCode = REQUEST_CODE_INTENT_FILTER;
                break;
            case R.id.intent_filter_google_radio_button:
                activityClass = IntentFilterGoogleActivity.class;
                requestCode = REQUEST_CODE_INTENT_FILTER_GOOGLE;
                break;
        }
        Intent intent = new Intent();
        intent.setClass(this, activityClass);
        startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch (resultCode) {
            case RESULT_OK:
                setResult(intent.getStringExtra(KEY_RESULT));
                break;
            default:
                setResult("");
                break;
        }
    }

    private void setResult(String result) {
        if (result == null || result.isEmpty()) {
            mTextView.setText("");
            return;
        }
        mTextView.setText(result);
    }
}
