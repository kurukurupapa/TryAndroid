package com.example.kurukurupapa.navigationdrawer01;

import android.content.Context;

/**
 * Navigation Drawerに表示する項目を保持するクラスです。
 */
public class NavigationDrawerItems {
    public static final int POSITION_SECTION1 = 0;
    public static final int POSITION_SECTION2 = 1;
    public static final int POSITION_SECTION3 = 2;
    public static final int POSITION_BLANK_FRAGMENT = 3;
    public static final int POSITION_ITEM_FRAGMENT = 4;
    public static final int POSITION_PLUS_ONE_FRAGMENT = 5;
    public static final int POSITION_PREFERENCE_FRAGMENT = 6;

    private static final int[] TITLE_RES_ID_ARR = new int[] {
            R.string.title_section1,
            R.string.title_section2,
            R.string.title_section3,
            R.string.title_blank_fragment,
            R.string.title_item_fragment,
            R.string.title_plus_one_fragment,
            R.string.title_settings,
    };

    private String[] mTitleArray;

    public NavigationDrawerItems(Context context) {
        mTitleArray = new String[TITLE_RES_ID_ARR.length];
        for (int i = 0; i < TITLE_RES_ID_ARR.length; i++) {
            mTitleArray[i] = context.getString(TITLE_RES_ID_ARR[i]);
        }
    }

    public String[] getTitleArray() {
        return mTitleArray;
    }

}
