package com.example.kurukurupapa.navigationdrawer01;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kurukurupapa.navigationdrawer01.blankfragment.BlankFragment;
import com.example.kurukurupapa.navigationdrawer01.itemflagment.ItemFragment;
import com.example.kurukurupapa.navigationdrawer01.plusonefragment.PlusOneFragment;

/**
 * Navigation Drawer Activityを使う練習アプリです。
 * 次のことを試しています。
 *
 * AndroidStudio 1.0 で、「Navigation Drawer Activity」を作成してみる。
 *
 * Navigation Drawerに、選択項目を追加する（新規フラグメントを追加する）。
 * →NavigationDrawerItemsクラスを新規作成して、全ての選択項目を保持させる。
 * →既存のNavigationDrawerFragment#onCreateViewメソッドにて、NavigationDrawerItemsクラスを参照する。
 *
 * Navigation Drawerで、各セクション選択時に、それぞれ異なるレイアウトの画面（Fragment）を表示する。
 * →既存のMainActivity#onNavigationDrawerItemSelectedメソッドにて、新規フラグメントの生成処理を追加する。
 *
 * アクションバーにアイコン付きメニューを表示する。
 * →AndroidManifest.xmlで、applicationタグまたはactivityタグのandroid:theme属性に、@style/Theme.AppCompat.XXXのテーマを設定する。
 * 　（例）android:theme="@style/Theme.AppCompat.Light"
 * →メニューのXMLで、ネームスペースに「xmlns:app="http://schemas.android.com/apk/res-auto"」を追加し、itemタグにapp:showAsAction属性、android:icon属性を追加する。
 * 　（例）app:showAsAction="ifRoom" android:icon="@android:drawable/ic_menu_preferences"
 * 参考：
 * Androidのアクションバーで使える小技7選 [Androidアプリのプログラミング] All About
 * http://allabout.co.jp/gm/gc/439463/
 *
 * アクションバーに検索バーを表示する。
 * →メニューのXMLで、itemタグのapp:showAsAction属性に「collapseActionView」を追加し、app:actionViewClass="android.support.v7.widget.SearchView"属性を追加する。
 * →アクティビティのonCreateOptionsMenuメソッドで、検索バーにリスナーを登録する。
 * 参考：
 * 2/3 Androidのアクションバーで使える小技7選 [Androidアプリのプログラミング] All About
 * http://allabout.co.jp/gm/gc/439463/2/
 * ケーワン・エンタープライズのエンジニアメモ(｀･ω･´)ゞﾋﾞｼｯ!!: ActionBarを使ってみる（SearchViewを配置する Android 2.2以上）
 * http://k-1-ne-jp.blogspot.jp/2013/12/actionbarsearchview-android-22.html
 *
 * アクションバーにロゴやアイコンを表示する。
 * 参考：
 * ActionBarのロゴの表示／非表示を切り替える。 - Android/iPhoneたわむれ日々
 * http://sharakova.hatenablog.com/entry/2014/06/28/171248
 * Personal search engines : Logo maker Search engine maker maker matrix_revolutions googletestad rune fonts perm style
 * http://www.funnylogo.info/
 * Generate Your Own Twitter Logo | Twitlogo
 * http://www.twitlogo.com/
 *
 * 設定画面を表示する。
 */
public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks,
        BlankFragment.OnFragmentInteractionListener,
        ItemFragment.OnFragmentInteractionListener,
        PlusOneFragment.OnFragmentInteractionListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate called");
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        Log.d(TAG, "onNavigationDrawerItemSelected called");

        // ナビゲーションドロワーで選択された項目に対応するFragmentを作成します。
        Fragment fragment = null;
        switch (position) {
            case NavigationDrawerItems.POSITION_SECTION1:
            case NavigationDrawerItems.POSITION_SECTION2:
            case NavigationDrawerItems.POSITION_SECTION3:
                fragment = PlaceholderFragment.newInstance(position + 1);
                break;
            case NavigationDrawerItems.POSITION_BLANK_FRAGMENT:
                fragment = BlankFragment.newInstance(null, null);
                mTitle = getString(R.string.title_blank_fragment);
                break;
            case NavigationDrawerItems.POSITION_ITEM_FRAGMENT:
                fragment = ItemFragment.newInstance(null, null);
                mTitle = getString(R.string.title_item_fragment);
                break;
            case NavigationDrawerItems.POSITION_PLUS_ONE_FRAGMENT:
                fragment = PlusOneFragment.newInstance(null, null);
                mTitle = getString(R.string.title_plus_one_fragment);
                break;
            case NavigationDrawerItems.POSITION_PREFERENCE_FRAGMENT:
                // support library では、PreferenceFragmentが使えないので、PreferenceActivityを使用します。
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
        }

        if (fragment == null) {
            return;
        }

        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        //actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        //actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);

        // アクションバーのタイトル設定
        ActionBarHelper.setDisplay(this, actionBar);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu called");

        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();

            // 検索バーにリスナーを登録します。
            MenuItem searchItem = menu.findItem(R.id.action_search);
            SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    Toast.makeText(getBaseContext(), s, Toast.LENGTH_LONG).show();
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    return false;
                }
            });

            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected called");

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * BlankFragmentオブジェクトにてUI操作された場合に呼ばれるメソッドですが、
     * 現状では、ユーザ操作を受け取るUIを用意していないため、動作することはありません。
     * @param uri
     */
    @Override
    public void onBlankFragmentInteraction(Uri uri) {
        Log.d(TAG, "onBlankFragmentInteraction called");
        // ダミーです。
    }

    /**
     * ItemFragmentオブジェクトにてアイテム選択された場合に呼ばれるメソッドです。
     * @param id
     */
    @Override
    public void onItemFragmentInteraction(String id) {
        Log.d(TAG, "onItemFragmentInteraction called");
        Toast.makeText(this, "項目が選択されました。id=" + id, Toast.LENGTH_LONG).show();
    }

    /**
     * PlusOneFragmentオブジェクトにてボタンクリック時に呼ばれるメソッドです。
     * @param uri
     */
    @Override
    public void onPlusOneFragmentInteraction(Uri uri) {
        Log.d(TAG, "onPlusOneFragmentInteraction called");
        Toast.makeText(this, "ボタンクリックされました。uri=" + uri, Toast.LENGTH_LONG).show();
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(ARG_SECTION_NUMBER + "=" + getArguments().getInt(ARG_SECTION_NUMBER));

            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

}
