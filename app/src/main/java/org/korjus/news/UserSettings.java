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

    public void deleteAll() {
        SharedPreferences.Editor editor = settings.edit();
        editor.clear();
        editor.apply();
    }

    public Date getLastVisitDate() {
        Date date = new Date(settings.getLong("date", 0));
        //Log.d(TAG, "get Date From Settings: " + date);
        return date;
    }

    public int getSpinnerPosition() {
        return settings.getInt("spinnerPos", 0);
    }

    public String getCustomUrl() {
        String base = "https://www.reddit.com/r/worldnews/.rss";
        return settings.getString("urlCustom", base);
    }

    public boolean getIsFirstVisit() {
        return settings.getBoolean("isFirstVisit", true);
    }

    public long getSessionStartTime() {
        return settings.getLong("session", 0l);
    }

    public void setLastVisitDate() {
        Clock clock = new Clock();
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong("date", clock.getCurrentMillis());
        editor.apply();

        Log.d(TAG, "last visit date saved to settings.");
    }

    public void setSessionStartTime() {
        Clock clock = new Clock();
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong("session", clock.getCurrentMillis());
        editor.apply();

        Log.d(TAG, "Session start time saved to settings.");
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
        editor.putString("urlCustom", Url.getSinceUrl(clock.getDifferenceMillis()));
        editor.apply();
    }

    public void setIsFirstVisit() {
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("isFirstVisit", false);
        editor.apply();
    }

    @Override
    public String toString() {
        return settings.getAll().toString();
    }
}
