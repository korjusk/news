package org.korjus.news;

import android.os.AsyncTask;
import android.util.Log;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

// Implementation of AsyncTask used to download XML feed from reddit
public class DownloadTask extends AsyncTask<String, Void, String> {
    private static final String TAG = "u8i9 DownloadTask";
    String lastUrl;


    @Override
    protected String doInBackground(String... urls) {
        lastUrl = (urls[0]);
        Log.d(TAG, "AsyncTask Background: " + lastUrl);

        try {
            loadXmlFromNetwork(urls[0]);
        } catch (IOException e) {
            e.getStackTrace();
        } catch (XmlPullParserException e) {
            e.getStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
/*
        // Update all fragments that are created
        for (Object value : MainActivity.m1.values()) {
            PlaceholderFragment fragment = (PlaceholderFragment) value;
            fragment.updateUI();
        }

        // Change the pager adapter nr of page count
        int newPageNr = (int) Math.ceil(items / 5.0);
        MainActivity.SectionsAdapter.setCount(newPageNr);

        // Triggers a redraw of the PageAdapter
        MainActivity.SectionsAdapter.notifyDataSetChanged();


        // Download more data if there's below 25 news in db
        if(items < 25){
            UserSettings settings = new UserSettings();
            String url = settings.getCustomUrl();

            String newUrl;
            if(url.contains("?")){
                newUrl = url + "&count=25&after=" + MainActivity.lastItemId;
            } else {
                newUrl = url + "?count=25&after=" + MainActivity.lastItemId;
            }

            // download more data if the url is new
            if (newUrl.equals(lastUrl)){
                Log.d(TAG, "Escaped loop");
                //saveLastVisitDate();

                if (items < 5) {
                    Toast.makeText(MainActivity.context, "No more new news...", Toast.LENGTH_LONG).show();
                }
            } else {
                new DownloadTask().execute(newUrl);
            }
        } else {
            //saveLastVisitDate();
        }*/
    }

    private void saveLastVisitDate() {
        UserSettings settings = new UserSettings();

/*        // try to save new url and date to settings
        if (settings.getSpinnerPosition() == 6) {  // temp

            Clock clock = new Clock();

            if (clock.getIsNewSession()) {
                Log.d(TAG, "diffMinutes > 20");
                settings.setLastVisitDate();
            }
        }

        */
        if (MainActivity.shouldSave) {
            //settings.setLastVisitDate();
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
            // Makes sure that the InputStream is closed after the app is
            // finished using it.
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