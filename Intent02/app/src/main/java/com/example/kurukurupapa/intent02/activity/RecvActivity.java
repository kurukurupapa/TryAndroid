package com.example.kurukurupapa.intent02.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kurukurupapa.intent02.R;
import com.example.kurukurupapa.intent02.model.IntentActivity;
import com.example.kurukurupapa.intent02.service.IntentActivityService;
import com.example.kurukurupapa.intent02.service.RecvService;

import java.util.List;

/**
 * インテント呼び出しを受信するアクティビティです。
 */
public class RecvActivity extends Activity {
    private static final String TAG = RecvActivity.class.getSimpleName();

    private RecvService mRecvService;
    private IntentActivityService mIntentActivityService;

    private RelativeLayout mRootLayout;
    private ImageView mSrcImageView;
    private TextView mSrcNameTextView;
    private Switch mIntentSwitch;
    private TextView mIntentValueTextView;
    private LinearLayout mRecvLinearLayout;
    private Animation mShowAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recv);

        // オブジェクト生成
        mRecvService = new RecvService(this);
        mIntentActivityService = new IntentActivityService(this);

        // UIオブジェクト取得
        mRootLayout = (RelativeLayout) findViewById(R.id.root_layout);
        mSrcImageView = (ImageView) findViewById(R.id.src_image_view);
        mSrcNameTextView = (TextView) findViewById(R.id.src_name_text_view);
        mIntentSwitch = (Switch) findViewById(R.id.intent_switch);
        mIntentValueTextView = (TextView) findViewById(R.id.intent_value_text_view);
        mRecvLinearLayout = (LinearLayout) findViewById(R.id.recv_linear_layout);
        mShowAnimation = AnimationUtils.loadAnimation(this, R.anim.show);

        mIntentSwitch.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                onIntentSwitchCheckedChanged(buttonView, isChecked);
            }
        });

        // インテント取得（初回分）
        mRecvService.setIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        // インテントを保持（2回目以降分）
        mRecvService.setIntent(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // インテントが変わっていたら内容を画面表示
        if (mRecvService.isIntentChanged()) {
            showSrc();
            showIntent();
            showActivity();
            mRootLayout.startAnimation(mShowAnimation);
            mRecvService.setIntentNoChanged();
        }
    }

    /**
     * インテント呼び出しのデータを表示
     */
    private void showSrc() {
        onIntentSwitchCheckedChanged(null, false);
        mIntentSwitch.setChecked(false);
    }

    /**
     * インテント呼び出し元アプリを表示
     */
    private void showIntent() {
        if (mRecvService.isValidSrcAppInfo()) {
            PackageManager pm = getPackageManager();
            mSrcImageView.setImageDrawable(mRecvService.getSrcAppIcon());
            mSrcNameTextView.setText(mRecvService.getSrcAppLabel());
        } else {
            mSrcImageView.setImageResource(R.drawable.ic_unknown);
            mSrcNameTextView.setText("<不明>");
            Toast.makeText(this, "呼び出し元アプリの取得に失敗しました。", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * インテント受信可能アクティビティを表示
     */
    private void showActivity() {
        // ListViewを初期化します。
        mRecvLinearLayout.removeAllViews();

        // アクティビティ一覧を取得します。
        List<IntentActivity> intentActivityList = mIntentActivityService.find(mRecvService.getSrcPackageName(), mRecvService.getIntent());

        // ListViewに、アクティビティ表示/起動用のViewを追加します。
        PackageManager pm = this.getPackageManager();
        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        for (IntentActivity intentActivity : intentActivityList) {
            View view = layoutInflater.inflate(R.layout.item_activity, null);
            ImageView iconImageView = (ImageView) view.findViewById(R.id.icon_image_view);
            TextView labelTextView = (TextView) view.findViewById(R.id.label_text_view);

            iconImageView.setImageDrawable(intentActivity.getResolveInfo().activityInfo.loadIcon(pm));
            labelTextView.setText(intentActivity.getResolveInfo().activityInfo.loadLabel(pm));

            view.setTag(intentActivity);
            view.setOnClickListener(new TextView.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // アクティビティを起動します。
                    IntentActivity intentActivity = (IntentActivity) v.getTag();
                    ResolveInfo info = intentActivity.getResolveInfo();
                    Intent intent = mRecvService.createIntent();
                    intent.setClassName(info.activityInfo.packageName, info.activityInfo.name);
                    startActivity(intent);
                    // 当アクティビティを終了しておきます。
                    // これにより、遷移先アクティビティでバックボタンクリック時に、当アクティビティを表示しなくなる。
                    finish();
                    // アクティビティ使用状況をDB登録します。
                    mIntentActivityService.updateOrInsert(intentActivity);
                }
            });
            mRecvLinearLayout.addView(view);
        }
    }

    /**
     * インテント表示スイッチクリック時の処理です。
     *
     * @param buttonView インテントスイッチ
     * @param isChecked チェック有無
     */
    private void onIntentSwitchCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            mIntentValueTextView.setText(mRecvService.getLongIntentStr());
            mIntentValueTextView.startAnimation(mShowAnimation);
        } else {
            mIntentValueTextView.setText(mRecvService.getShortIntentStr());
            mIntentValueTextView.startAnimation(mShowAnimation);
        }
    }

    /**
     * 送信ボタンクリック時の処理です。
     * @param v 送信ボタン
     */
    public void onSendButtonClick(View v) {
        startActivity(mRecvService.createIntent());
    }

}
