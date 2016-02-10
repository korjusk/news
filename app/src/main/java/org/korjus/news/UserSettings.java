package org.korjus.news;

import android.content.SharedPreferences;
import android.util.Log;

import java.util.Date;

public class UserSettings {
    private static final String TAG = "u8i9 Settings";
    MainActivity mainActivity;
    SharedPreferences settings;

    public UserSettings() {
        mainActivity = (MainActivity) MainActivity.context;
        settings = mainActivity.getSharedPreferences("settings", 0);
    }


    public Date getDate() {
        Date date = new Date(settings.getLong("date", 0));
        Log.d(TAG, "getDateFromSettings: " + date);
        return date;
    }

    public int getSpinnerPosition() {
        return settings.getInt("spinnerPos", 0);
    }

    public String getCustomUrl() {
        String base = "https://www.reddit.com/r/worldnews/.rss";
        return settings.getString("urlCustom", base);
    }

    public long getDifference() {
        return settings.getLong("dif", 0);
    }

    public void setDate(long millis) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong("date", millis);
        editor.apply();

        Date date = new Date(millis);
        Log.d(TAG, "Date saved to settings: " + date);
    }

    public void setSpinnerPosition(int i) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("spinnerPos", i);
        editor.apply();
    }

    public void setCustomUrl(int pos) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("urlCustom", Url.getUrl(pos));
        editor.apply();
    }

    public void setCustomUrlWithDif() {
        Clock clock = new Clock();

        SharedPreferences.Editor editor = settings.edit();
        editor.putString("urlCustom", Url.getSinceUrl(clock.getDifferenceMinus3hours()));
        editor.apply();
    }

    public void setDifference(long dif) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong("dif", dif);
        editor.apply();
    }

    @Override
    public String toString() {
        return settings.getAll().toString();
    }
}
