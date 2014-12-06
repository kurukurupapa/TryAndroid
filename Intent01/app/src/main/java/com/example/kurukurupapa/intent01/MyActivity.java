package com.example.kurukurupapa.intent01;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.apache.commons.lang3.StringUtils;

/**
 * インテントを送信するアクティビティです。
 */
public class MyActivity extends Activity {
    private static final String TAG = MyActivity.class.getSimpleName();
    private static final String TYPE_NONE = "未選択";

    private Spinner mActionSpinner;
    private Spinner mTypeSpinner;
    private EditText mExtraTextEditText;

    /**
     * アクティビティ作成時の処理です。
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        // UIオブジェクト取得
        mActionSpinner = (Spinner) findViewById(R.id.action_spinner);
        mTypeSpinner = (Spinner) findViewById(R.id.type_spinner);
        mExtraTextEditText = (EditText) findViewById(R.id.extra_text_edit_text);

        // Actionスピナーに選択肢を設定します。
        // ひとまず1件のみです。
        String[] actionItems = new String[] {
                Intent.ACTION_SEND,
        };
        ArrayAdapter<String> actionAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, actionItems);
        actionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mActionSpinner.setAdapter(actionAdapter);

        // Typeスピナーに選択肢を設定します。
        String[] typeItems = new String[] {
                "text/plain",
                TYPE_NONE,
        };
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, typeItems);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mTypeSpinner.setAdapter(typeAdapter);
    }

    /**
     * 送信ボタンクリック時の処理です。
     * @param v 送信ボタン
     */
    public void onSendButtonClick(View v) {
        // 送信するインテントを作成します。
        String action = mActionSpinner.getSelectedItem().toString();
        String type = mTypeSpinner.getSelectedItem().toString();
        CharSequence text = mExtraTextEditText.getText();
        Intent intent = new Intent();
        intent.setAction(action);
        if (!type.equals(TYPE_NONE)) {
            intent.setType(type);
        }
        if (!StringUtils.isEmpty(text)) {
            intent.putExtra(Intent.EXTRA_TEXT, text);
        }

        // インテントを送信します。
        try {
            startActivity(intent);
        }
        catch (ActivityNotFoundException e) {
            String msg = "インテント送信に失敗しました。";
            Log.d(TAG, msg, e);
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        }
    }
}
