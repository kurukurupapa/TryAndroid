package com.example.kurukurupapa.gspreadsheet01;

import android.util.Log;

import com.google.gdata.data.spreadsheet.ListEntry;
import com.google.gdata.data.spreadsheet.ListFeed;
import com.google.gdata.data.spreadsheet.SpreadsheetEntry;
import com.google.gdata.data.spreadsheet.SpreadsheetFeed;
import com.google.gdata.data.spreadsheet.WorksheetEntry;
import com.google.gdata.data.spreadsheet.WorksheetFeed;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * GoogleスプレッドシートにHTTP通信を行い、所定のスプレッドシートの内容を取得するサービスです。
 *
 * ほぼ下記ページのコピーです。
 * Google Sheets API version 3.0 - Google Apps Platform — Google Developers
 * https://developers.google.com/google-apps/spreadsheets/#retrieving_a_list-based_feed
 */
public class QueryService {
    private static final String TAG = QueryService.class.getSimpleName();
    private static final String SPREADSHEET_TITLE = "GDataDemo";
    private static final String SHEET_NAME = "RecordDemo";

    private com.google.gdata.client.spreadsheet.SpreadsheetService mService;
    private String mResult;

    /**
     * コンストラクタ
     */
    public QueryService() {
        // Googleライブラリのサービスオブジェクトを作成します。
        mService = new com.google.gdata.client.spreadsheet.SpreadsheetService(SPREADSHEET_TITLE);
    }

    /**
     * スプレッドシートの内容を取得します。
     * @return スプレッドシートの文字列表現
     */
    public String getResult() {
        return mResult;
    }

    /**
     * HTTP通信を行います。
     *
     * @param username Googleアカウントのユーザ名
     * @param password 上記ユーザのパスワード、または事前に登録したアプリ用パスワード
     * @throws AuthenticationException
     * @throws MalformedURLException
     * @throws IOException
     * @throws ServiceException
     */
    public void run(String username, String password) throws AuthenticationException, MalformedURLException, IOException, ServiceException {
        // Authorize the service object for a specific user (see other sections)
        // ユーザ認証を行います。
        login(username, password);

        // Define the URL to request.  This should never change.
        URL SPREADSHEET_FEED_URL = new URL("https://spreadsheets.google.com/feeds/spreadsheets/private/full");

        // Make a request to the API and get all spreadsheets.
        SpreadsheetFeed feed = mService.getFeed(SPREADSHEET_FEED_URL, SpreadsheetFeed.class);
        List<SpreadsheetEntry> spreadsheets = feed.getEntries();
        Log.d(TAG, "スプレッドシート件数=" + spreadsheets.size());

        if (spreadsheets.size() == 0) {
            // There were no spreadsheets, act accordingly.
            throw new RuntimeException("スプレッドシートが見つかりませんでした。title=" + SPREADSHEET_TITLE);
        }

        // Choose a spreadsheet more intelligently based on your app's needs.
        // スプレッドシートの一覧から、所定のスプレッドシートを探します。
        SpreadsheetEntry spreadsheet = null;
        for (SpreadsheetEntry e : spreadsheets) {
            if (e.getTitle().getPlainText().equals(SPREADSHEET_TITLE)) {
                spreadsheet = e;
            }
        }
        if (spreadsheet == null) {
            throw new RuntimeException("スプレッドシートが見つかりませんでした。title=" + SPREADSHEET_TITLE);
        }

        // Get the first worksheet of the first spreadsheet.
        // Choose a worksheet more intelligently based on your app's needs.
        // ワークシートの一覧から、所定のワークシートを探します。
        WorksheetFeed worksheetFeed = mService.getFeed(spreadsheet.getWorksheetFeedUrl(), WorksheetFeed.class);
        List<WorksheetEntry> worksheets = worksheetFeed.getEntries();
        Log.d(TAG, "ワークシート数=" + worksheets.size());
        WorksheetEntry worksheet = null;
        for (WorksheetEntry e : worksheets) {
            if (e.getTitle().getPlainText().equals(SHEET_NAME)) {
                worksheet = e;
            }
        }
        if (worksheet == null) {
            throw new RuntimeException("ワークシートが見つかりませんでした。title=" + SHEET_NAME);
        }

        // Fetch the list feed of the worksheet.
        URL listFeedUrl = worksheet.getListFeedUrl();
        ListFeed listFeed = mService.getFeed(listFeedUrl, ListFeed.class);
        Log.d(TAG, "行数=" + listFeed.getEntries().size());

        // Iterate through each row, printing its cell values.
        StringBuilder lines = new StringBuilder();
        for (ListEntry row : listFeed.getEntries()) {
            StringBuilder columns = new StringBuilder();
            // Iterate over the remaining columns, and print each cell value
            for (String tag : row.getCustomElements().getTags()) {
                if (columns.length() > 0) {
                    columns.append(",");
                }
                columns.append(row.getCustomElements().getValue(tag));
            }
            lines.append(columns.toString() + "\n");
        }
        if (lines.length() == 0) {
            mResult = "データがありませんでした。";
        } else {
            mResult = lines.toString();
        }
    }

    /**
     * パスワード認証します。
     * @param username Googleアカウントのユーザ名
     * @param password 上記ユーザのパスワード、または事前に登録したアプリ用パスワード
     * @throws AuthenticationException
     */
    private void login(String username, String password) throws AuthenticationException {
        mService.setUserCredentials(username, password);
        Log.d(TAG, "ユーザ認証に成功しました。");
    }

}
