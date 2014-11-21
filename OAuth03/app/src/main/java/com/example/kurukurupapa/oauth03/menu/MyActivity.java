package com.example.kurukurupapa.oauth03.menu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.kurukurupapa.oauth03.R;
import com.example.kurukurupapa.oauth03.accountmanager.AccountManagerActivity;
import com.example.kurukurupapa.oauth03.browserintentgoogle20.BrowserIntentGoogle20Activity;
import com.example.kurukurupapa.oauth03.browserintenttwitter10a.BrowserIntentTwitter10aActivity;
import com.example.kurukurupapa.oauth03.browserpingoogle20.BrowserPinGoogle20Activity;
import com.example.kurukurupapa.oauth03.browserpintwitter10a.BrowserPinTwitter10aActivity;
import com.example.kurukurupapa.oauth03.googleapisclient.GoogleApisClientActivity;
import com.example.kurukurupapa.oauth03.webviewgoogle20.WebViewGoogle20Activity;
import com.example.kurukurupapa.oauth03.webviewtwitter10a.WebViewTwitter10aActivity;

import java.util.HashMap;

/**
 * Androidで、Twitter,GoogleのOAuth認証をしてみます。
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
    private static final int REQUEST_CODE_WEB_VIEW = 10;
    private static final int REQUEST_CODE_BROWSER = 20;
    private static final int REQUEST_CODE_BROWSER_PIN_GOOGLE = 21;
    private static final int REQUEST_CODE_INTENT_FILTER = 30;
    private static final int REQUEST_CODE_INTENT_FILTER_GOOGLE = 31;
    public static final String KEY_RESULT = "KEY_RESULT";

    private RadioGroup mImplementationRadioGroup;
    private RadioGroup mServiceRadioGroup;
    private HashMap<int[], Class<? extends Activity>> mActivityMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        mImplementationRadioGroup = (RadioGroup) findViewById(R.id.implementation_radio_group);
        mServiceRadioGroup = (RadioGroup) findViewById(R.id.service_radio_group);
        final RadioButton twitter10aRadioButton = (RadioButton) findViewById(R.id.twitter_10a_radio_button);
        final RadioButton google20RadioButton = (RadioButton) findViewById(R.id.google_20_radio_button);

        // ラジオボタンの組み合わせと、対応するアクティビティを定義します。
        mActivityMap = new HashMap<int[], Class<? extends Activity>>();
        mActivityMap.put(new int[]{R.id.web_view_radio_button, R.id.twitter_10a_radio_button}, WebViewTwitter10aActivity.class);
        mActivityMap.put(new int[]{R.id.web_view_radio_button, R.id.google_20_radio_button}, WebViewGoogle20Activity.class);
        mActivityMap.put(new int[]{R.id.browser_intent_radio_button, R.id.twitter_10a_radio_button}, BrowserIntentTwitter10aActivity.class);
        mActivityMap.put(new int[]{R.id.browser_intent_radio_button, R.id.google_20_radio_button}, BrowserIntentGoogle20Activity.class);
        mActivityMap.put(new int[]{R.id.browser_pin_radio_button, R.id.twitter_10a_radio_button}, BrowserPinTwitter10aActivity.class);
        mActivityMap.put(new int[]{R.id.browser_pin_radio_button, R.id.google_20_radio_button}, BrowserPinGoogle20Activity.class);
        mActivityMap.put(new int[]{R.id.google_apis_client_radio_button, R.id.google_20_radio_button}, GoogleApisClientActivity.class);
        mActivityMap.put(new int[]{R.id.account_manager_radio_button, R.id.google_20_radio_button}, AccountManagerActivity.class);

        // 2つのラジオグループで、組み合わせ不可のラジオボタンを非活性にします。
        mImplementationRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (radioGroup.getCheckedRadioButtonId() == R.id.account_manager_radio_button
                        || radioGroup.getCheckedRadioButtonId() == R.id.google_apis_client_radio_button) {
                    twitter10aRadioButton.setEnabled(false);
                    google20RadioButton.setChecked(true);
                } else {
                    twitter10aRadioButton.setEnabled(true);
                }
            }
        });
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
        for (int[] key : mActivityMap.keySet()) {
            if (key[0] == mImplementationRadioGroup.getCheckedRadioButtonId()
                    && key[1] == mServiceRadioGroup.getCheckedRadioButtonId()) {
                activityClass = mActivityMap.get(key);
            }
        }
        Intent intent = new Intent();
        intent.setClass(this, activityClass);
        startActivity(intent);
    }

    public void onImplementationRadioGroupClick(View v) {
        Log.v(TAG, "onImplementationRadioGroupClick called");
    }
}
