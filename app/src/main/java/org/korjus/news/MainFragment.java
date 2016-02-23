package org.korjus.news;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainFragment extends Fragment {
    private static final String TAG = "u8i9 Fragment";
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static Map<Integer, MainFragment> mapFragments = new HashMap<>();
    private ArrayAdapter<String> itemsAdapter;
    private List<String> dataList;
    private int pageNr;
    private SwipeRefreshLayout swipeContainer;
    private MainActivity mainActivity;
    private DatabaseHelper dbHelper;

    public MainFragment() {
    }

    // Returns a new instance of this fragment for the given section number.
    public static MainFragment newInstance(int sectionNumber) {
        MainFragment fragment = new MainFragment();
        mapFragments.put(sectionNumber, fragment);
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public static void updateAllFragments() {
        // Update all fragments that are created
        for (Object value : mapFragments.values()) {
            MainFragment fragment = (MainFragment) value;
            fragment.updateUI();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) MainActivity.getContext();
        dbHelper = DatabaseHelper.getInstance(mainActivity);
        pageNr = getArguments().getInt(ARG_SECTION_NUMBER);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        addDataToDataList();
        instantiateListView(rootView);
        pullToRefresh(rootView);

        return rootView;
    }

    // Add 5 news items to dataList if they are available
    private void addDataToDataList() {
        int pos = (pageNr - 1) * 5 + 1;
        dataList = dbHelper.getNewsTitles(pos);

    }

    private void instantiateListView(View rootView) {
        itemsAdapter = new ArrayAdapter<>(getContext(), R.layout.list_item, dataList);
        ListView listView = (ListView) rootView.findViewById(R.id.listView);
        listView.setAdapter(itemsAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int pos = (pageNr - 1) * 5 + position + 1;

                String url = dbHelper.getNewsContent(pos);
                if (url == null) {
                    Log.d(TAG, "Url was null. Selecting comments url.");
                    url = dbHelper.getNewsComments(pos);
                }

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                int pos = (pageNr - 1) * 5 + position + 1;
                Uri uri = Uri.parse(dbHelper.getNewsComments(pos));

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(uri);
                startActivity(intent);
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
                mainActivity.refresh();
            }
        });
        // Configure the refreshing colors
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                    android.R.color.holo_green_light,
                    android.R.color.holo_orange_light,
                    android.R.color.holo_red_light);
        }
    }

    public void updateUI() {
        int a = (pageNr - 1) * 5 + 1;

        dataList.clear();
        dataList.addAll(dbHelper.getNewsTitles(a));
        itemsAdapter.notifyDataSetChanged();

        // Disables pull to refresh icon
        swipeContainer.setRefreshing(false);
    }
}
