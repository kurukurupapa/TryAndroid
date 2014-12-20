package com.example.kurukurupapa.db01.activity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.kurukurupapa.db01.service.DbHelper;
import com.example.kurukurupapa.db01.service.ListService;
import com.example.kurukurupapa.db01.R;

import java.util.ArrayList;

/**
 * データベース操作を行うアプリです。
 * ※コンテンツプロバイダ未使用です。
 */
public class MainActivity extends ActionBarActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private ListView mListView;
    private ListService mListService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mListView = (ListView) findViewById(R.id.list_view);
        mListService = new ListService();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // データを取得
        DbHelper dbHelper = new DbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        ArrayList<String> list;
        try {
            mListService.run(db);
            list = mListService.getStringList();
            list.add("(データ追加)");
        }
        catch (Exception e) {
            Log.e(TAG, "データ検索に失敗しました。", e);
            Toast.makeText(this, "データ検索に失敗しました。", Toast.LENGTH_LONG).show();
            list = new ArrayList<String>();
        }
        finally {
            db.close();
        }

        // ListViewにデータを設定
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onListViewItemClick(parent, view, position, id);
            }
        });
    }

    private void onListViewItemClick(AdapterView<?> parent, View view, int position, long id) {
        long recordId = mListService.getId(position);
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(DetailActivity.KEY_ID, recordId);
        startActivity(intent);
    }
}
