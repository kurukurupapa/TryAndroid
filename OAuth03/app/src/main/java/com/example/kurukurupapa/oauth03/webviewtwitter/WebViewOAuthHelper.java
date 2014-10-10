package com.example.kurukurupapa.oauth03.webviewtwitter;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.example.kurukurupapa.oauth03.R;

import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.TwitterApi;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

public class WebViewOAuthHelper {
    private static final String TAG = WebViewOAuthHelper.class.getSimpleName();

    private final String apiKey;
    private final String apiSecret;
    private final String callbackUrl;
    private final Context mContext;
    private final WebView mWebView;
    private final Runnable mOkRunnable;

    private OAuthService mService;
    private Token mRequestToken;
    private Token mAccessToken;
    private String mOAuthVerifier;
    private String mBody;

    public WebViewOAuthHelper(Context context, WebView webView, Runnable okRunnable) {
        this.mContext = context;
        this.mWebView = webView;
        this.mOkRunnable = okRunnable;
        this.apiKey = context.getString(R.string.twitter_api_key);
        this.apiSecret = context.getString(R.string.twitter_api_secret);
        this.callbackUrl = context.getString(R.string.twitter_callback_url);
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
        mOAuthVerifier = null;
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
                .callback(callbackUrl)
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
     * WebViewを使用します。
     */
    private boolean auth() {
        Log.v(TAG, "auth called");
        if (mOAuthVerifier != null) {
            return true;
        }

        // Twitterの認証ページURLを取得します。
        final String authUrl = mService.getAuthorizationUrl(mRequestToken);
        Log.v(TAG, "authUrl=" + authUrl);

        // コールバックURLを使用する場合は、
        // Twitterの認証成功後、コールバックURLの遷移し、
        // GETパラメータとして、oauth_verifierが取得できます。

        // WebViewを使った認証
        // ただし、セキュリティ的に推奨されない。
        // WebViewはUIスレッドで操作する必要がある。
        // 参考：
        // Twitter4jを使ってOAuth認証をアプリ内で行う方法 - 素人のアンドロイドアプリ開発日記
        // http://andante.in/i/android%E3%82%A2%E3%83%97%E3%83%AAtips/twitter4j%E3%82%92%E4%BD%BF%E3%81%A3%E3%81%A6oauth%E8%AA%8D%E8%A8%BC%E3%82%92%E3%82%A2%E3%83%97%E3%83%AA%E5%86%85%E3%81%A7%E8%A1%8C%E3%81%86%E6%96%B9%E6%B3%95/

        // JavaScript有効化
        //mWebView.getSettings().setJavaScriptEnabled(true);
        // ボタン/リンク操作で標準ブラウザを起動させない
        mWebView.setWebViewClient(new WebViewClient(){
            /**
             * ページ描画完了時の処理です。
             */
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.v(TAG, "url=" + url);

                if (url != null && url.startsWith(callbackUrl)) {
                    setOAuthVerifier(Uri.parse(url));
                }
            }
        });
        // WebViewに、Twitterの連携アプリ認証画面を表示します。
        mWebView.loadUrl(authUrl);
        return false;
    }

    public void setOAuthVerifier(Uri uri) {
        Log.v(TAG, "setOAuthVerifier called");
        String oauthToken = uri.getQueryParameter("oauth_token");
        String oauthVerifier = uri.getQueryParameter("oauth_verifier");

        // トークンをチェックします。
        if (oauthToken == null || !oauthToken.equals(mRequestToken.getToken())) {
            // 処理を中止します。
            Log.d(TAG, "トークンエラーです。oauthToken=" + oauthToken + ",mRequestToken.getToken()=" + mRequestToken.getToken());
            Toast.makeText(mContext, "エラーが発生しました。", Toast.LENGTH_LONG).show();
            clear();
            return;
        }

        mOAuthVerifier = oauthVerifier;
        Log.d(TAG, "mOAuthVerifier=" + mOAuthVerifier);
        start();
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
                Verifier verifier = new Verifier(mOAuthVerifier);
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
