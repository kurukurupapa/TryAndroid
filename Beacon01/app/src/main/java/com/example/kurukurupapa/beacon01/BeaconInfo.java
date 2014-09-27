package com.example.kurukurupapa.beacon01;

import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;

/**
 * Beaconの情報を扱うクラスです。
 */
public class BeaconInfo {
    private BluetoothDevice mBluetoothDevice;
    private int mRssi;
    private byte[] mScanRecord;
    private StringBuilder sb;

    public BeaconInfo(BluetoothDevice bluetoothDevice, int rssi, byte[] mScanRecord) {
        this.mBluetoothDevice = bluetoothDevice;
        this.mRssi = rssi;
        this.mScanRecord = mScanRecord;
        parse();
    }

    public void parse() {
        sb = new StringBuilder();

        //
        // Bluetoothデバイスのデータを取得します。
        //

        // 名前
        sb.append("name: " + mBluetoothDevice.getName() + "\n");

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
        String bondStateStr;
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

        //
        // 電波強度を取得します。
        //
        sb.append("rssi: " + mRssi + "\n");

        //
        // Beacon特有のデータを取得します。
        //
        String uuid = null;
        String major = null;
        String minor = null;
        if(mScanRecord.length > 30)
        {
            if((mScanRecord[5] == (byte)0x4c) && (mScanRecord[6] == (byte)0x00) &&
                    (mScanRecord[7] == (byte)0x02) && (mScanRecord[8] == (byte)0x15))
            {
                uuid = IntToHex2(mScanRecord[9] & 0xff)
                        + IntToHex2(mScanRecord[10] & 0xff)
                        + IntToHex2(mScanRecord[11] & 0xff)
                        + IntToHex2(mScanRecord[12] & 0xff)
                        + "-"
                        + IntToHex2(mScanRecord[13] & 0xff)
                        + IntToHex2(mScanRecord[14] & 0xff)
                        + "-"
                        + IntToHex2(mScanRecord[15] & 0xff)
                        + IntToHex2(mScanRecord[16] & 0xff)
                        + "-"
                        + IntToHex2(mScanRecord[17] & 0xff)
                        + IntToHex2(mScanRecord[18] & 0xff)
                        + "-"
                        + IntToHex2(mScanRecord[19] & 0xff)
                        + IntToHex2(mScanRecord[20] & 0xff)
                        + IntToHex2(mScanRecord[21] & 0xff)
                        + IntToHex2(mScanRecord[22] & 0xff)
                        + IntToHex2(mScanRecord[23] & 0xff)
                        + IntToHex2(mScanRecord[24] & 0xff);

                major = IntToHex2(mScanRecord[25] & 0xff) + IntToHex2(mScanRecord[26] & 0xff);
                minor = IntToHex2(mScanRecord[27] & 0xff) + IntToHex2(mScanRecord[28] & 0xff);
            }
        }
        if (uuid != null) {
            sb.append("beacon uuid: " + uuid + "\n");
            sb.append("beacon major: " + major + "\n");
            sb.append("beacon minor: " + minor + "\n");
        } else {
            sb.append("beacon: Beaconではないようです\n");
        }
    }

    /**
     * intデータを 2桁16進数に変換するメソッド
     */
    private String IntToHex2(int i) {
        char hex_2[] = {Character.forDigit((i>>4) & 0x0f,16),Character.forDigit(i&0x0f, 16)};
        String hex_2_str = new String(hex_2);
        return hex_2_str.toUpperCase();
    }

    public String getText() {
        return sb.toString();
    }
}
