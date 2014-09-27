package com.example.kurukurupapa.bluetooth01;

import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.util.Log;

/**
 * ListViewに表示する1つの要素で、画面表示用のBluetoothデバイス情報を保持します。
 */
public class CardItem {
    private BluetoothDevice mBluetoothDevice;

    public CardItem(BluetoothDevice bluetoothDevice) {
        mBluetoothDevice = bluetoothDevice;
        Log.i(MyActivity.TAG, "name=[" + bluetoothDevice.getName() + "],address=[" + bluetoothDevice.getAddress() + "]");
    }

    public CharSequence getTitle() {
        return mBluetoothDevice.getName();
    }

    public CharSequence getMsg() {
        StringBuilder sb = new StringBuilder();

        // MACアドレス
        sb.append("address: " + mBluetoothDevice.getAddress() + "\n");

        // デバイスクラス
        // 電波強度が分かるらしい。
        // http://ja.wikipedia.org/wiki/Bluetooth#.E3.82.AF.E3.83.A9.E3.82.B9
        BluetoothClass bluetoothClass = mBluetoothDevice.getBluetoothClass();
        int majoreClass = mBluetoothDevice.getBluetoothClass().getMajorDeviceClass() >> 8;
        int minorClass = mBluetoothDevice.getBluetoothClass().getDeviceClass() & 0xFF;
        sb.append("majore class: " + majoreClass + "\n");
        sb.append("minor class: " + minorClass + "\n");

        // 接続状態
        CharSequence bondStateStr;
        switch (mBluetoothDevice.getBondState()) {
            case BluetoothDevice.BOND_BONDING:
                bondStateStr = "接続中";
                break;
            case BluetoothDevice.BOND_BONDED:
                bondStateStr = "接続履歴あり";
                break;
            case BluetoothDevice.BOND_NONE:
                bondStateStr = "新規デバイス";
                break;
            default:
                bondStateStr = "接続状態不明";
                break;
        }
        sb.append("bond state: " + bondStateStr + "\n");

        return sb.toString();
    }
}
