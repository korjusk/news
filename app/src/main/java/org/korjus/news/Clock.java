package org.korjus.news;

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

    public long getCurrentMillis() {
        Date c = new Date();
        //Log.d(TAG, "getCurrentMillis " + c.getTime() + " time: " + c);
        return c.getTime();
    }

    public long getDifferenceMillis() {
        long difference = (getCurrentMillis() - settings.getDate().getTime());
        return difference;
    }

    public Date getDateFromString(String raw) {
        SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
        Date date = new Date();

        try {
            // raw = 2016-02-08T22:39:57+00:00
            String dateString = raw.replace("T", "-");
            // date = 2016-02-08-22:39:57+00:00
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
        int week = 604800000;

        if(millis < hour) {
            return base + String.valueOf(millis / minutes) + " minutes ago.\nShowing last hour news.";
        } else if (millis < day) {
            return base + String.valueOf(millis / hour) + " hours " + end;
        } else if (millis < 30 * day) {
            return base + String.valueOf(millis / day) + " days " + end;
        } else {
            return base + "long time ago.\nShowing last month news.";
        }
    }

}