package com.example.kurukurupapa.oauth03;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

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
        }
        Intent intent = new Intent();
        intent.setClass(this, activityClass);
        startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch (resultCode) {
            case RESULT_OK:
                String result = intent.getStringExtra(KEY_RESULT);
                mTextView.setText(result);
                break;
            default:
                mTextView.setText("");
                break;
        }
    }

}
