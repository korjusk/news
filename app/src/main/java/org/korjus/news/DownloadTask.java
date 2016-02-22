package org.korjus.news;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

// Implementation of AsyncTask used to download XML feed from reddit
public class DownloadTask extends AsyncTask<String, Void, String> {
    private static final String TAG = "u8i9 DownloadTask";
    private String lastUrl;

    @Override
    protected String doInBackground(String... urls) {
        lastUrl = (urls[0]);
        Log.d(TAG, "AsyncTask Background: " + lastUrl);

        try {
            loadXmlFromNetwork(urls[0]);
        } catch (IOException | XmlPullParserException e) {
            e.getStackTrace();
        }
        //Log.d(TAG, "before lag");
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        //Log.d(TAG, "after lag");
        MainActivity mainActivity = (MainActivity) MainActivity.getContext();
        DatabaseHelper dbHelper = DatabaseHelper.getInstance(mainActivity);
        dbHelper.addFiveNewsToOld(1);

        long itemsInNewsDb = dbHelper.getNewsSize();
        // Change the pager adapter nr of page count
        int newPageNr = (int) Math.ceil(itemsInNewsDb / 5.0);
        mainActivity.setSectionsAdapterCount(newPageNr);
        Log.d(TAG, String.valueOf(itemsInNewsDb) + " news in db. " + String.valueOf(dbHelper.getOldSize()) + " in old db.");

        PlaceholderFragment.updateAllFragments();

        // Download more data if there's below 25 news in db
        if(itemsInNewsDb < 25){
            UserSettings settings = new UserSettings();
            String url = settings.getCustomUrl();

            String newUrl = url + "?count=25&after=" + settings.getLastItemId();

            if (url.contains(".rss?sort=")) {
                newUrl = newUrl.replace("?count=", "&count=");
            }

            // download more data if the url is new
            if (newUrl.equals(lastUrl)){
                Log.d(TAG, "Escaped loop");

                if (itemsInNewsDb < 5) {
                    Toast.makeText(mainActivity, "No more new news...", Toast.LENGTH_LONG).show();
                }
            } else {
                new DownloadTask().execute(newUrl);
            }
        }
    }



    // Uploads XML from reddit, parses it, and combines it with
    // HTML markup. Returns HTML string.
    private void loadXmlFromNetwork(String urlString) throws XmlPullParserException, IOException {
        InputStream stream = null;
        RssParser rssParser = new RssParser();

        try {
            stream = downloadUrl(urlString);
            rssParser.parse(stream);
            // Makes sure that the InputStream is closed after the app is finished using it.
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
    }

    // Given a string representation of a URL, sets up a connection and gets
    // an input stream.
    private InputStream downloadUrl(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000 /* milliseconds */);
        conn.setConnectTimeout(15000 /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        // Starts the query
        conn.connect();
        return conn.getInputStream();
    }
}