package org.korjus.news;

import android.content.SharedPreferences;

import java.util.Date;

public class UserSettings {
    private static final String TAG = "u8i9 UserSettings";
    private MainActivity mainActivity;
    private SharedPreferences settings;

    public UserSettings() {
        mainActivity = (MainActivity) MainActivity.getContext();
        settings = mainActivity.getSharedPreferences("settings", 0);
    }

    public void deleteAll() {
        SharedPreferences.Editor editor = settings.edit();
        editor.clear();
        editor.apply();
    }

        //// Getters ////

    public long getCurrentSessionStartTime() {
        return settings.getLong("currentSession", 0l);
    }

    // Previous session start time
    public Date getLastSessionTime() {
        Date date = new Date(settings.getLong("lastSession", 0));
        return date;
    }

    // Previous download time
    public long getLastDownloadTime() {
        return settings.getLong("lastDownload", 0l);
    }

    public int getSpinnerPosition() {
        return settings.getInt("spinner", 0);
    }

    public void setSpinnerPosition(int i) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("spinner", i);
        editor.apply();
    }

    public String getCustomUrl() {
        return settings.getString("customUrl", getCustomSource());
    }

    public void setCustomUrl(int pos) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("customUrl", Url.getUrl(pos));
        editor.apply();
    }

    public String getCustomSource() {
        return settings.getString("source", "https://www.reddit.com/r/worldnews");
    }

        //// Setters ////

    public void setCustomSource(String source) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("source", source);
        editor.apply();
    }

    public String getLastItemId() {
        return settings.getString("lastItemId", "");
    }

    public void setLastItemId(String lastItemId) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("lastItemId", lastItemId);
        editor.apply();
    }

    public boolean getIsFirstVisit() {
        return settings.getBoolean("isFirstVisit", true);
    }

    public void setCurrentSessionStartTime() {
        Clock clock = new Clock();
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong("currentSession", clock.getCurrentTimeMillis());
        editor.apply();
    }

    // Previous session start time
    public void setLastSessionTime() {
        SharedPreferences.Editor editor = settings.edit();
        // Last Session time will be set to previous session "CurrentSessionStartTime";
        editor.putLong("lastSession", getCurrentSessionStartTime());
        editor.apply();
    }

    // Previous download time
    public void setLastDownloadTime() {
        Clock clock = new Clock();
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong("lastDownload", clock.getCurrentTimeMillis());
        editor.apply();
    }

    public void setIsFirstVisitToFalse() {
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("isFirstVisit", false);
        editor.apply();
    }

    @Override
    public String toString() {
        return settings.getAll().toString();
    }
}
