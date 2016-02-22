package org.korjus.news;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Clock {
    private static final String TAG = "u8i9 Clock";
    private UserSettings settings;

    public Clock() {
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
        @SuppressLint("SimpleDateFormat")
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
        String base = "Last session was ";
        String end = "ago.";

        long minutes = 60000l;
        long hour = 3600000l;
        long day = 86400000l;
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

    // Returns true if last download was more than 30 minutes ago
    public boolean getIsNewSession() {
        boolean IsNewSession = getLastDownloadDifferenceMillis() > 1800000; // 30 minutes
        // Log.d(TAG, "IsNewActiveSession: " + String.valueOf(IsNewSession));
        return IsNewSession;
    }

    public int getTimezoneMillis() {
        TimeZone timeZone = TimeZone.getDefault();
        return timeZone.getRawOffset();
    }
}