package org.korjus.news;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

/*Todo
rename everything
clean code
comment
oldDb has duplicated values
adding items to db is too slow
OldNews class in unnecessary
public static -> private

*/

// cd data/data/org.korjus.news/databases
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "u8i9 MainActivity";
    private static final String URL = "https://www.reddit.com/r/worldnews/.rss";
    private static final String URLold = "https://www.reddit.com/r/worldnews/top/.rss?sort=top&t=all";
    private static final String URL2 = "https://www.reddit.com/r/worldnews/.rss?limit=100";
    public static long itemsInDb;
    public static long itemsInDbOld;
    public static SQLiteDatabase db;
    public static SQLiteDatabase dbOld;
    public static Context context;
    public static Map m1 = new HashMap();
    static long startTime;
    public static String lastId;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        context = this;

        // Create the adapter that will return a fragment for each tab of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int page) {
                // Add selected page news to old news database

                // 5 news per page so loop 5 times
                for (int i = 1; i < 6; i++) {
                    // News location in db
                    int e = page * 5 + i;

                    // Convert news to old news and add that to old news db
                    OldNews oldNews = new OldNews(cupboard().withDatabase(db).get(NewsItem.class, e).getId());
                    itemsInDbOld = cupboard().withDatabase(dbOld).put(oldNews);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        // Instantiate Database Helper OLD
        DatabaseHelperOld dbHelperOld = new DatabaseHelperOld(this);
        dbOld = dbHelperOld.getWritableDatabase();

        // Instantiate Database Helper
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        db = dbHelper.getWritableDatabase();

        // Download and parse data from URL
        new DownloadXmlTask().execute(URL);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Refreshed!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                // Close and delete Database so It could be recreated
                db.close();
                deleteDatabase(DatabaseHelper.DATABASE_NAME);

                itemsInDb = 1l;

                // Start Main Activity
                Intent goToHome = new Intent(context, MainActivity.class);
                startActivity(goToHome);
            }
        });
        Log.d(TAG, "MainActivity onCreate end, items in DB: " + String.valueOf(itemsInDb) + " In OLD DB: " + String.valueOf(itemsInDbOld));

    }

    // Uploads XML from reddit, parses it, and combines it with
    // HTML markup. Returns HTML string.
    private void loadXmlFromNetwork(String urlString) throws XmlPullParserException, IOException {
        InputStream stream = null;
        StackOverflowXmlParser stackOverflowXmlParser = new StackOverflowXmlParser();

        try {
            stream = downloadUrl(urlString);
            stackOverflowXmlParser.parse(stream);
            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (stream != null) {
                stream.close();
            }
        }

    }

    // Given a string representation of a URL, sets up a connection and gets
    // an input stream.
    private InputStream downloadUrl(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000 /* milliseconds */);
        conn.setConnectTimeout(15000 /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        // Starts the query
        conn.connect();
        return conn.getInputStream();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            // Close and delete Database so It could be recreated
            dbOld.close();
            db.close();

            deleteDatabase(DatabaseHelperOld.DATABASE_NAME);
            deleteDatabase(DatabaseHelper.DATABASE_NAME);

            itemsInDbOld = 0l;
            itemsInDb = 1l;

            // Start Main Activity
            Intent goToHome = new Intent(context, MainActivity.class);
            startActivity(goToHome);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        ArrayAdapter<String> itemsAdapter;
        ArrayList<String> dataList;
        int pageNr;

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            m1.put(sectionNumber, fragment);
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            pageNr = getArguments().getInt(ARG_SECTION_NUMBER);

            dataList = new ArrayList<>();

            // Add 5 news items to dataList if they are available
            for (int i = 1; i < 6; i++) {
                int e = (pageNr - 1) * 5 + i;

                NewsItem item = cupboard().withDatabase(db).get(NewsItem.class, e);
                if (item != null) {
                    dataList.add(item.getTitle());
                } else {
                    break;
                }
            }

            itemsAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, dataList);

            ListView listView = (ListView) rootView.findViewById(R.id.listView);
            listView.setAdapter(itemsAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    int pos = (pageNr - 1) * 5 + position + 1;
                    Intent goToImdb = new Intent(Intent.ACTION_VIEW);
                    goToImdb.setData(Uri.parse(cupboard().withDatabase(db).get(NewsItem.class, pos).getContent()));
                    startActivity(goToImdb);
                }
            });

            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    int pos = (pageNr - 1) * 5 + position + 1;
                    Intent goToImdb = new Intent(Intent.ACTION_VIEW);
                    goToImdb.setData(Uri.parse(cupboard().withDatabase(db).get(NewsItem.class, pos).getLink()));
                    startActivity(goToImdb);
                    return true;
                }
            });
            Log.d(TAG, "Fragment onCreateView end, Items in DB: " + String.valueOf(itemsInDb) + " In OLD DB: " + String.valueOf(itemsInDbOld));
            return rootView;
        }


        @Override
        public void onResume() {
            super.onResume();
        }

        public void updateUI() {
            // Add 5 items to dataList
            for (int i = 1; i < 6; i++) {
                int e = (pageNr - 1) * 5 + i;

                NewsItem item = cupboard().withDatabase(db).get(NewsItem.class, e);
                if (item != null) {
                    // Clear dataList before adding any data
                    if(i==1){
                        dataList.clear();
                    }
                    dataList.add(item.getTitle());
                    if (pageNr == 1) {
                        // If this fragment is first page fragment then add all the news that`s
                        // inserted to dataList also to db OLD.
                        itemsInDbOld = cupboard().withDatabase(dbOld).put(new OldNews(cupboard().withDatabase(db).get(NewsItem.class, i).getId()));
                        Log.d(TAG, "Fragment updateUI, adding to old db " + String.valueOf(i) + "    ID: " +
                                cupboard().withDatabase(db).get(NewsItem.class, i).getId() + " items in Old DB: " + String.valueOf(itemsInDbOld));
                    }
                } else {
                    break;
                }
            }
            //long endTime = System.nanoTime();
            //long duration = (endTime - startTime) / 1000000;
            //Log.d(TAG, "u8itime " + String.valueOf(duration));
            itemsAdapter.notifyDataSetChanged();
            Log.d(TAG, "Fragment updated, Items in DB: " + String.valueOf(itemsInDb) + " In OLD DB: " + String.valueOf(itemsInDbOld));
        }
    }

    // Implementation of AsyncTask used to download XML feed from stackoverflow.com.
    private class DownloadXmlTask extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... urls) {
            try {
                Log.d(TAG, "AsyncTask Background: " + (urls[0]));
                loadXmlFromNetwork(urls[0]);
            } catch (IOException e) {
                e.getStackTrace();
            } catch (XmlPullParserException e) {
                e.getStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            // Update all fragments that are created
            for (Object value : m1.values()) {
                PlaceholderFragment fragment = (PlaceholderFragment) value;
                fragment.updateUI();
            }

            // Change the pager adapter nr of page count
            int newPageNr = (int) Math.ceil(itemsInDb / 5.0);
            Log.d(TAG, "newPageNr " + String.valueOf(newPageNr));
            mSectionsPagerAdapter.setCount(newPageNr);

            // Triggers a redraw of the PageAdapter
            mSectionsPagerAdapter.notifyDataSetChanged();

            // Download more data if there's below 25 news in db
            if(itemsInDb < 25){
                //String URL3 = URL + "&count=25&after=" + lastId;
                String URL3 = URL + "?count=25&after=" + lastId;
                new DownloadXmlTask().execute(URL3);
            }
        }
    }


}
