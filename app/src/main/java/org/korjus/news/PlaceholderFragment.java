package org.korjus.news;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;


public class PlaceholderFragment extends Fragment {
    private static final String TAG = "u8i9 Fragment";
    private static final String ARG_SECTION_NUMBER = "section_number";
    ArrayAdapter<String> itemsAdapter;
    List<String> dataList;
    int pageNr;
    SwipeRefreshLayout swipeContainer;
    MainActivity mainActivity;
    DatabaseHelper dbHelper;

    public PlaceholderFragment() {
    }

    // Returns a new instance of this fragment for the given section number.
    public static PlaceholderFragment newInstance(int sectionNumber) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        MainActivity.m1.put(sectionNumber, fragment);
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) MainActivity.context;
        dbHelper = DatabaseHelper.getInstance(mainActivity);
        pageNr = getArguments().getInt(ARG_SECTION_NUMBER);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        addDataToDataList();
        instantiateListview(rootView);
        pullToRefresh(rootView);

        return rootView;
    }

    // Add 5 news items to dataList if they are available
    private void addDataToDataList() {
        int pos = (pageNr - 1) * 5 + 1;
        dataList = dbHelper.getNewsTitles(pos);

    }

    private void instantiateListview(View rootView){
        itemsAdapter = new ArrayAdapter<>(getContext(), R.layout.list_item, dataList);
        ListView listView = (ListView) rootView.findViewById(R.id.listView);
        listView.setAdapter(itemsAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int pos = (pageNr - 1) * 5 + position + 1;
                Intent goToImdb = new Intent(Intent.ACTION_VIEW);
                goToImdb.setData(Uri.parse(dbHelper.getNewsContent(pos)));
                startActivity(goToImdb);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                int pos = (pageNr - 1) * 5 + position + 1;
                Intent goToImdb = new Intent(Intent.ACTION_VIEW);
                Uri uri = Uri.parse(dbHelper.getNewsComments(pos));
                goToImdb.setData(uri);
                startActivity(goToImdb);
                return true;
            }
        });
    }

    private void pullToRefresh(View rootView){
        // Lookup the swipe container view
        swipeContainer = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                dbHelper.deleteNewsDb();

                UserSettings settings = new UserSettings();
                if (settings.getSpinnerPosition() == 6){
                    settings.setCustomUrl(6);
                    //settings.setCustomUrlWithDif();
                }
                mainActivity.refresh();
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    public void updateUI() {
        Log.d(TAG, "updateUI page nr: " + String.valueOf(pageNr));
        int a = (pageNr - 1) * 5 + 1;

        dataList.clear();
        dataList.addAll(dbHelper.getNewsTitles(a));
        itemsAdapter.notifyDataSetChanged();
    }
}
