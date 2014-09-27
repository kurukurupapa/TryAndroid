package com.example.kurukurupapa.bluetooth01;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * ListViewのデータを操作します。
 */
public class CardArrayAdapter extends ArrayAdapter<CardItem> {
    private final LayoutInflater mLayoutInflater;
    private int mPosition;

    public CardArrayAdapter(Context context, List<CardItem> cardItemList) {
        super(context, 0, cardItemList);
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mPosition = -1;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Viewが未作成の場合、新規に作成します。
        // 作成済みの場合は、そのまま再利用します。
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.card_item, null);
        }

        // 当該行のデータを取得します。
        CardItem cardItem = getItem(position);

        // Viewオブジェクトを設定します。
        TextView titleTextView = (TextView) convertView.findViewById(R.id.title_text_view);
        TextView msgTextView = (TextView) convertView.findViewById(R.id.msg_text_view);
        titleTextView.setText(cardItem.getTitle());
        msgTextView.setText(cardItem.getMsg());

        // アニメーション開始
        // ※下にスクロールする場合だけアニメーションを表示します。
        if (mPosition < position) {
            convertView.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.card_anim));
            mPosition = position;
        } else {
            mPosition = position;
        }

        return convertView;
    }
}
