package com.example.kurukurupapa.bluetooth01;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Bluetooth検出関連のレシーバーです。
 */
public class BluetoothReceiver extends BroadcastReceiver {
    private MyActivity mMyActivity;
    private List<String> mAddressList;

    public BluetoothReceiver(MyActivity myActivity) {
        mMyActivity = myActivity;
        mAddressList = new ArrayList<String>();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.i(MyActivity.TAG, "onReceive called. action=" + action);

        if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_STARTED)) {
            mAddressList.clear();
            mMyActivity.clearListView();
            mMyActivity.setProgressBarIndeterminateVisibility(true);
            Toast.makeText(mMyActivity, "Bluetoothデバイスの検出を開始しました。", Toast.LENGTH_LONG).show();

        } else if (action.equals(BluetoothDevice.ACTION_FOUND) || action.equals(BluetoothDevice.ACTION_NAME_CHANGED)) {
            BluetoothDevice bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            // 同じデバイスが何度か検出されることがありました。
            // 検出1回目のみ処理を行います。
            String address = bluetoothDevice.getAddress();
            if (!mAddressList.contains(address)) {
                mAddressList.add(address);
                mMyActivity.addCardItem(new CardItem(bluetoothDevice));
            }

        } else if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
            mMyActivity.setProgressBarIndeterminateVisibility(false);
            Toast.makeText(mMyActivity, "Bluetoothデバイスの検出を終了しました。", Toast.LENGTH_LONG).show();
        }
    }
}
