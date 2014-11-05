package com.example.kurukurupapa.oauth03.browserpintwitter10a;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.example.kurukurupapa.oauth03.R;

import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.TwitterApi;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

public class BrowserOAuthHelper {
    private static final String TAG = BrowserOAuthHelper.class.getSimpleName();

    private final String apiKey;
    private final String apiSecret;
    private final Context mContext;
    private final Runnable mOkRunnable;

    private OAuthService mService;
    private Token mRequestToken;
    private Token mAccessToken;
    private String mOAuthPin;
    private String mBody;

    public BrowserOAuthHelper(Context context, Runnable okRunnable) {
        this.mContext = context;
        this.mOkRunnable = okRunnable;
        this.apiKey = context.getString(R.string.twitter_api_key);
        this.apiSecret = context.getString(R.string.twitter_api_secret);
    }

    public void start() {
        Log.v(TAG, "start called");

        // １．OAuthService オブジェクトを生成
        createService();

        // ２．リクエスト・トークン（request token）を取得
        if (!getRequestToken()) {
            return;
        }

        // ３．ユーザに認証してもらう
        if (!auth()) {
            return;
        }

        // ４．アクセストークン（access Token）を取得
        if (!getAccessToken()) {
            return;
        }

        // ５．リクエスト送信
        request();
    }

    public void clear() {
        mRequestToken = null;
        mOAuthPin = null;
        mAccessToken = null;
        mBody = null;
    }

    /**
     * OAuthService オブジェクトを生成
     *
     * Consumer Key (API Key), Consumer Secret (API Secret)
     * 事前に、Twitter（https://dev.twitter.com/）で、当アプリを登録し、入手しておきました。
     *
     * コールバックURL
     * 設定しない場合は、OOB（Out of Band：帯域外） OAuthを使用するらしいです。
     * 設定する場合は、Twitter（https://dev.twitter.com/）に登録した当アプリ設定で、Callback URLに、URLを設定する必要がありました。
     * ただし、Twitterに登録したURLと、下記のcallbackメソッドで設定するURLは、違っていてもエラーになりませんでした。
     * 下記callbackメソッドで、当アプリ内のHTMLを指定すると、Twitter認証後の画面遷移でエラー発生しました。
     *
     * providerメソッドにTwitterApi.classを渡して実行すると、次のエラーが発生しました。
     * 「org.scribe.exceptions.OAuthException: Response body is incorrect. Can't extract token and secret from this: 'SSL is required'」
     * そのため、TwitterApi.SSL.classを渡すようにしました。
     * 参照：
     * 【Twitter】Twitter 仕様変更(2014-01-15)で、OAuth認証（scribe-java）が動かなくなってしまった。【Scala(Java)】 - Qiita
     * http://qiita.com/takudo/items/dd63cf98012c56a914ed
     */
    private void createService() {
        Log.v(TAG, "createService called");
        if (mService != null) {
            return;
        }

        mService = new ServiceBuilder()
                .provider(TwitterApi.SSL.class)    //Twitterの場合
                .apiKey(apiKey)
                .apiSecret(apiSecret)
                .build();
        Log.v(TAG, "OAuthServiceオブジェクトを生成しました。");
    }

    /**
     * リクエスト・トークン（request token）を取得
     */
    private boolean getRequestToken() {
        Log.v(TAG, "getRequestToken called");
        if (mRequestToken != null) {
            return true;
        }

        AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                // HTTP通信を行うため、非UIスレッドで実行します。
                mRequestToken = mService.getRequestToken();
                return true;
            }
            @Override
            protected void onPostExecute(Boolean aBoolean) {
                start();
            }
        };
        task.execute();
        return false;
    }

    /**
     * ユーザに認証してもらう
     *
     * PINを使用します。
     * ※コールバックURLは使用しません。
     *
     * OAuthの認証にWebViewを使うのはやめよう - Shogo's Blog
     * http://shogo82148.github.io/blog/2012/11/24/no-more-webview/
     */
    private boolean auth() {
        Log.v(TAG, "auth called");
        if (mOAuthPin != null && mOAuthPin.length() > 0) {
            return true;
        }

        // Twitterの認証ページURLを取得します。
        final String authUrl = mService.getAuthorizationUrl(mRequestToken);
        Log.v(TAG, "authUrl=" + authUrl);

        // ブラウザに、Twitterの連携アプリ認証画面を表示します。
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(authUrl));
        mContext.startActivity(intent);

        return false;
    }

    public void setOAuthPin(String oauthPin) {
        mOAuthPin = oauthPin;
        Log.v(TAG, "mOAuthPin=" + mOAuthPin);
    }

    /**
     * アクセストークン（access Token）を取得
     */
    private boolean getAccessToken() {
        Log.v(TAG, "getAccessToken called");
        if (mAccessToken != null) {
            return true;
        }

        AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                // HTTP通信を行うため、非UIスレッドで実行します。
                Verifier verifier = new Verifier(mOAuthPin);
                mAccessToken = mService.getAccessToken(mRequestToken, verifier); // the requestToken you had from step 2
                return true;
            }
            @Override
            protected void onPostExecute(Boolean aBoolean) {
                start();
            }
        };
        task.execute();
        return false;
    }

    /**
     * リクエスト送信
     */
    private void request() {
        AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                // HTTP通信を行うため、非UIスレッドで実行します。
                //OAuthRequest request = new OAuthRequest(Verb.GET, "https://api.twitter.com/1/account/verify_credentials.xml");
                OAuthRequest request = new OAuthRequest(Verb.GET, "https://api.twitter.com/1.1/account/verify_credentials.json");
                mService.signRequest(mAccessToken, request); // the access token from step 4
                Response response = request.send();
                mBody = response.getBody();
                Log.v(TAG, "mBody=" + mBody);
                return true;
            }
            @Override
            protected void onPostExecute(Boolean result) {
                Log.v(TAG, "onPostExecute called");
                if (result) {
                    mOkRunnable.run();
                }
            }
        };
        task.execute();
    }

    public String getResult() {
        return mBody;
    }
}
