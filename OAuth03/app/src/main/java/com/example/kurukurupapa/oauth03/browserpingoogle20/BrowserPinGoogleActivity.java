package com.example.kurukurupapa.oauth03.browserpingoogle20;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.kurukurupapa.oauth03.menu.MyActivity;
import com.example.kurukurupapa.oauth03.R;

public class BrowserPinGoogleActivity extends Activity {
    private static final String TAG = BrowserPinGoogleActivity.class.getSimpleName();

    private BrowserPinGoogleOAuthHelper mOAuthHelper;
    private EditText mPinEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser_pin_google);

        mOAuthHelper = new BrowserPinGoogleOAuthHelper(this, new Runnable() {
            @Override
            public void run() {
                onOAuthOk();
            }
        });
        mPinEditText = (EditText) findViewById(R.id.pin_edit_text);
    }

    public void onBrowserButtonClick(View v) {
        Log.v(TAG, "onBrowserButtonClick called");
        mPinEditText.setText("");
        mOAuthHelper.clear();
        mOAuthHelper.start();
    }

    public void onAuthButtonClick(View v) {
        Log.v(TAG, "onAuthButtonClick called");
        String pin = mPinEditText.getText().toString();
        if (pin.isEmpty()) {
            Toast.makeText(this, "PINが入力されていません。", Toast.LENGTH_LONG).show();
        } else {
            mOAuthHelper.setOAuthPin(pin);
            mOAuthHelper.start();
        }
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
