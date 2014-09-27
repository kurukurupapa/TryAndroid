package com.example.kurukurupapa.beacon01;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * メインアクティビティです。
 *
 * Bluetooth 4.0 LEを使う関係で（？）、Android 4.3（API 18）以上が必要になります。
 *
 * 参考：
 * Bluetooth Low Energy | Android Developers
 * http://developer.android.com/guide/topics/connectivity/bluetooth-le.html
 *
 * iOS - AndroidでiBeacon信号を受信してみよう - Qiita
 * http://qiita.com/KazuyukiEguchi/items/2f6c439ae8faeb06d23f
 *
 * aBeacon ~iBeacon を Android で受信する~ | ピックアップ | ギャップロ
 * http://www.gaprot.jp/pickup/ibeacon/abeacon/
 *
 * 【連載】Bluetooth LE (5) Android 4.3 で Bluetooth LE 機器を使う (フェンリル | デベロッパーズブログ)
 * http://blog.fenrir-inc.com/jp/2013/10/bluetooth-le-android.html
 */
public class MyActivity extends Activity {
    /** ログ出力用タグ */
    public static final String TAG = MyActivity.class.getCanonicalName();
    /** Bluetooth設定ダイアログ呼び出し時のリクエストコード */
    public static final int REQUEST_CODE_REQUEST_ENABLE_BT = 1;

    private TextView mTextView;
    private ListView mListView;
    private Button mScanButton;
    private Animation mShowAnim;
    private Animation mHideAnim;

    private BeaconHelper mBeaconHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // インジケータ準備
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        // UI設定
        setContentView(R.layout.activity_my);

        // オブジェクト生成
        mBeaconHelper = new BeaconHelper(this);
//        mListAdapter = new BeaconListAdapter();

        // UIオブジェクト取得
        mTextView = (TextView) findViewById(R.id.text_view);
//        mListView = (ListView) findViewById(R.id.list_view);
//        mListView.setAdapter();
        mScanButton = (Button) findViewById(R.id.scan_button);

        // アニメーション準備
        mShowAnim = AnimationUtils.loadAnimation(this, R.anim.show);
        mShowAnim.setFillAfter(true);
        mShowAnim.setFillEnabled(true);
        mHideAnim = AnimationUtils.loadAnimation(this, R.anim.hide);
        mHideAnim.setFillAfter(true);
        mHideAnim.setFillEnabled(true);
    }

    /**
     * 検出ボタンクリック時の処理です。
     * @param v 更新ボタン
     */
    public void onScanButtonClick(View v) {
        Log.i(TAG, "onScanButtonClick called");
        mBeaconHelper.startScan();
    }

    /**
     * 他のアクティビティから、当アクティビティに戻ってきた時の処理です。
     * @param requestCode リクエストコード
     * @param resultCode リザルトコード
     * @param data データ
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "onActivityResult called");
        switch (requestCode) {
            case REQUEST_CODE_REQUEST_ENABLE_BT:
                // Bluetooth設定がONに操作された場合は、検出処理を行う。
                if (resultCode == Activity.RESULT_OK) {
                    mBeaconHelper.startScan();
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void showScanStart() {
        setProgressBarIndeterminateVisibility(true);
        mScanButton.startAnimation(mHideAnim);
        Toast.makeText(this, "Beaconデバイスの検索を開始しました。", Toast.LENGTH_LONG).show();
    }

    public void showScanStop() {
        setProgressBarIndeterminateVisibility(false);
        mScanButton.startAnimation(mShowAnim);
        Toast.makeText(this, "Beaconデバイスの検索を終了しました。", Toast.LENGTH_LONG).show();
    }

    public void setText(String msg) {
        mTextView.setText(msg);
    }
}
