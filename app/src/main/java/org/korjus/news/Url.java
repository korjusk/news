package org.korjus.news;

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
                urlTemp = "https://www.reddit.com/r/worldnews/new/.rss";
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
                long dif = (clock.getLastSessionDifferenceMillis());
                urlTemp = getSinceUrl(dif);
                break;
        }
        return urlTemp;
    }


    public static String getSinceUrl(Long millis) {
        String urlTemp = "https://www.reddit.com/r/worldnews/top/.rss?sort=top&t=";

        long hour = 3600000l;
        long day = 86400000l;
        long week = 604800000l;

        if (millis < hour){
            urlTemp += "hour";
        } else if (millis < day){
            urlTemp += "day";
        } else if (millis < week){
            urlTemp += "week";
        } else {
            urlTemp += "month";
        }

        return urlTemp;
    }

}
