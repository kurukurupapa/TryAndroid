package com.example.kurukurupapa.oauth03.googleapisclient;

import android.accounts.AccountManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.kurukurupapa.oauth03.R;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAuthIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class GoogleApisClientOAuthHelper {
    private static final String TAG = GoogleApisClientOAuthHelper.class.getSimpleName();

    private final GoogleApisClientActivity mActivity;
    private final Runnable mOkRunnable;
    private final Runnable mNgRunnable;
    private GoogleAccountCredential mCredential;
    private Drive mDrive;
    private UserRecoverableAuthIOException mUserRecoverableAuthIOException;
    private StringBuilder mResult;

    public GoogleApisClientOAuthHelper(GoogleApisClientActivity activity, Runnable okRunnable, Runnable ngRunnable) {
        mActivity = activity;
        mOkRunnable = okRunnable;
        mNgRunnable = ngRunnable;
    }

    public void clear() {
        mCredential = null;
        mDrive = null;
        mUserRecoverableAuthIOException = null;
        mResult = null;
    }

    /**
     * OAuth処理を開始します。
     */
    public void start() {
        // GoogleAccountCredentialオブジェクトを作成
        createCredential();

        // アカウントを選択
        if (!setSelectedAccount()) {
            return;
        }

        // Googleドライブサービスのオブジェクトを作成
        createDrive();

        // 認証
        if (!auth()) {
            return;
        }

        // Googleサービスアクセス
        if (!requestDriveFileList()) {
            return;
        }

        // 処理完了
        mOkRunnable.run();
    }

    /**
     * GoogleAccountCredentialオブジェクトを作成します。
     */
    private void createCredential() {
        if (mCredential != null) {
            return;
        }

        List<String> scopes = Arrays.asList(
                DriveScopes.DRIVE_READONLY
        );
        mCredential = GoogleAccountCredential.usingOAuth2(mActivity, scopes);
        Log.v(TAG, "GoogleAccountCredentialオブジェクトを作成しました。");
    }

    /**
     * アカウントを選択します。
     * @return 後続処理続行可能になった場合true
     */
    private boolean setSelectedAccount() {
        if (mCredential.getSelectedAccount() != null) {
            return true;
        }

        mActivity.startActivityForResult(mCredential.newChooseAccountIntent(), GoogleApisClientActivity.REQUEST_CODE_ACCOUNT_PICKER);
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
        if (resultCode != GoogleApisClientActivity.RESULT_OK) {
            mNgRunnable.run();
            return;
        }

        String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
        mCredential.setSelectedAccountName(accountName);
        Log.v(TAG, "アカウントが選択されました。accountName=" + accountName);

        // ※必要ならアカウント名を保存しておきます。

        start();
    }

    /**
     * Googleドライブサービスのオブジェクトを作成します。
     */
    private void createDrive() {
        if (mDrive != null) {
            return;
        }

        mDrive = new Drive
                .Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), mCredential)
                .setApplicationName(mActivity.getString(R.string.app_name))
                .build();
        Log.v(TAG, "Driveオブジェクトを作成しました。");
    }

    /**
     * 認証します。
     * @return 後続処理続行可能になった場合true
     */
    private boolean auth() {
        // 認証
        if (mUserRecoverableAuthIOException == null) {
            return true;
        }

        mActivity.startActivityForResult(mUserRecoverableAuthIOException.getIntent(), GoogleApisClientActivity.REQUEST_CODE_AUTHORIZATION);
        mUserRecoverableAuthIOException = null;
        Log.v(TAG, "認証ダイアログを起動しました。");
        return false;
    }

    /**
     * 認証ダイアログにて承認された場合、OAuth処理を再開します。
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void onAuthorizationResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != GoogleApisClientActivity.RESULT_OK) {
            mNgRunnable.run();
            return;
        }

        start();
    }

    /**
     * Googleサービスにアクセスします。
     * @return 後続処理続行可能になった場合true
     */
    private boolean requestDriveFileList() {
        if (mResult != null) {
            return true;
        }

        // 非UIスレッドで、インターネットへアクセスします。
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                boolean result = false;
                try {
                    FileList fileList = mDrive.files().list().execute();
                    mResult = new StringBuilder();
                    for (File file : fileList.getItems()) {
                        if (mResult.length() > 0) {
                            mResult.append("\n");
                        }
                        mResult.append(file.getTitle());
                    }
                    Log.v(TAG, "Googleドライブのファイル一覧を取得しました。");
                    result = true;

                } catch (UserRecoverableAuthIOException e) {
                    // 認証が必要な場合に呼び出されます。
                    mUserRecoverableAuthIOException = e;
                    result = true;
                } catch (GoogleAuthIOException e) {
                    // Google Developer Consoleで、クライアントIDの登録に不備があった場合、または
                    // 当アプリへのデジタル署名の組み込みに不備があった場合、呼び出されます。
                    Log.e(TAG, "e=" + e + ", e.message=" + e.getMessage());
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage());
                }
                return result;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if (result) {
                    // OAuth処理を再開します。
                    start();
                } else {
                    Toast.makeText(mActivity, "エラーが発生しました。", Toast.LENGTH_LONG).show();
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
