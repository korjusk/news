package org.korjus.news;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Clock {
    private static final String TAG = "u8i9 Clock";
    MainActivity mainActivity;
    UserSettings settings;

    public Clock() {
        mainActivity = (MainActivity) MainActivity.context;
        settings = new UserSettings();
    }

    public long getCurrentTimeMillis() {
        Date c = new Date();
        return c.getTime();
    }

    public long getLastSessionDifferenceMillis() {
        long difference = (getCurrentTimeMillis() - settings.getLastSessionTime().getTime());
        return difference;
    }

    public long getLastDownloadDifferenceMillis() {
        long start = settings.getLastDownloadTime();
        long current = getCurrentTimeMillis();
        return current - start;
    }

    public Date getTimeFromString(String raw) {
        SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
        Date date = new Date();

        try {
            // raw = 2016-02-08T22:39:57+00:00
            String dateString = raw.replace("T", "-");
            // dateString = 2016-02-08-22:39:57+00:00
            date = ft.parse(dateString);
        } catch (Exception e) {
            e.getStackTrace();
        }
        return date;
    }

    public String getStringFromMillis(long millis){
        String base = "Last visit was ";
        String end = "ago.";

        int minutes = 60000;
        int hour = 3600000;
        int day = 86400000;
        long month = 2629746000l;

        if(millis < hour) {
            return base + String.valueOf(millis / minutes) + " minutes ago.\nShowing last hour news.";
        } else if (millis < day) {
            return base + String.valueOf(millis / hour) + " hours " + end;
        } else if (millis < month) {
            return base + String.valueOf(millis / day) + " days " + end;
        } else {
            return base + "long time ago.\nShowing last month news.";
        }
    }

    // Returns true if last download was more than 20 minutes ago
    public boolean getIsNewSession() {
        boolean IsNewSession = getLastDownloadDifferenceMillis() > 1200000; // 20 minutes
        Log.d(TAG, "IsNewActiveSession: " + String.valueOf(IsNewSession));
        return IsNewSession;
    }
}