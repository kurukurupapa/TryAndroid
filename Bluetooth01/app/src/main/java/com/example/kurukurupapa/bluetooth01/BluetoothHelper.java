package com.example.kurukurupapa.bluetooth01;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.Toast;

/**
 * Bluetooth検出の操作を行います。
 */
public class BluetoothHelper {
    private MyActivity mMyActivity;
    private BluetoothAdapter mBluetoothAdapter;
    private boolean mReceiverFlag;

    public BluetoothHelper(MyActivity myActivity) {
        mMyActivity = myActivity;
    }

    public void start() {
        // BluetoothAdapterを取得
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(mMyActivity, "この端末は、Bluetoothをサポートしていないようです。", Toast.LENGTH_LONG).show();
            return;
        }

        // Bluetooth設定を確認します。
        if (!mBluetoothAdapter.isEnabled()) {
            // Bluetooth設定をONにするダイアログボックスを表示します。
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            mMyActivity.startActivityForResult(intent, MyActivity.REQUEST_CODE_BLUETOOTH_REQUEST_ENABLE);
            return;
        }

        // レシーバーを登録します。
        // 始めの1回目のみ。
        if (!mReceiverFlag) {
            mReceiverFlag = true;
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
            intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
            intentFilter.addAction(BluetoothDevice.ACTION_NAME_CHANGED);
            intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            mMyActivity.registerReceiver(new BluetoothReceiver(mMyActivity), intentFilter);
        }

        // 既に検出中なら停止する。
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
            Log.i(MyActivity.TAG, "既存の検出処理を中止しました。");
        }

        // 検出開始する。
        mBluetoothAdapter.startDiscovery();
    }
}
