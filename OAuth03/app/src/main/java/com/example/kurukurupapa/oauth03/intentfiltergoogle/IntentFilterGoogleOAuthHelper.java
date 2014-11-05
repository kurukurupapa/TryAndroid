package com.example.kurukurupapa.oauth03.intentfiltergoogle;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

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

/**
 * Scribeライブラリを使用して、GoogleのOAuth 2.0認証を行います。
 *
 * ※当アプリから、ブラウザを起動する際、標準ブラウザだと上手く動いたけど、Chromeだとダメでした。
 *
 * 事前準備
 * 次のサイトで、プロジェクトを作成しました。
 *   Google Developers Console
 *   https://console.developers.google.com/project
 * プロジェクトでは、次の設定を行っておきました。
 * 「APIと認証＞API」ページで、「Google+ API」を有効化しました。
 * 「APIと認証＞認証情報」ページで、「新しいクライアントIDを作成」ボタンをクリックし、
 * アプリケーションの種類を「インストールされているアプリケーション」、インストールされているアプリケーションの種類を「その他」にして、
 * 登録し、クライアントID、クライアントシークレット、リダイレクトURIが作成されました。
 * 「APIと認証＞同意画面」ページで、必須項目を登録しておきました。
 * ※同意画面の設定を忘れると、当アプリからGoogleの認証画面を呼び出したときに、「invalid_client」エラーが表示されました。
 *
 * Scribe 1.3.5で、Google OAuth 2.0の認証を行うには、ひと手間必要な模様でした。
 * 次のプログラムを、当アプリに組み込みました。
 *   Google OAuth2.0 for scribe-java
 *   https://gist.github.com/yincrash/2465453#file-google2api-java
 *
 * 注意事項
 * OAuth2.0では、リクエストトークンの取得が不要になりました。
 *
 * 参考
 * 開発日誌 (7) : scribeでGoogle OAuth 2.0 (client_secretなしで認証) - 家族ToDo(仮)開発日誌
 * http://kazokutodo.hatenablog.com/entry/2014/02/10/011927
 */
public class IntentFilterGoogleOAuthHelper {
    private static final String TAG = IntentFilterGoogleOAuthHelper.class.getSimpleName();

    private final String mClientKey;
    private final String mClientSecret;
    private final String mRedirectUrl;
    private final Context mContext;
    private final Runnable mOkRunnable;

    private OAuthService mService;
    private Token mAccessToken;
    private String mOAuthVerifier;
    private String mResult;

    public IntentFilterGoogleOAuthHelper(Context context, Runnable okRunnable) {
        this.mContext = context;
        this.mOkRunnable = okRunnable;
        this.mClientKey = context.getString(R.string.google_web_client_key);
        this.mClientSecret = context.getString(R.string.google_web_client_secret);
        this.mRedirectUrl = context.getString(R.string.google_web_redirect_url);
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
        mResult = null;
    }

    /**
     * OAuthService オブジェクトを生成
     *
     * Consumer Key (API Key), Consumer Secret (API Secret)
     * 事前に、Google Developers Consoleで、入手した値を設定します。
     *
     * コールバックURL
     * 事前に、Google Developers Consoleで、入手した値を設定します。
     * 独自スキーマを指定すると、エラーになりました。
     */
    private void createService() {
        Log.v(TAG, "createService called");
        if (mService != null) {
            return;
        }

        mService = new ServiceBuilder()
                .provider(Google2Api.class)
                .apiKey(mClientKey)
                .apiSecret(mClientSecret)
                .callback(mRedirectUrl)
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
     * コールバックURLとインテントフィルターを使用します。
     * ※PINは使用しません。
     *
     * OAuthの認証にWebViewを使うのはやめよう - Shogo's Blog
     * http://shogo82148.github.io/blog/2012/11/24/no-more-webview/
     *
     * AndroidからGoogle OAuthでプロフィール情報にアクセスする方法 - 今日の役に立たない一言 － Today’s Trifle! －
     * http://d.hatena.ne.jp/satoshis/20130119/p1
     *
     * 特定のURLをフックしてアプリを起動させる（暗黙的インテント） - tomstay's memo
     * http://tomstay.hatenablog.jp/entry/20110719/1311072062
     */
    private boolean auth() {
        Log.v(TAG, "auth called");
        if (mOAuthVerifier != null) {
            return true;
        }

        // 認証ページURLを取得します。
        final String authUrl = mService.getAuthorizationUrl(null);
        Log.v(TAG, "authUrl=" + authUrl);

        // ブラウザに、連携アプリ認証画面を表示します。
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(authUrl));
        mContext.startActivity(intent);

        return false;
    }

    /**
     * ブラウザでの認証後、コールバックURLに遷移した際に付加される（GETパラメータ）認証情報を設定します。
     * @param uri コールバックURL＋認証情報（GETパラメータ）
     */
    public void setOAuthVerifier(Uri uri) {
        Log.v(TAG, "setOAuthVerifier called");
        mOAuthVerifier = uri.getQueryParameter("code");
        Log.d(TAG, "mOAuthVerifier=" + mOAuthVerifier);
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
                mResult = requestOAuthUserInfo() + "\n" + requestPlusPeople();
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
        return mResult;
    }
}
