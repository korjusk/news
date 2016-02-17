package org.korjus.news;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

import java.util.HashMap;
import java.util.Map;

import io.fabric.sdk.android.Fabric;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

/*Todo
oldDb has duplicated values
adding items to db is too slow
OldNews class in unnecessary

rename
clean code
comment
public static -> private

spinner arrow style
support for older versions
support for other timezones
icon

cd data/data/org.korjus.news/databases
*/


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "u8i9 MainActivity";
    public static SQLiteDatabase db;
    public static SQLiteDatabase dbOld;
    public static Context context;
    public static Map m1 = new HashMap();
    public static String lastItemId;
    public static SectionsPagerAdapter SectionsAdapter;
    public static boolean shouldSave;
    private UserSettings settings;
    private Clock clock;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        context = this;
        settings = new UserSettings();
        clock = new Clock();

        instantiateSpinner();
        instantiateViewPagerWithAdapters();
        restartDatabases();
        sessionInfo();

        if (settings.getSpinnerPosition() == 6) {
            settings.setCustomUrl(6);
            Toast.makeText(this, clock.getStringFromMillis(clock.getLastSessionDifferenceMillis()), Toast.LENGTH_LONG).show();
        }

        // Download and parse data from urlCustom
        new DownloadTask().execute(settings.getCustomUrl());
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

            deleteDatabase(DatabaseBlockedHelper.DATABASE_NAME);
            deleteDatabase(DatabaseHelper.DATABASE_NAME);

            DatabaseBlockedHelper.itemsInDb = 0l;
            DatabaseHelper.itemsInDb = 1l;

            lastItemId = null;

            UserSettings settings = new UserSettings();
            settings.deleteAll();

            refresh();
            return true;
        }

        if (id == R.id.menu_update) {
            // User can manually check if there is updates in dropbox folder
            // This will be removed when the app becomes available in play store
            String updateUrl =
                    "https://www.dropbox.com/sh/6afaza65f37mlze/AADXVimhKAZDzw7d9Fc_QTuXa?dl=0";
            Intent checkUpdates = new Intent(Intent.ACTION_VIEW);
            checkUpdates.setData(Uri.parse(updateUrl));
            startActivity(checkUpdates);
        }

        if (id == R.id.menu_help) {
            Log.d(TAG, "Help!!");
            alert();
        }

        return super.onOptionsItemSelected(item);
    }

    private void instantiateSpinner() {
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getSupportActionBar().getThemedContext(),
                R.array.order, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setSelection(settings.getSpinnerPosition(), false);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "instantiateSpinner on item selected, pos: " + String.valueOf(position));
                settings.setSpinnerPosition(position);
                settings.setCustomUrl(position);
                refresh();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void instantiateViewPagerWithAdapters() {
        // Create the adapter that will return a fragment for each tab of the activity.
        SectionsAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter and on page change listner.
        ViewPager mViewPager = (ViewPager) findViewById(R.id.container);
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

                    NewsItem newsItem = cupboard().withDatabase(db).get(NewsItem.class, e);
                    if (null != newsItem) {
                        // Convert news to blocked news and add that to old news db
                        OldNews oldNews = new OldNews(newsItem.getId());
                        DatabaseBlockedHelper.itemsInDb = cupboard().withDatabase(dbOld).put(oldNews);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    private void restartDatabases(){
        if (null != db){
            // Close and delete Database so It could be recreated
            db.close();
            deleteDatabase(DatabaseHelper.DATABASE_NAME);
        }

        DatabaseBlockedHelper dbHelperOld = new DatabaseBlockedHelper(this);
        DatabaseHelper dbHelper = new DatabaseHelper(this);

        dbOld = dbHelperOld.getWritableDatabase();
        db = dbHelper.getWritableDatabase();

        DatabaseHelper.itemsInDb = 1l;
    }

    private void sessionInfo() {
        if (clock.getIsNewSession())  {

            // Previous session start time
            settings.setLastSessionTime();

            settings.setCurrentSessionStartTime();
        }

        settings.setLastDownloadTime();
    }
    
    public void refresh() {
        // Start new Main Activity
        Intent goToHome = new Intent(context, MainActivity.class);
        startActivity(goToHome);
    }

    public void alert() {
        String msg = "Swipe left to see next news.\n" +
                "Pull down to refresh.\n" +
                "Long click to open news comments.\n" +
                "Click app title to change sorting order";

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        alertDialogBuilder.setMessage(msg).setCancelable(true);

        AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.show();
    }
}