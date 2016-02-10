package org.korjus.news;

import android.util.Patterns;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.regex.Matcher;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

// This class parses RSS feeds from reddit.
public class RssParser {
    private static final String TAG = "u8i9 RssParser";
    private static final String ns = null;
    private Date listVisitDate;
    private boolean sinceLastVisit;

    // Finds all URLs from String and returns only the second URL
    public static String extractLink(String text) {
        Matcher m = Patterns.WEB_URL.matcher(text);
        int queue = 1;
        while (m.find()) {
            String url = m.group();
            if (queue == 2) {
                return url;
            }
            queue++;
        }
        return "Error at extracting second url";
    }

    public void parse(InputStream in) throws XmlPullParserException, IOException {
        loadSettings();
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            readFeed(parser);
        } finally {
            in.close();
        }
    }

    private void readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "feed");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag
            if (name.equals("entry")) {
                readEntry(parser);
            } else {
                skip(parser);
            }
        }
    }

    // Parses the contents of an entry. If it encounters a title, summary, or link tag, hands them
    // off
    // to their respective &quot;read&quot; methods for processing. Otherwise, skips the tag.
    private void readEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "entry");
        String title = null;
        String id = null;
        String link = null;
        String published = null;
        String content = null;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            switch (name) {
                case "title":
                    title = readTitle(parser);
                    break;
                case "id":
                    id = readId(parser);
                    break;
                case "link":
                    link = readLink(parser);
                    break;
                case "updated":
                    published = readPublished(parser);
                    break;
                case "content":
                    content = readContent(parser);
                    break;
                default:
                    skip(parser);
                    break;
            }
        }

        // Returns null when there is no entry in the "db Old" with oldId matching "id"
        OldNews oldNews = cupboard().withDatabase(MainActivity.dbOld).query(OldNews.class).withSelection("oldId = ?", id).get();

        // true means its new news and it will be added to db
        if (oldNews == null) {

            // Check if sort order is set to "since last visit"
            if (sinceLastVisit) {
                Clock clock = new Clock();
                // Current news item published time
                Date item = clock.getDateFromString(published);

                long diffMinutes = clock.getDifferenceMillis() / 60000;
                //Log.d(TAG, "diff minutes is:: " + String.valueOf(diffMinutes));

                Date current = new Date(listVisitDate.getTime() - 7200000); // todo minus 2h timezone

                // Its acceptable when news item are published after listVisitDate(Time when user last visited the app)
                // OR if difference is 60minutes or smaller. if its below 1 hour then all the past hour news will be acceptable
                boolean acceptable = item.after(current) || diffMinutes <= 60;

                //Log.d(TAG, "published: " + item + " current date:    " + current + " Acceptable: " + String.valueOf(acceptable));

                // Add news item to db only if they are acceptable
                if (acceptable){
                    DatabaseHelper.itemsInDb = cupboard().withDatabase(MainActivity.db).put(new NewsItem(content, id, link, published, title));
                } else {
                }

            } else { // Sort order is not set to "since last visit". Add data to db.
                DatabaseHelper.itemsInDb = cupboard().withDatabase(MainActivity.db).put(new NewsItem(content, id, link, published, title));
            }

        }
        // Last news item Id. It is used to get next page url.
        MainActivity.lastItemId = id;
    }

    // Processes title tags in the feed.
    private String readTitle(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "title");
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "title");
        return title;
    }

    // Processes link tags in the feed.
    private String readLink(XmlPullParser parser) throws IOException, XmlPullParserException {
        String link = "";
        parser.require(XmlPullParser.START_TAG, ns, "link");
        String tag = parser.getName();
        if (tag.equals("link")) {
            link = parser.getAttributeValue(null, "href");
            parser.nextTag();
        }
        parser.require(XmlPullParser.END_TAG, ns, "link");
        return link;
    }

    // Processes Id tags in the feed.
    private String readId(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "id");
        String id = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "id");
        return id;
    }

    // Processes published tags in the feed.
    private String readPublished(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "updated");
        String published = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "updated");
        return published;
    }

    // Processes content tags in the feed.
    private String readContent(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "content");
        String content = readText(parser);
        String link = extractLink(content);
        parser.require(XmlPullParser.END_TAG, ns, "content");
        return link;
    }

    // For the tags title and summary, extracts their text values.
    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    // Skips tags the parser isn't interested in. Uses depth to handle nested tags. i.e.,
    // if the next tag after a START_TAG isn't a matching END_TAG, it keeps going until it
    // finds the matching END_TAG (as indicated by the value of "depth" being 0).
    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }



    private void loadSettings(){
        UserSettings settings = new UserSettings();

        if (settings.getSpinnerPosition() == 6) {
            sinceLastVisit = true;
            listVisitDate = settings.getDate();

            //Log.d(TAG, "Settings:: " + settings.toString());
            } else {
            sinceLastVisit = false;
        }
    }
}
