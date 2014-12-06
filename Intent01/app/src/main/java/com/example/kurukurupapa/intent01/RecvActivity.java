package com.example.kurukurupapa.intent01;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * インテントを受信するアクティビティです。
 */
public class RecvActivity extends Activity {
    private static final String TAG = RecvActivity.class.getSimpleName();

    private Intent mIntent;
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recv);

        // インテント取得（初回分）
        mIntent = getIntent();

        // UIオブジェクト取得
        mTextView = (TextView) findViewById(R.id.description_text_view);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        // インテントを保持（2回目以降分）
        mIntent = intent;
    }

    @Override
    protected void onResume() {
        super.onResume();

        // インテントの内容を画面表示
        mTextView.setText(makeText(mIntent));
    }

    private String makeText(Intent intent) {
        return ""
                + "-- 主な項目 --" + "\n"
                + "Action: " + intent.getAction() + "\n"
                + "Categories: " + ToStringBuilder.reflectionToString(intent.getCategories()) + "\n"
                + "Type: " + intent.getType() + "\n"
                + "-- 詳細項目 --" + "\n"
                + ToStringBuilder.reflectionToString(intent, ToStringStyle.MULTI_LINE_STYLE) + "\n"
                ;
    }

}
