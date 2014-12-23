package com.example.kurukurupapa.intent02.activity;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kurukurupapa.intent02.R;
import com.example.kurukurupapa.intent02.service.SendService;

/**
 * インテント送信アクティビティ
 *
 * インテントを送信するアクティビティです。
 */
public class SendActivity extends Activity {
    private static final String TAG = SendActivity.class.getSimpleName();

    private SendService mSendService;
    private Spinner mKindSpinner;
    private TextView mText1LabelTextView;
    private EditText mText1EditText;
    private Animation mShowAnimation;

    /**
     * アクティビティ作成時の処理です。
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);

        // オブジェクト生成
        mSendService = new SendService(this);

        // UIオブジェクト取得
        mKindSpinner = (Spinner) findViewById(R.id.kind_spinner);
        mText1LabelTextView = (TextView) findViewById(R.id.text1_label_text_view);
        mText1EditText = (EditText) findViewById(R.id.text1_edit_text);
        mShowAnimation = AnimationUtils.loadAnimation(this, R.anim.show);

        // 種類スピナーに選択肢を設定します。
        ArrayAdapter<String> actionAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mSendService.getKindArr());
        actionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mKindSpinner.setAdapter(actionAdapter);
        mKindSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                onKindSpinnerItemSelected(adapterView, view, position, id);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // 何もしない
            }
        });
    }

    private void onKindSpinnerItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
        mSendService.setSelectedKind(position);
        mText1EditText.setText(mSendService.getDefaultText());
        mText1EditText.setEnabled(mSendService.useText());
        if (mSendService.useText()) {
            mText1LabelTextView.setVisibility(View.VISIBLE);
            mText1LabelTextView.startAnimation(mShowAnimation);
            mText1EditText.setVisibility(View.VISIBLE);
            mText1EditText.startAnimation(mShowAnimation);
        } else {
            mText1LabelTextView.setVisibility(View.GONE);
            mText1EditText.setVisibility(View.GONE);
        }
    }

    /**
     * 送信ボタンクリック時の処理です。
     * @param v 送信ボタン
     */
    public void onSendButtonClick(View v) {
        // インテント作成
        String text1 = mText1EditText.getText().toString();
        Intent intent = mSendService.createIntent(text1);

        // インテント送信
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
