package org.korjus.news;

import android.content.SharedPreferences;
import android.util.Log;

import java.util.Date;

public class UserSettings {
    private static final String TAG = "u8i9 UserSettings";
    MainActivity mainActivity;
    SharedPreferences settings;

    public UserSettings() {
        mainActivity = (MainActivity) MainActivity.context;
        settings = mainActivity.getSharedPreferences("settings", 0);
    }


    public Date getDate() {
        Date date = new Date(settings.getLong("date", 0));
        Log.d(TAG, "get Date From Settings: " + date);
        return date;
    }

    public int getSpinnerPosition() {
        return settings.getInt("spinnerPos", 0);
    }

    public String getCustomUrl() {
        String base = "https://www.reddit.com/r/worldnews/.rss";
        return settings.getString("urlCustom", base);
    }

    public void setLastVisitDate() {
        Clock clock = new Clock();
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong("date", clock.getCurrentMillis());
        editor.apply();

        Log.d(TAG, "Date saved to settings.");
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
        editor.putString("urlCustom", Url.getSinceUrl(clock.getDifferenceMillis()/1000));
        editor.apply();
    }

    @Override
    public String toString() {
        return settings.getAll().toString();
    }
}
