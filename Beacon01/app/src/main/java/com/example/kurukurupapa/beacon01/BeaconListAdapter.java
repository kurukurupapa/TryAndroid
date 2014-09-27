package com.example.kurukurupapa.beacon01;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class BeaconListAdapter extends ArrayAdapter<BeaconItem> {
    public BeaconListAdapter(Context context) {
        super(context, 0, new BeaconItem[]{});
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return super.getView(position, convertView, parent);
    }
}
