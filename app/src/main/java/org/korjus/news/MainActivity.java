package org.korjus.news;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.HashMap;
import java.util.Map;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

/*Todo
rename somethings
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
    public static final String URL = "https://www.reddit.com/r/worldnews/.rss";
    private static final String URL2 = "https://www.reddit.com/r/worldnews/top/.rss?sort=top&t=all";
    public static long itemsInDb;
    public static long itemsInDbOld;
    public static SQLiteDatabase db;
    public static SQLiteDatabase dbOld;
    public static Context context;
    public static Map m1 = new HashMap();
    public static String lastItemId;
    public static SectionsPagerAdapter SectionsAdapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        context = this;

        // Create the adapter that will return a fragment for each tab of the activity.
        SectionsAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter and on page change listner.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(SectionsAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int page) {
                // Add selected page news items to old news database.
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

        // Instantiate Databases
        DatabaseHelperOld dbHelperOld = new DatabaseHelperOld(this);
        DatabaseHelper dbHelper = new DatabaseHelper(this);

        dbOld = dbHelperOld.getWritableDatabase();
        db = dbHelper.getWritableDatabase();

        // Download and parse data from URL
        new DownloadTask().execute(URL);

        // Set up Floating Action Button [temp]
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

        Log.d(TAG, "MainActivity onCreate end, items in DB: " + String.valueOf(itemsInDb) +
                " In OLD DB: " + String.valueOf(itemsInDbOld));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here.
        int id = item.getItemId();

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

}
