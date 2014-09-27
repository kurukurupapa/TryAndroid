package com.example.kurukurupapa.cardui01;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * カードUIの練習です。
 *
 * 参考：
 * [Android]ListViewでアニメーション付きのカードUIを作る
 * http://uchidak.net/android-card-ui-with-animation
 *
 * 画像：
 * 商用無料の写真検索さん - フリーの画像素材が6700万点以上
 * http://www.nairegift.com/freephoto/
 * ※CCライセンス「表示」または「表示—改変禁止」を使用
 */
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // サンプルデータを作成します。
        List<CardItem> list = new ArrayList<CardItem>();
        for (int i = 0; i < 5; i++) {
            list.add(new CardItem(R.drawable.image_01_4719142040_s, "Cappuccino in ugly glass cup", "by insidious_plots"));
            list.add(new CardItem(R.drawable.image_02_3175586009_s, "coffee break", "by limaoscarjuliet"));
            list.add(new CardItem(R.drawable.image_03_775701003_s, "coffee @ filter", "by ercwttmn"));
            list.add(new CardItem(R.drawable.image_04_8866813913_s, "Coffee break. London", "by Link Humans UK"));
            list.add(new CardItem(R.drawable.image_05_2383092395_s, "Coffee Anyone?", "by Vicky Hugheston"));
        }

        // ListViewにサンプルデータを設定します。
        CardArrayAdapter adapter = new CardArrayAdapter(this, list);
        ListView listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(adapter);
    }

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.card_ui, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    */

}
