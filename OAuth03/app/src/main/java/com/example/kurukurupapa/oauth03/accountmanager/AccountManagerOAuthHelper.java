package com.example.kurukurupapa.oauth03.accountmanager;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.kurukurupapa.oauth03.R;
import com.example.kurukurupapa.oauth03.service.GoogleOAuthUserInfo;
import com.example.kurukurupapa.oauth03.service.GooglePlusAccount;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class AccountManagerOAuthHelper {
    private static final String TAG = AccountManagerOAuthHelper.class.getSimpleName();

    private final Activity mActivity;
    private final Runnable mOkRunnable;
    private final Runnable mNgRunnable;
    private final String mClientKey;
    private String mAccountName;
    private String mAccountType;
    private String mAuthToken;
    private String mResult;

    public AccountManagerOAuthHelper(Activity activity, Runnable okRunnable, Runnable ngRunnable) {
        mActivity = activity;
        mOkRunnable = okRunnable;
        mNgRunnable = ngRunnable;
        mClientKey = mActivity.getString(R.string.google_native_client_key);
    }

    public void clear() {
        mAccountName = null;
        mAuthToken = null;
        mResult = null;
    }

    /**
     * OAuth処理を開始します。
     */
    public void start() {
        Log.v(TAG, "start called");

        // アカウントを選択
        if (!setSelectedAccount()) {
            return;
        }

        // 認証
        if (!auth()) {
            return;
        }

        // Webサービスアクセス
        if (!request()) {
            return;
        }

        // 処理完了
        mOkRunnable.run();
    }

    /**
     * アカウントを選択します。
     * @return 後続処理続行可能になった場合true
     */
    private boolean setSelectedAccount() {
        if (mAccountName != null) {
            return true;
        }

        Intent intent = AccountManager.get(mActivity).newChooseAccountIntent(
                null, null, new String[]{
                        "com.google"
                },
                false, null, null, null, null);
        mActivity.startActivityForResult(intent, AccountManagerActivity.REQUEST_CODE_ACCOUNT_PICKER);
        Log.v(TAG, "アカウント選択ダイアログを起動しました。");
        return false;
    }

    /**
     * アカウント選択ダイアログの戻り値から、アカウント名を取得し、OAuth処理を再開します。
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void onAccountPickerResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != AccountManagerActivity.RESULT_OK) {
            mNgRunnable.run();
            return;
        }

        mAccountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
        mAccountType = data.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE);
        Log.v(TAG, "アカウントが選択されました。accountName=" + mAccountName + ",accountType=" + mAccountType);

        // ※必要ならアカウント名を保存しておきます。

        start();
    }

    /**
     * 認証します。
     * @return 後続処理続行可能になった場合true
     */
    private boolean auth() {
        if (mAuthToken != null) {
            return true;
        }

        // FIXME いまいち実装方法が分かりませんでした。
        AccountManager manager = AccountManager.get(mActivity);
        manager.getAuthToken(new Account(mAccountName, mAccountType),
                "oauth2:https://www.googleapis.com/auth/userinfo.profile",
                //"oauth2:https://www.googleapis.com/auth/userinfo.email",
                //"oauth2:https://www.googleapis.com/auth/plus.me",
                null, true, new AccountManagerCallback<Bundle>() {
                    @Override
                    public void run(AccountManagerFuture<Bundle> future) {
                        Bundle bundle = null;
                        try {
                            bundle = future.getResult();
                            Intent intent = (Intent) bundle.get(AccountManager.KEY_INTENT);
                            Log.v(TAG, "intent=" + intent);
                            if (intent != null) {
                                mActivity.startActivity(intent);
                                return;
                            }
                            String accountName = bundle.getString(AccountManager.KEY_ACCOUNT_NAME);
                            String accountType = bundle.getString(AccountManager.KEY_ACCOUNT_TYPE);
                            mAuthToken = bundle.getString(AccountManager.KEY_AUTHTOKEN);
                            Log.v(TAG, "AuthTokenを取得しました。accountName=" + accountName + ",accountType=" + accountType);

                            // OAuth処理を再開します。
                            start();
                        } catch (OperationCanceledException e) {
                            Log.e(TAG, "e=" + e + ",e.message=" + e.getMessage());
                            Toast.makeText(mActivity, "エラーが発生しました。", Toast.LENGTH_LONG).show();
                            clear();
                            mNgRunnable.run();
                        } catch (IOException e) {
                            Log.e(TAG, "e=" + e + ",e.message=" + e.getMessage());
                            Toast.makeText(mActivity, "エラーが発生しました。", Toast.LENGTH_LONG).show();
                            clear();
                            mNgRunnable.run();
                        } catch (AuthenticatorException e) {
                            e.printStackTrace();
                            Log.e(TAG, "e=" + e + ",e.message=" + e.getMessage());
                            Toast.makeText(mActivity, "エラーが発生しました。", Toast.LENGTH_LONG).show();
                            clear();
                            mNgRunnable.run();
                        }
                    }
                }, null);
        Log.v(TAG, "アクセス許可ダイアログを起動しました。");


//        manager.getAuthToken(new Account(mAccountName, mAccountType), "oauth2:https://www.googleapis.com/auth/userinfo.profile", true,
//                new AccountManagerCallback<Bundle>() {
//                    public void run(AccountManagerFuture<Bundle> future) {
//                        try {
//                            Bundle bundle = future.getResult();
//                            if (bundle.containsKey(AccountManager.KEY_INTENT)) {
//                                //まだAPIアクセス許可が出ていない場合にgetAuthToken()すると
//                                //BundleにKEY_INTENTが含まれる。この場合AuthTokenはNULLとなる。
//                                Log.v("getAuthToken", "アクセス許可画面へ");
//                                Intent intent = bundle.getParcelable(AccountManager.KEY_INTENT);
//                                //「FLAG_ACTIVITY_NEW_TASK」の前の「~」はビット反転演算子
//                                //これをしないとアクセス許可画面でのボタンクリックを待たずにonActivityResult()が呼ばれてしまう
//                                intent.setFlags(intent.getFlags() & ~Intent.FLAG_ACTIVITY_NEW_TASK);
//                                //mActivity.startActivityForResult(intent, REQUEST_CODE_AUTH);
//                            } else if (bundle.containsKey(AccountManager.KEY_AUTHTOKEN)) {
//                                try {
//                                    String accountName = bundle.getString(AccountManager.KEY_ACCOUNT_NAME);
//                                    String authToken = bundle.getString(AccountManager.KEY_AUTHTOKEN);
//                                    if (authToken == null) {
//                                        throw new Exception("authTokenがNULL accountName=" + accountName);
//                                    }
//                                    Log.v("onGetAuthToken", "AuthToken取得完了 accountName=" + accountName + " authToken=" + authToken);
////                                    if (authTokenType.equals(AUTH_TOKEN_TYPE_PROFILE)) {
////                                        getUserInfo(); //ユーザー情報取得開始
////                                    } else if (authTokenType.equals(AUTH_TOKEN_TYPE_ADSENSE)) {
////                                        getAdSenseReport(); //レポート取得開始
////                                    }
//                                } catch (OperationCanceledException e) {
//                                    Log.v("onGetAuthToken", "AuthToken取得キャンセル");
//                                } catch (Exception e) {
//                                    Log.v("onGetAuthToken", "AuthToken取得失敗", e);
//                                }
//                            }
//                        } catch (Exception e) {
//                            Log.v("getAuthToken", "AuthToken取得失敗", e);
//                        }
//                    }
//                },
//                null);


        return false;
    }

    /**
     * Webサービスにアクセスします。
     * @return 後続処理続行可能になった場合true
     */
    private boolean request() {
        if (mResult != null) {
            return true;
        }

        // 非UIスレッドで、インターネットへアクセスします。
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                // HTTP通信を行うため、非UIスレッドで実行します。
                DefaultHttpClient client = new DefaultHttpClient();
                mResult = requestOAuthUserInfo(client) + "\n" + requestPlusPeople(client);
                return true;
            }

            private String requestOAuthUserInfo(DefaultHttpClient client) {
                String url = "https://www.googleapis.com/oauth2/v2/userinfo?access_token=" + mAuthToken + "&key=" + mClientKey;
                HttpGet httpGet = new HttpGet(url);
                HttpResponse res = null;
                String body = null;
                try {
                    res = client.execute(httpGet);
                    HttpEntity entity = res.getEntity();
                    body = EntityUtils.toString(entity);
                    Log.v(TAG, "url=" + url + ", response=" + body);
                } catch (IOException e) {
                    throw new RuntimeException("", e);
                }

                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                GoogleOAuthUserInfo oauthUserInfo = gson.fromJson(body, GoogleOAuthUserInfo.class);
                return gson.toJson(oauthUserInfo);
            }

            private String requestPlusPeople(DefaultHttpClient client) {
                String url = "https://www.googleapis.com/plus/v1/people/me?access_token=" + mAuthToken + "&key=" + mClientKey;
                HttpGet httpGet = new HttpGet(url);
                HttpResponse res = null;
                String body = null;
                try {
                    res = client.execute(httpGet);
                    HttpEntity entity = res.getEntity();
                    body = EntityUtils.toString(entity);
                    Log.v(TAG, "url=" + url + ", response=" + body);
                } catch (IOException e) {
                    throw new RuntimeException("", e);
                }

                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                GooglePlusAccount googlePlusAccount = gson.fromJson(body, GooglePlusAccount.class);
                return gson.toJson(googlePlusAccount);
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if (result) {
                    // OAuth処理を再開します。
                    start();
                } else {
                    Toast.makeText(mActivity, "エラーが発生しました。", Toast.LENGTH_LONG).show();
                    clear();
                    mNgRunnable.run();
                }
            }
        }.execute();
        return false;
    }

    public String getResult() {
        return mResult.toString();
    }
}
