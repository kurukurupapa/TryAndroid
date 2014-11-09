package com.example.kurukurupapa.oauth03.browserpintwitter10a;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kurukurupapa.oauth03.R;
import com.example.kurukurupapa.oauth03.menu.MyActivity;

public class BrowserPinTwitter10aActivity extends Activity {
    private static final String TAG = BrowserPinTwitter10aActivity.class.getSimpleName();

    private BrowserPinTwitter10aOAuthHelper mOAuthHelper;
    private EditText mPinEditText;
    private TextView mResultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser_pin_twitter10a);

        mOAuthHelper = new BrowserPinTwitter10aOAuthHelper(this, new Runnable() {
            @Override
            public void run() {
                onOAuthOk();
            }
        });
        mPinEditText = (EditText) findViewById(R.id.pin_edit_text);
        mResultTextView = (TextView) findViewById(R.id.result_text_view);
    }

    public void onBrowserButtonClick(View v) {
        Log.v(TAG, "onBrowserButtonClick called");
        mPinEditText.setText("");
        mResultTextView.setText("");
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
        mResultTextView.setText(mOAuthHelper.getResult());
    }
}
