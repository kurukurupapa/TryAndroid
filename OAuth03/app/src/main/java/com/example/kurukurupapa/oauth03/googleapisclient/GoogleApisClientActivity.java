package com.example.kurukurupapa.oauth03.googleapisclient;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.example.kurukurupapa.oauth03.R;

/**
 * Androidで、AccountManagerとGoogle APIs Client Library for Javaを使用して、OAuth2.0認証を行います。
 *
 * 使用ライブラリ：
 * Google APIs Client Library for Java（https://developers.google.com/api-client-library/java/）
 * Drive API Client Library for Java（https://developers.google.com/api-client-library/java/apis/drive/v2）
 * Google Play Service（https://developer.android.com/google/play-services/index.html）
 *
 * 事前準備：
 * 当アプリ用のデジタル署名を作成し、デジタル署名から、証明書フィンガープリント（SHA1）を作成しました。
 * Google Developer Console（https://console.developers.google.com/）にて、次を行いました。
 * ・プロジェクトを追加。
 * ・「APIと認証＞API」ページにて、Drive APIなどを有効化。
 * ・「APIと認証＞認証情報」ページにて、Androidアプリ用クライアントID作成（パッケージ名、証明書フィンガープリント（SHA1）が必要）。
 * ・「APIと認証＞同意画面」ページにて、必須項目を設定。
 * 当アプリビルド時に、デジタル署名を含めるようにしました。
 *
 * 参考：
 * AndroidでのGoogle Account OAuth認証方法 - Qiita（http://qiita.com/skonb/items/97683c9b0522d753b870）
 * Android - Google APIs Client Library for JavaからGoogle Drive APIを使用する - Qiita（http://qiita.com/kubotaku1119/items/9df79c568e100c0c7623）
 */
public class GoogleApisClientActivity extends Activity {
    public static final int REQUEST_CODE_ACCOUNT_PICKER = 10;
    public static final int REQUEST_CODE_AUTHORIZATION = 20;

    private static final String TAG = GoogleApisClientActivity.class.getSimpleName();

    private GoogleApisClientOAuthHelper mOAuthHelper;
    private Button mStartButton;
    private TextView mResultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_account_manager);

        mStartButton = (Button) findViewById(R.id.start_button);
        mResultTextView = (TextView) findViewById(R.id.result_text_view);

        mOAuthHelper = new GoogleApisClientOAuthHelper(this, new Runnable() {
            @Override
            public void run() {
                onOAuthOk();
            }
        }, new Runnable() {
            @Override
            public void run() {
                onOAuthNg();
            }
        });
    }

    /**
     * スタートボタンクリック時の処理です。
     * @param v スタートボタン
     */
    public void onStartButtonClick(View v) {
        Log.v(TAG, "onStartButtonClick called");
        mResultTextView.setText("");
        mOAuthHelper.clear();
        mOAuthHelper.start();
        mStartButton.setEnabled(false);
        setProgressBarIndeterminateVisibility(true);
    }

    /**
     * 他アクティビティからの戻り時の処理です。
     * @param requestCode リクエストコード
     * @param resultCode リザルトコード
     * @param data データ
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // アカウント選択ダイアログ
            case REQUEST_CODE_ACCOUNT_PICKER:
                mOAuthHelper.onAccountPickerResult(requestCode, resultCode, data);
                break;
            // 認証ダイアログ
            case REQUEST_CODE_AUTHORIZATION:
                mOAuthHelper.onAuthorizationResult(requestCode, resultCode, data);
                break;
        }
    }

    /**
     * OAuth＆Googleサービスアクセス成功時に呼ばれます。
     */
    private void onOAuthOk() {
        Log.v(TAG, "onOAuthOk called");
        mResultTextView.setText(mOAuthHelper.getResult());
        mStartButton.setEnabled(true);
        setProgressBarIndeterminateVisibility(false);
    }

    /**
     * OAuth＆Googleサービスアクセス中止/失敗時に呼ばれます。
     */
    private void onOAuthNg() {
        Log.v(TAG, "onOAuthNg called");
        mResultTextView.setText("未取得です。");
        mStartButton.setEnabled(true);
        setProgressBarIndeterminateVisibility(false);
    }

    /**
     * 戻るボタンクリック時の処理です。
     * @param v 戻るボタン
     */
    public void onBackButtonClick(View v) {
        Log.v(TAG, "onBackButtonClick called");
        finish();
    }
}
