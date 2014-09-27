package com.example.kurukurupapa.titlebarprogress01;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

/**
 * インジケータ/プログレスバーの練習です。
 *
 * 参考：
 * Androidアプリ開発！！: Webの表示-02(WebView,requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);, )
 * http://androidgamepark.blogspot.jp/2013/10/web-01webviewrequestwindowfeaturewindow.html
 *
 * [Android]指定した時間後にちょっとした処理を行う方法 ｜ Developers.IO
 * http://dev.classmethod.jp/smartphone/android/android-tas/
 */
public class MyActivity extends Activity {
    /** ログ出力のタグ */
    private static final String TAG = MyActivity.class.getSimpleName();
    /** 進捗カウントアップの場合に、プログレスバーを更新する間隔 */
    private static final long DELAY = 100;

    private LinearLayout mStartLayout;
    private LinearLayout mStopLayout;
    private Button mStartButton;
    private Button mStopButton;
    private RadioButton mIndeterminateRadioButton;
    private RadioButton mCountableRadioButton;
    private Handler mHandler;
    private Runnable mRunnable;
    private int mProgress;
    private Animation mShowAnim;
    private Animation mHideAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // タイトルバーの右隅にインジケータ（丸がクルクル回るアニメーション）を表示する準備です。
        // ※この段階では表示されません。
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        // タイトルバーにプログレスバーを表示する準備です。
        // ※この段階では表示されません。
        requestWindowFeature(Window.FEATURE_PROGRESS);

        // 上記のrequestWindowFeatureメソッド呼び出し後に、setContentViewを呼びます。
        setContentView(R.layout.activity_my);

        // UIオブジェクトを取得します。
        mStartLayout = (LinearLayout) findViewById(R.id.start_layout);
        mStopLayout = (LinearLayout) findViewById(R.id.stop_layout);
        mStartButton = (Button) findViewById(R.id.start_button);
        mStopButton = (Button) findViewById(R.id.stop_button);
        mIndeterminateRadioButton = (RadioButton) findViewById(R.id.indeterminate_radio_button);
        mCountableRadioButton = (RadioButton) findViewById(R.id.countable_radio_button);

        // アニメーションを準備します。
        mShowAnim = AnimationUtils.loadAnimation(this, R.anim.show_anim);
        mShowAnim.setFillAfter(true);
        mShowAnim.setFillEnabled(true);
        mHideAnim = AnimationUtils.loadAnimation(this, R.anim.hide_anim);
        mHideAnim.setFillAfter(true);
        mHideAnim.setFillEnabled(true);

        // プログレスバー操作用
        mHandler = new Handler();

        // UIの操作可否を切り替える。
        enableStartLayout();
    }

    public void onStartButtonClick(View v) {
        Log.i(TAG, "onStartButtonClick called.");

        // UIの操作可否を切り替える。
        enableStopLayout();

        // 進捗表示形式のラジオボタンの選択状態を取得します。
        if (mIndeterminateRadioButton.isChecked()) {
            startIndeterminate();
        } else {
            startCountable();
        }
    }

    private void startIndeterminate() {
        // インジケータ・プログレスバーの両方を表示します。
        //setProgressBarVisibility(true);

        // インジケータを表示します。
        setProgressBarIndeterminateVisibility(true);

        // プログレスバーを表示します。
        setProgressBarIndeterminate(true);
    }

    private void startCountable() {
        // インジケータは、進捗量を表示できないようです。
        setProgressBarIndeterminateVisibility(false);

        // プログレスバーを表示します。
        setProgressBarIndeterminate(false);
        setProgress(0);

        // プログレスバー更新処理を登録します。
        mProgress = 0;
        if (mRunnable == null) {
            mRunnable = new Runnable() {
                @Override
                public void run() {
                    if (mProgress < 10000) {
                        mProgress += 500;
                        setProgress(mProgress);
                        mHandler.removeCallbacks(mRunnable);
                        mHandler.postDelayed(mRunnable, DELAY);
                    } else {
                        onStopButtonClick(null);
                    }
                }
            };
        }
        mHandler.postDelayed(mRunnable, DELAY);
    }

    public void onStopButtonClick(View v) {
        Log.i(TAG, "onStopButtonClick called.");

        // プログレスバー更新処理を停止します。
        mHandler.removeCallbacks(mRunnable);

        // インジケータ・プログレスバーの両方を非表示にします。
        //setProgressBarVisibility(false);

        // インジケータを非表示にします。
        setProgressBarIndeterminateVisibility(false);

        // プログレスバーを非表示にします。
        setProgressBarIndeterminate(false);
        setProgress(0);

        // UIの操作可否を切り替える。
        enableStartLayout();
    }

    private void enableStartLayout() {
        // UIの操作可否を切り替える。（有効/無効切り替えの場合）
        // これだけだと子供Viewが有効/無効にならなかった。
//        mStopLayout.setEnabled(false);
//        mStartLayout.setEnabled(true);
        // 各Viewを有効/無効設定する。
        mStopButton.setEnabled(false);
        mStartButton.setEnabled(true);
        mIndeterminateRadioButton.setEnabled(true);
        mCountableRadioButton.setEnabled(true);

        // UIの操作可否を切り替える。（アニメーションの場合）
        mStopLayout.startAnimation(mHideAnim);
        mStartLayout.startAnimation(mShowAnim);

        mStartLayout.bringToFront();
    }

    private void enableStopLayout() {
        // UIの操作可否を切り替える。（有効/無効切り替えの場合）
        // これだけだと子供Viewが有効/無効にならなかった。
//        mStopLayout.setEnabled(true);
//        mStartLayout.setEnabled(false);
        // 各Viewを有効/無効設定する。
        mIndeterminateRadioButton.setEnabled(false);
        mCountableRadioButton.setEnabled(false);
        mStopButton.setEnabled(true);
        mStartButton.setEnabled(false);

        // UIの操作可否を切り替える。（アニメーションの場合）
        mStartLayout.startAnimation(mHideAnim);
        mStopLayout.startAnimation(mShowAnim);

        mStopLayout.bringToFront();
    }
}
