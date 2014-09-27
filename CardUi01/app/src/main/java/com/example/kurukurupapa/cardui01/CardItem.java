package com.example.kurukurupapa.cardui01;

/**
 * カードのデータを保持するクラスです。
 */
public class CardItem {
    private int mImageResource;
    private CharSequence mTitle;
    private CharSequence mAuthor;

    public CardItem(int resId, CharSequence title, CharSequence author) {
        mImageResource = resId;
        mTitle = title;
        mAuthor = author;
    }

    public int getImageResource() {
        return mImageResource;
    }

    public CharSequence getTitle() {
        return mTitle;
    }

    public CharSequence getAuthor() {
        return mAuthor;
    }

}
