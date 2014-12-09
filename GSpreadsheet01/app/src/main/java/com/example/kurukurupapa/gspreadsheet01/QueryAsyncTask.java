package com.example.kurukurupapa.gspreadsheet01;

import android.os.AsyncTask;
import android.util.Log;

/**
 * Googleスプレッドシートに、非同期でHTTP通信するためのクラスです。
 */
public class QueryAsyncTask extends AsyncTask<Void, Void, Void> {
    private static final String TAG = QueryAsyncTask.class.getSimpleName();

    private String mUsername;
    private String mPassword;
    private Runnable mOnPostExecuteListener;
    private QueryService mQueryService;

    public QueryAsyncTask(String username, String password, Runnable onPostExecuteListener) {
        mUsername = username;
        mPassword = password;
        mOnPostExecuteListener = onPostExecuteListener;
        mQueryService = new QueryService();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            mQueryService.run(mUsername, mPassword);
        }
        catch (Exception e) {
            Log.v(TAG, e.toString());
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        // 呼び元へ制御を戻します。
        mOnPostExecuteListener.run();
    }

    /**
     * スプレッドシートの内容を取得します。
     * @return スプレッドシートの内容
     */
    public String getResult() {
        return mQueryService.getResult();
    }
}
