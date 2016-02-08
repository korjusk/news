package org.korjus.news;

import android.content.SharedPreferences;

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
                urlTemp += "all"; // todo change to since
                break;
        }

        MainActivity mainActivity = (MainActivity) MainActivity.context;

        SharedPreferences settings = mainActivity.getSharedPreferences("settings", 0);
        SharedPreferences.Editor editor = settings.edit();

        editor.putString("urlCustom", urlTemp);
        editor.putInt("spinnerPos", pos);
        editor.apply();

        if(pos != MainActivity.spinnerPos){
            mainActivity.refresh();
        }

        mainActivity.loadSettings();

        return urlTemp;
    }

    public static String getUrl() {
        MainActivity mainActivity = (MainActivity) MainActivity.context;
        SharedPreferences settings = mainActivity.getSharedPreferences("settings", 0);
        return settings.getString("urlCustom", base);
    }
}
