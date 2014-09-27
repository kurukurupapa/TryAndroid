package com.example.kurukurupapa.cardui01;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * カードUI用のアダプタークラスです。
 */
public class CardArrayAdapter extends ArrayAdapter<CardItem> {
    private final LayoutInflater mLayoutInflater;
    private int mPosition;

    public CardArrayAdapter(Context context, List<CardItem> objects) {
        super(context, 0, objects);
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
        ImageView imageView = (ImageView) convertView.findViewById(R.id.image_view);
        TextView titleView = (TextView) convertView.findViewById(R.id.title_view);
        TextView authorView = (TextView) convertView.findViewById(R.id.author_view);
        imageView.setImageResource(cardItem.getImageResource());
        titleView.setText(cardItem.getTitle());
        authorView.setText(cardItem.getAuthor());

        // アニメーション開始
        // ※下にスクロールする場合だけアニメーションを表示します。
        if (mPosition < position) {
            convertView.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.card_animation));
            mPosition = position;
        } else {
            mPosition = position;
        }

        return convertView;
    }

}
