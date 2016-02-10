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

    public long getCurrentMillis() {
        Date c = new Date();
        Log.d(TAG,"getCurrentMillis " + c.toString());
        return c.getTime();
    }


    public Date getDateFromSettings() {
        return settings.getDate();
    }

    public long getDifferenceMinus3hours() { // in seconds minus time zone 2 hours
        long difference = (getCurrentMillis() - getDateFromSettings().getTime()) / 1000 - 7200;
        return difference;
    }

    public long getDifference() { // in seconds
        long difference = (getCurrentMillis() - getDateFromSettings().getTime()) / 1000;
        return difference;
    }

    public Date getDateFromString(String raw){
        SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd-HH:mm:ss");
        Date date = new Date();

        try {
            // raw = 2016-02-08T22:39:57+00:00
            String dateString = raw.replace("T","-");
            // date = 2016-02-08-22:39:57+00:00
            date = ft.parse(dateString);
        } catch (Exception e) {
            e.getStackTrace();
        }
        return date;
    }

    public void setCurrentMillisMinus3hours() {
        settings.setDate(getCurrentMillis() - 10800000);
        // todo minus timezone only
    }
}