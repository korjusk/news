package org.korjus.news;

import java.util.Date;


// This class decides if data should be added to database
// and adds it to there if necessary
public class AddDataToDatabase {
    private static final String TAG = "u8i9 AddData";
    private Date listVisitDate;
    private boolean sinceLastVisit;

    public AddDataToDatabase(String content, String id, String link, String published, String title) {
       /* // Returns null when there is no entry in the "db Old" with oldId matching "id"
        //OldNews oldNews = cupboard().withDatabase(MainActivity.dbOld).query(OldNews.class).withSelection("oldId = ?", id).get();

        // true means its new news and it will be added to db
        if (oldNews == null) {
            loadSettings();

            // Check if sort order is set to "since last visit"
            if (sinceLastVisit) {
                Clock clock = new Clock();
                // Current news item published time
                Date item = clock.getTimeFromString(published);

                long diffMinutes = clock.getLastSessionDifferenceMillis() / 60000;
                //Log.d(TAG, "diff minutes is:: " + String.valueOf(diffMinutes));

                Date current = new Date(listVisitDate.getTime() - clock.getTimezoneMillis());

                // Its acceptable when news item are published after listVisitDate(Time when user last visited the app)
                // OR if difference is 60minutes or smaller. if its below 1 hour then all the past hour news will be acceptable
                boolean acceptable = item.after(current) || diffMinutes <= 60;

*//*                Log.d(TAG, "published: " + item + " current date:    " + current + " Acceptable: " + String.valueOf(acceptable));
                Log.d(TAG, "timezon: " + String.valueOf(clock.getTimezoneMillis()));
                Log.d(TAG, "published: " + published);
                Log.d(TAG, "item: " + item);
                Log.d(TAG, "listVisitDate: " + listVisitDate);
                Log.d(TAG, "current: " + current);
                Log.d(TAG, "acceptable: " + String.valueOf(acceptable));*//*

                // Add news item to db only if they are acceptable
                if (acceptable){
                    //DatabaseHelper.itemsInDb = cupboard().withDatabase(MainActivity.db).put(new NewsItem(content, id, link, published, title));
                }

            } else { // Sort order is not set to "since last visit". Add data to db.
                //DatabaseHelper.itemsInDb = cupboard().withDatabase(MainActivity.db).put(new NewsItem(content, id, link, published, title));
            }

        }
        // Last news item Id. It is used to get next page url.
        MainActivity.lastItemId = id;*/
    }

    private void loadSettings(){
        UserSettings settings = new UserSettings();

        if (settings.getSpinnerPosition() == 6) {
            sinceLastVisit = true;
            listVisitDate = settings.getLastSessionTime();

        } else {
            sinceLastVisit = false;
        }
    }
}
