package org.korjus.news;

import android.util.Log;

public class Url {
    private static final String TAG = "u8i9 Url";
    private static String base = "https://www.reddit.com/r/worldnews/.rss";

    public static String getUrl(int pos) {
        String urlTemp = "https://www.reddit.com/r/worldnews/top/.rss?sort=top&t=";
        switch (pos) {
            case 0:
                urlTemp = base;
                break;
            case 1:
                urlTemp += "hour";
                break;
            case 2:
                urlTemp += "day";
                break;
            case 3:
                urlTemp += "week";
                break;
            case 4:
                urlTemp += "month";
                break;
            case 5:
                urlTemp += "year";
                break;
            case 6:
                Clock clock = new Clock();
                long dif = (clock.getDifferenceMillis() / 1000);
                Log.d(TAG, "dif is: " + dif);
                urlTemp = getSinceUrl(dif);
                break;
        }
        return urlTemp;
    }


    public static String getSinceUrl(Long dif) {
        String urlTemp = "https://www.reddit.com/r/worldnews/top/.rss?sort=top&t=";

        long minutes = dif / 60;

        if (minutes<60){
            urlTemp += "hour";
        } else if (minutes<60*24){
            urlTemp += "day";
        } else if (minutes<60*24*7){
            urlTemp += "week";
        } else {
            urlTemp += "month";
        }

        Log.d(TAG, String.valueOf(minutes) + " was difference in minutes and returning url: " + urlTemp);
        return urlTemp;
    }

}
