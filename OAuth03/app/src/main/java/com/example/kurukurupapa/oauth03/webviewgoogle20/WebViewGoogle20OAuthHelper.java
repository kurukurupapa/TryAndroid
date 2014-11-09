package com.example.kurukurupapa.oauth03.webviewgoogle20;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.kurukurupapa.oauth03.R;
import com.example.kurukurupapa.oauth03.service.GoogleOAuthUserInfo;
import com.example.kurukurupapa.oauth03.service.GooglePlusAccount;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.Google2Api;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

public class WebViewGoogle20OAuthHelper {
    private static final String TAG = WebViewGoogle20OAuthHelper.class.getSimpleName();

    private final String mApiKey;
    private final String mApiSecret;
    private final String mCallbackUrl;
    private final Context mContext;
    private final WebView mWebView;
    private final Runnable mOkRunnable;

    private OAuthService mService;
    private Token mAccessToken;
    private String mOAuthVerifier;
    private String mJson;

    public WebViewGoogle20OAuthHelper(Context context, WebView webView, Runnable okRunnable) {
        this.mContext = context;
        this.mWebView = webView;
        this.mOkRunnable = okRunnable;
        this.mApiKey = context.getString(R.string.google_web_client_key);
        this.mApiSecret = context.getString(R.string.google_web_client_secret);
        this.mCallbackUrl = context.getString(R.string.google_web_redirect_url);
    }

    public void start() {
        Log.v(TAG, "start called");

        // １．OAuthService オブジェクトを生成
        createService();

        // ２．ユーザに認証してもらう
        if (!auth()) {
            return;
        }

        // ３．アクセストークン（access Token）を取得
        if (!getAccessToken()) {
            return;
        }

        // ４．リクエスト送信
        request();
    }

    public void clear() {
        mOAuthVerifier = null;
        mAccessToken = null;
        mJson = null;
    }

    /**
     * OAuthService オブジェクトを生成
     *
     * Consumer Key (API Key), Consumer Secret (API Secret)
     * 事前に、Google Developers Consoleで、入手した値を設定します。
     */
    private void createService() {
        if (mService != null) {
            return;
        }
        Log.v(TAG, "createService called");

        mService = new ServiceBuilder()
                .provider(Google2Api.class)
                .apiKey(mApiKey)
                .apiSecret(mApiSecret)
                .callback(mCallbackUrl)
                .scope(
                        "https://www.googleapis.com/auth/userinfo.profile " +
                        "https://www.googleapis.com/auth/userinfo.email " +
                        "https://www.googleapis.com/auth/plus.me"
                )
                .build();
        Log.v(TAG, "OAuthServiceオブジェクトを生成しました。");
    }

    /**
     * ユーザに認証してもらう
     *
     * WebViewを使用します。
     */
    private boolean auth() {
        if (mOAuthVerifier != null) {
            return true;
        }
        Log.v(TAG, "auth called");

        // 認証ページURLを取得します。
        final String authUrl = mService.getAuthorizationUrl(null);
        Log.v(TAG, "authUrl=" + authUrl);

        // コールバックURLを使用する場合は、
        // 認証成功後、コールバックURLへ遷移し、
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

                if (url != null && url.startsWith(mCallbackUrl)) {
                    setOAuthVerifier(Uri.parse(url));
                }
            }
        });
        // WebViewに、連携アプリ認証画面を表示します。
        mWebView.loadUrl(authUrl);
        return false;
    }

    public void setOAuthVerifier(Uri uri) {
        Log.v(TAG, "setOAuthVerifier called");
        String oauthToken = uri.getQueryParameter("oauth_token");
        String oauthVerifier = uri.getQueryParameter("oauth_verifier");

//        // トークンをチェックします。
//        if (oauthToken == null || !oauthToken.equals(mRequestToken.getToken())) {
//            // 処理を中止します。
//            Log.d(TAG, "トークンエラーです。oauthToken=" + oauthToken + ",mRequestToken.getToken()=" + mRequestToken.getToken());
//            Toast.makeText(mContext, "エラーが発生しました。", Toast.LENGTH_LONG).show();
//            clear();
//            return;
//        }

        mOAuthVerifier = oauthVerifier;
        Log.d(TAG, "mOAuthVerifier=" + mOAuthVerifier);
        start();
    }

    /**
     * アクセストークン（access Token）を取得
     */
    private boolean getAccessToken() {
        if (mAccessToken != null) {
            return true;
        }
        Log.v(TAG, "getAccessToken called");

        AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                // HTTP通信を行うため、非UIスレッドで実行します。
                Verifier verifier = new Verifier(mOAuthVerifier);
                mAccessToken = mService.getAccessToken(null, verifier);
                Log.v(TAG, "mAccessToken=" + mAccessToken);
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
                mJson = requestOAuthUserInfo() + "\n" + requestPlusPeople();
                return true;
            }

            private String requestOAuthUserInfo() {
                String url = "https://www.googleapis.com/oauth2/v2/userinfo";
                OAuthRequest request = new OAuthRequest(Verb.GET, url);
                mService.signRequest(mAccessToken, request);
                Response response = request.send();
                String body = response.getBody();
                Log.v(TAG, "url=" + url + ", response=" + body);

                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                GoogleOAuthUserInfo oauthUserInfo = gson.fromJson(body, GoogleOAuthUserInfo.class);
                return "\"" + url + "\": " + gson.toJson(oauthUserInfo);
            }

            private String requestPlusPeople() {
                String url = "https://www.googleapis.com/plus/v1/people/me";
                OAuthRequest request = new OAuthRequest(Verb.GET, url);
                mService.signRequest(mAccessToken, request);
                Response response = request.send();
                String body = response.getBody();
                Log.v(TAG, "url=" + url + ", response=" + body);

                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                GooglePlusAccount account = gson.fromJson(body, GooglePlusAccount.class);
                return "\"" + url + "\": " + gson.toJson(account);
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
        return mJson;
    }
}
