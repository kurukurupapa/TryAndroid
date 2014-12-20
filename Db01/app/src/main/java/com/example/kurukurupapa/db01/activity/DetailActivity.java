package com.example.kurukurupapa.db01.activity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kurukurupapa.db01.service.DbHelper;
import com.example.kurukurupapa.db01.entity.Entity;
import com.example.kurukurupapa.db01.service.QueryService;
import com.example.kurukurupapa.db01.R;
import com.example.kurukurupapa.db01.service.UpdateService;

public class DetailActivity extends ActionBarActivity {
    private static final String TAG = DetailActivity.class.getSimpleName();
    public static final String KEY_ID = "id";

    private QueryService mQueryService;
    private UpdateService mUpdateService;
    private Entity mEntity;

    private TextView mIdTextView;
    private EditText mNameEditText;
    private EditText mTextEditText;
    private EditText mNumberEditText;
    private TextView mTimestampTextView;
    private Button mDeleteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // オブジェクト生成
        mQueryService = new QueryService();
        mUpdateService = new UpdateService();

        // UIオブジェクトを取得
        mIdTextView = (TextView) findViewById(R.id.id_text_view);
        mNameEditText = (EditText) findViewById(R.id.name_edit_text);
        mTextEditText = (EditText) findViewById(R.id.text_edit_text);
        mNumberEditText = (EditText) findViewById(R.id.number_edit_text);
        mTimestampTextView = (TextView) findViewById(R.id.timestamp_text_view);
        mDeleteButton = (Button) findViewById(R.id.delete_button);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // 呼び元アクティビティからのパラメータを取得
        Intent intent = getIntent();
        long id = intent.getLongExtra(KEY_ID, Entity.NO_ID);
        Log.d(TAG, "パラメータ id=" + id);

        // エンティティ取得
        DbHelper dbHelper = new DbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        try {
            mEntity = mQueryService.queryOrCreate(db, id);
        }
        catch (Exception e) {
            Log.e(TAG, "データ読み込みに失敗しました。", e);
            Toast.makeText(this, "データ読み込みに失敗しました。", Toast.LENGTH_LONG).show();
            mEntity = new Entity();
        }
        finally {
            db.close();
        }

        // エンティティ表示
        showEntity();
    }

    private void showEntity() {
        mIdTextView.setText(mEntity.getIdString());
        mNameEditText.setText(mEntity.getName());
        mTextEditText.setText(mEntity.getText());
        mNumberEditText.setText(mEntity.getNumberString());
        mTimestampTextView.setText(mEntity.getTimestampString(this));

        mDeleteButton.setEnabled(!mEntity.isNew());
    }

    private void takeEntity() {
        mEntity.setName(mNameEditText.getText().toString());
        mEntity.setText(mTextEditText.getText().toString());
        mEntity.setNumber(mNumberEditText.getText().toString());
    }

    public void onSaveButtonClick(View v) {
        // ユーザ入力を取得
        takeEntity();

        // エンティティ更新
        DbHelper dbHelper = new DbHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            mUpdateService.insertOrUpdate(db, mEntity);
        }
        catch (Exception e) {
            Log.e(TAG, "データ更新に失敗しました。", e);
            Toast.makeText(this, "データ更新に失敗しました。", Toast.LENGTH_LONG).show();
            mEntity = new Entity();
        }
        finally {
            db.close();
        }

        // 前画面へ戻る
        finish();
    }

    public void onDeleteButtonClick(View v) {
        // エンティティ削除
        DbHelper dbHelper = new DbHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            mUpdateService.delete(db, mEntity);
        }
        catch (Exception e) {
            Log.e(TAG, "データ削除に失敗しました。", e);
            Toast.makeText(this, "データ削除に失敗しました。", Toast.LENGTH_LONG).show();
            mEntity = new Entity();
        }
        finally {
            db.close();
        }

        // 前画面へ戻る
        finish();
    }

    public void onCancelButtonClick(View v) {
        // 前画面へ戻る
        finish();
    }
}
