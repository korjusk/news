package org.korjus.news;

import java.util.Date;


// This class decides if data should be added to database
// and adds it to there if necessary
public class AddDataToDatabase {
    private static final String TAG = "u8i9 AddData";
    private Date lastVisitDate;
    private boolean sinceLastVisit;

    public AddDataToDatabase(String code, String title, String content,
                             String comments, String published) {

        MainActivity mainActivity = (MainActivity) MainActivity.getContext();
        DatabaseHelper dbHelper = DatabaseHelper.getInstance(mainActivity);

        // Checks if news is new
        // or user has seen the news already and the news id is in old news db
        if (dbHelper.isNewNews(code)) {
            loadSettings();

            // Check if sort order is set to "since last visit"
            if (sinceLastVisit) {
                Clock clock = new Clock();
                // Current news item published time
                Date item = clock.getTimeFromString(published);

                long diffMinutes = clock.getLastSessionDifferenceMillis() / 60000;
                //Log.d(TAG, "diff minutes is:: " + String.valueOf(diffMinutes));

                Date current = new Date(lastVisitDate.getTime() - clock.getTimezoneMillis());

                // Its acceptable when news item are published after lastVisitDate(Time when user last visited the app)
                // OR if difference is 60minutes or smaller. if its below 1 hour then all the past hour news will be acceptable
                boolean acceptable = item.after(current) || diffMinutes <= 60;


                // Add news item to db only if they are acceptable
                if (acceptable){
                    dbHelper.addNews(code, title, content, comments, published);
                }

            } else { // Sort order is not set to "since last visit". Add data to db.
                dbHelper.addNews(code, title, content, comments, published);
            }

        }

        // Last news item Id. It is used to get next page url.
        UserSettings settings = new UserSettings();
        settings.setLastItemId(code);
    }

    private void loadSettings(){
        UserSettings settings = new UserSettings();

        if (settings.getSpinnerPosition() == 6) {
            sinceLastVisit = true;
            lastVisitDate = settings.getLastSessionTime();

        } else {
            sinceLastVisit = false;
        }
    }
}
