package org.korjus.news;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;



public class PlaceholderFragment extends Fragment {
    private static final String TAG = "u8i9 Fragment";
    private static final String ARG_SECTION_NUMBER = "section_number";
    ArrayAdapter<String> itemsAdapter;
    ArrayList<String> dataList;
    int pageNr;
    SwipeRefreshLayout swipeContainer;
    MainActivity mainActivity;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        pageNr = getArguments().getInt(ARG_SECTION_NUMBER);

        // Add 5 news items to dataList if they are available
        addDataToDataList();
        instantiateListview(rootView);
        pullToRefresh(rootView);

        return rootView;
    }

    private void addDataToDataList() {
        dataList = new ArrayList<>();
        for (int i = 1; i < 6; i++) {
            int e = (pageNr - 1) * 5 + i;

            NewsItem item = cupboard().withDatabase(MainActivity.db).get(NewsItem.class, e);
            if (item != null) {
                dataList.add(item.getTitle());
            } else {
                break;
            }
        }
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
                goToImdb.setData(Uri.parse(cupboard().withDatabase(MainActivity.db).get(NewsItem.class, pos).getContent()));
                startActivity(goToImdb);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                int pos = (pageNr - 1) * 5 + position + 1;
                Intent goToImdb = new Intent(Intent.ACTION_VIEW);
                NewsItem item = cupboard().withDatabase(MainActivity.db).get(NewsItem.class, pos);
                Uri uri = Uri.parse(item.getCommentsLink());
                goToImdb.setData(uri);
                startActivity(goToImdb);
                return true;
            }
        });
        //Log.d(TAG, "Fragment onCreateView end, Items in DB: " + String.valueOf(MainActivity.itemsInDb) + " In OLD DB: " + String.valueOf(MainActivity.itemsInDbOld));

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
        // Add 5 items to dataList
        for (int i = 1; i < 6; i++) {
            int e = (pageNr - 1) * 5 + i;

            NewsItem item = cupboard().withDatabase(MainActivity.db).get(NewsItem.class, e);
            if (item != null) {
                // Clear dataList before adding any data
                if (i == 1) {
                    dataList.clear();
                }
                dataList.add(item.getTitle());
                if (pageNr == 1) {
                    for (int r = 0; r < 100; r++){
                        // If this fragment is first page fragment then add all the news that`s
                        // inserted to dataList also to db OLD.
                        DatabaseBlockedHelper.itemsInDb = cupboard().withDatabase(MainActivity.dbOld).put(new OldNews(cupboard().withDatabase(MainActivity.db).get(NewsItem.class, i).getId()));
                        // Log.d(TAG, "Fragment updateUI, adding to old db " + String.valueOf(i) + "    ID: " + cupboard().withDatabase(MainActivity.db).get(NewsItem.class, i).getId() + " items in Old DB: " + String.valueOf(MainActivity.itemsInDbOld));
                    }
                    }
            } else {
                break;
            }
        }
        itemsAdapter.notifyDataSetChanged();
        // Log.d(TAG, "Fragment updated, Items in DB: " + String.valueOf(MainActivity.itemsInDb) + " In OLD DB: " + String.valueOf(MainActivity.itemsInDbOld));
    }
}
