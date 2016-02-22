package org.korjus.news;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

import java.util.HashMap;
import java.util.Map;

import io.fabric.sdk.android.Fabric;


/*Todo
5 seconds lag between doInBackground and onPostExecute.

menu
clean, rename, comment
public static -> private
move onOptionsItemSelected to new class

spinner arrow style
icon
disable back button?
minsdk to 10

Test different:
Timezones
Locations
Daylight times
Android versions

cd data/data/org.korjus.news/databases
*/


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "u8i9 MainActivity";
    public static Context context;
    public static Map m1 = new HashMap();
    public static String lastItemId;
    public static SectionsPagerAdapter SectionsAdapter;
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
        sessionInfo();

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
            // Delete Database's and other data.
            DatabaseHelper dbHelper = DatabaseHelper.getInstance(MainActivity.context);
            dbHelper.deleteAllData();

            lastItemId = null;
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
            alert();
        }

        if (id == R.id.menu_source){
            // Make new alert with Save and Cancel buttons.
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            // Get the layout inflater
            LayoutInflater inflater = MainActivity.this.getLayoutInflater();
            View mView = inflater.inflate(R.layout.dialog_source, null);

            // Set text to EditText
            final EditText etSearch = (EditText)mView.findViewById(R.id.etSource);
            final String base = "https://www.reddit.com";
            etSearch.setText(settings.getCustomSource().replace(base, ""));

            builder.setView(mView)
                    // Add action buttons
                    .setPositiveButton("Save",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    String mSearchText = etSearch.getText().toString();
                                    settings.setCustomSource(base + mSearchText);
                                            Log.d(TAG, settings.getCustomSource());
                                }
                            })
                    .setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    dialog.cancel();
                                }
                            });
            // return builder.create();
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void instantiateSpinner() {
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        // Create an ArrayAdapter using the string array and a simple spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getSupportActionBar().getThemedContext(),
                R.array.order, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        // Set spinner position
        spinner.setSelection(settings.getSpinnerPosition(), false);
        // Add on item select listener
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "instantiateSpinner on item selected, pos: " + String.valueOf(position));
                settings.setSpinnerPosition(position);
                settings.setCustomUrl(position);
                DatabaseHelper dbHelper = DatabaseHelper.getInstance(context);
                dbHelper.deleteNewsDb();
                refresh(); // todo reload ilma refreshita
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void instantiateViewPagerWithAdapters() {
        // Create the adapter that will return a fragment for each tab of the activity.
        SectionsAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter and on page change listener.
        ViewPager mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(SectionsAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int page) {
                int i = page * 5 + 1;
                DatabaseHelper dbHelper = DatabaseHelper.getInstance(MainActivity.context);
                // Add selected page news ID's to old db
                dbHelper.addFiveNewsToOld(i);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }



    private void sessionInfo() {
        // Update session start times
        if (clock.getIsNewSession())  {
            // Previous session start time
            settings.setLastSessionTime();
            settings.setCurrentSessionStartTime();
        }
        settings.setLastDownloadTime();

        // Update URL
        int spinnerPosition = settings.getSpinnerPosition();
        settings.setCustomUrl(spinnerPosition);

        // Make toast if "since last time" is selected
        if (spinnerPosition == 6) {
            Toast.makeText(this, clock.getStringFromMillis(clock.getLastSessionDifferenceMillis()), Toast.LENGTH_LONG).show();
        }
    }

    public void refresh() {
        // Start new Main Activity
        Intent goToHome = new Intent(context, MainActivity.class);
        startActivity(goToHome);
    }

    // Show instructions
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