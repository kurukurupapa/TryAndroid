package com.example.kurukurupapa.bluetooth01;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Bluetoothの検出を行う練習です。
 * ※検出のみです。接続はしません。
 *
 * 参考：
 * Bluetoothで通信を行う(1) « Tech Booster
 * http://techbooster.jpn.org/andriod/application/5191/
 *
 * Bluetoothで通信を行う(2) « Tech Booster
 * http://techbooster.jpn.org/andriod/device/5535/
 *
 * リソース：
 * Bluetooth icon | Icon Search Engine | Iconfinder
 * https://www.iconfinder.com/icons/100241/bluetooth_icon#size=128
 */
public class MyActivity extends Activity  {
    /** ログ出力のタグ */
    public static final String TAG = MyActivity.class.getName();
    /** Bluetooth設定ダイアログボックスのリクエストコード */
    public static final int REQUEST_CODE_BLUETOOTH_REQUEST_ENABLE = 1;

    private ListView mListView;
    private ArrayAdapter mCardArrayAdapter;
    private List<CardItem> mCardItemList;
    private BluetoothHelper mBluetoothHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // タイトルバーへ表示するインジケータの準備です。
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_my);

        // ListViewの準備です。
        mCardItemList = new ArrayList<CardItem>();
        mCardArrayAdapter = new CardArrayAdapter(this, mCardItemList);
        mListView = (ListView) findViewById(R.id.list_view);
        mListView.setAdapter(mCardArrayAdapter);

        // Bluetoothデバイス検出処理のオブジェクト生成です。
        mBluetoothHelper = new BluetoothHelper(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 検出ボタンクリック時の処理です。
     * @param v 検出ボタン
     */
    public void onDetectionButtonClick(View v) {
        Log.i(TAG, "onDetectionButtonClick called");
        mBluetoothHelper.start();
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
            case REQUEST_CODE_BLUETOOTH_REQUEST_ENABLE:
                // Bluetooth設定がONに操作された場合は、検出処理を行う。
                if (resultCode == Activity.RESULT_OK) {
                    mBluetoothHelper.start();
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void clearListView() {
        mCardArrayAdapter.clear();
    }

    public void addCardItem(CardItem cardItem) {
        mCardArrayAdapter.add(cardItem);
    }

}
