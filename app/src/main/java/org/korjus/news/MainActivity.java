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
import android.support.v4.app.FragmentManager;
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
clean code
comment
get page 2 rss*/

// cd data/data/org.korjus.news/databases
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "u8i9 Main";
    private static final String URL = "https://www.reddit.com/r/worldnews/.rss";
    public static long itemsInDb;
    public static SQLiteDatabase db;
    public static SQLiteDatabase dbOld;
    public static Context context;
    public static Map m1 = new HashMap();

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

        // Create the adapter that will return a fragment for each of the five
        // primary sections of the activity.
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
                for (int i = 1; i < 6; i++) {
                    int e = page * 5 + i;
                    Log.d(TAG, String.valueOf(e));
                    OldNews oldNews = new OldNews(cupboard().withDatabase(db).get(NewsItem.class, e).getId());
                    cupboard().withDatabase(dbOld).put(oldNews);
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

                // Start Main Activity
                Intent goToHome = new Intent(context, MainActivity.class);
                startActivity(goToHome);
            }
        });
    }

    // Uploads XML from reddit, parses it, and combines it with
    // HTML markup. Returns HTML string.
    private String loadXmlFromNetwork(String urlString) throws XmlPullParserException, IOException {
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

        return "wtf";
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
            deleteDatabase(DatabaseHelperOld.DATABASE_NAME);

            // Close and delete Database so It could be recreated
            db.close();
            deleteDatabase(DatabaseHelper.DATABASE_NAME);

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
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            pageNr = getArguments().getInt(ARG_SECTION_NUMBER);

            dataList = new ArrayList<>();

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

            return rootView;
        }


        @Override
        public void onResume() {
            super.onResume();
        }

        public void updateUI() {
            for (int i = 1; i < 6; i++) {
                int e = (pageNr - 1) * 5 + i;

                NewsItem item = cupboard().withDatabase(db).get(NewsItem.class, e);
                if (item != null) {
                    dataList.add(item.getTitle());
                    if (pageNr == 1) {
                        Log.d(TAG, String.valueOf(i));
                        cupboard().withDatabase(dbOld).put(new OldNews(cupboard().withDatabase(db).get(NewsItem.class, i).getId()));
                    }
                } else {
                    break;
                }
            }
            itemsAdapter.notifyDataSetChanged();
        }

    }

    // Implementation of AsyncTask used to download XML feed from stackoverflow.com.
    private class DownloadXmlTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {
                return loadXmlFromNetwork(urls[0]);
            } catch (IOException e) {
                return "error";
            } catch (XmlPullParserException e) {
                return "error";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            for (Object value : m1.values()) {
                PlaceholderFragment fragment = (PlaceholderFragment) value;
                fragment.updateUI();
            }
            int newPageNr = (int) Math.ceil(itemsInDb / 5);
            Log.d(TAG, "newPageNr " + String.valueOf(newPageNr));
            // Change the count back to the initial count
            mSectionsPagerAdapter.setCount(newPageNr);
            // Triggers a redraw of the PageAdapter
            mSectionsPagerAdapter.notifyDataSetChanged();
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        private int _count = 5;


        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        public void setCount(int count) {
            this._count = count;
        }

        @Override
        public int getCount() {
            // Show 5 total pages.
            return this._count;
        }

    }
}
