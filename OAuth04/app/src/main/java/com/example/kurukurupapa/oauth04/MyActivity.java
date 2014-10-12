package com.example.kurukurupapa.oauth04;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

/**
 * Androidで、Google APIs Client Library for Javaを使用して、OAuth2.0認証を行います。
 *
 * 使用ライブラリ：
 * Google APIs Client Library for Java（https://developers.google.com/api-client-library/java/）
 * Drive API Client Library for Java（https://developers.google.com/api-client-library/java/apis/drive/v2）
 * Google Play Service（https://developer.android.com/google/play-services/index.html）
 *
 * 事前準備：
 * 当アプリ用のデジタル署名を作成しました。
 * デジタル署名から、証明書フィンガープリント（SHA1）を作成しました。
 *
 * Google Developer Console（https://console.developers.google.com/）にて、次を行いました。
 * ・プロジェクトを追加。
 * ・「APIと認証＞API」ページにて、Drive APIなどを有効化。
 * ・「APIと認証＞認証情報」ページにて、Androidアプリ用クライアントID作成（パッケージ名、証明書フィンガープリント（SHA1）が必要）。
 * ・「APIと認証＞同意画面」ページにて、必須項目を設定。
 *
 * 当アプリビルド時に、デジタル署名を含めるようにしました。
 *
 * 参考：
 * AndroidでのGoogle Account OAuth認証方法 - Qiita（http://qiita.com/skonb/items/97683c9b0522d753b870）
 * Android - Google APIs Client Library for JavaからGoogle Drive APIを使用する - Qiita（http://qiita.com/kubotaku1119/items/9df79c568e100c0c7623）
 */
public class MyActivity extends Activity {
    public static final int REQUEST_CODE_ACCOUNT_PICKER = 1;
    public static final int REQUEST_CODE_AUTHORIZATION = 2;

    private static final String TAG = MyActivity.class.getSimpleName();

    private OAuthHelper mOAuthHelper;
    private TextView mTextView;
    private Button mStartButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_my);

        mTextView = (TextView) findViewById(R.id.text_view);
        mStartButton = (Button) findViewById(R.id.start_button);

        mOAuthHelper = new OAuthHelper(this, new Runnable() {
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

    /**
     * スタートボタンクリック時の処理です。
     * @param v スタートボタン
     */
    public void onStartButtonClick(View v) {
        Log.v(TAG, "onStartButtonClick called");
        mOAuthHelper.clear();
        mOAuthHelper.start();
        mStartButton.setEnabled(false);
        setProgressBarIndeterminateVisibility(true);
    }

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
        mTextView.setText(mOAuthHelper.getResult());
        mStartButton.setEnabled(true);
        setProgressBarIndeterminateVisibility(false);
    }

    /**
     * OAuth＆Googleサービスアクセス中止/失敗時に呼ばれます。
     */
    private void onOAuthNg() {
        Log.v(TAG, "onOAuthNg called");
        mTextView.setText("未取得です。");
        mStartButton.setEnabled(true);
        setProgressBarIndeterminateVisibility(false);
    }
}
