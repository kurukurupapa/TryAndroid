package com.example.kurukurupapa.beacon01;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

/**
 * Beaconの検索を行います。
 */
public class BeaconHelper {
    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10 * 1000;

    /**
     * 操作元アクティビティ
     * UIは、このアクティビティで管理します。
     */
    private MyActivity mMyActivity;

    /** Bluetooth操作に必要となるBluetoothAdapter */
    private BluetoothAdapter mBluetoothAdapter;
    /** Bluetoothデバイス発見時のコールバック処理 */
    private BluetoothAdapter.LeScanCallback mLeScanCallback;
    /** Bluetooth検索状態 */
    private boolean mScanning;
    /** 結果テキスト */
    private StringBuilder mResultText;

    public BeaconHelper(MyActivity myActivity) {
        this.mMyActivity = myActivity;
    }

    /**
     * Bluetoothデバイスの検索を開始します。
     */
    public void startScan() {
        mResultText = new StringBuilder();

        // BluetoothAdapterの取得
        if (mBluetoothAdapter == null) {
            BluetoothManager bManager = (BluetoothManager) mMyActivity.getSystemService(Context.BLUETOOTH_SERVICE);
            mBluetoothAdapter = bManager.getAdapter();
        }

        // Bluetoothが無効化されていたら、Bluetooth有効化のダイアログボックスを表示します。
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Log.i(mMyActivity.TAG, "BluetoothAdapterが取得でない、または無効化されています。");
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            mMyActivity.startActivityForResult(enableBtIntent, mMyActivity.REQUEST_CODE_REQUEST_ENABLE_BT);
            return;
        }
        Log.i(mMyActivity.TAG, "Bluetooth ONになりました。");

        // Bluetoothデバイス発見時のコールバック処理を定義します。
        if (mLeScanCallback == null) {
            mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(BluetoothDevice bluetoothDevice, int rssi, byte[] scanRecord) {
                    Log.i(mMyActivity.TAG, "onLeScan called");
                    // デバイスが検索されるたびに呼び出される
                    // bluetoothDevice - デバイス情報
                    // rssi - 電波強度
                    // scanRecord - アドバタイジングパケット

                    final BeaconInfo beaconInfo = new BeaconInfo(bluetoothDevice, rssi, scanRecord);
                    Log.i(mMyActivity.TAG, "onLeScan: bluetoothInfo=" + beaconInfo.toString());

                    mMyActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (mResultText.length() > 0) {
                                mResultText.append("--\n");
                            }
                            mResultText.append(beaconInfo.getText());
                            mMyActivity.setText(mResultText.toString());
                        }
                    });
                }
            };
        }

        // Bluetoothデバイスの検索終了処理を登録します。
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.i(mMyActivity.TAG, "run called");
                stopScan();
            }
        }, SCAN_PERIOD);

        // Bluetoothデバイスの検索開始
        mScanning = true;
        mBluetoothAdapter.startLeScan(mLeScanCallback);
        mMyActivity.showScanStart();
    }

    public void stopScan() {
        mScanning = false;
        mBluetoothAdapter.stopLeScan(mLeScanCallback);
        mMyActivity.showScanStop();
    }
}
