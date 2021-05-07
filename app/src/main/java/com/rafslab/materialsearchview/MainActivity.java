package com.rafslab.materialsearchview;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.iammert.library.ui.multisearchviewlib.MultiSearchView;
import com.iammert.library.ui.multisearchviewlib.helper.KeyboardHelper;
import com.rafslab.materialsearchview.models.Data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MultiSearchView searchView;
    private List<Data> dataList;
    private RecyclerAdapter adapter;
    private MotionLayout motion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        motion.setTransition(R.id.search_motion);
        dataList = new ArrayList<>();
        adapter = new RecyclerAdapter(MainActivity.this, dataList, recyclerView);
        adapter.setPlayAnimation(false);
        getData();
        searchView.setSearchViewListener(new MultiSearchView.MultiSearchViewListener() {
            @Override
            public void onTextChanged(int index, CharSequence s) {

            }

            @Override
            public void onSearchComplete(int index, CharSequence s) {
                final List<Data> filteredItem = setFilterSingleQuery(dataList, s.toString());
                if (filteredItem.size() > 0){
                    adapter.setPlayAnimation(true);
                    adapter.setFilter(filteredItem);
                } else Toast.makeText(MainActivity.this, "No Results!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSearchItemRemoved(int index) {
            }

            @Override
            public void onItemSelected(int index, CharSequence s) {
                final List<Data> filteredItem = setFilterSingleQuery(dataList, s.toString());
                if (filteredItem.size() > 0){
                    adapter.setPlayAnimation(true);
                    adapter.setFilter(filteredItem);
                } else {
                    Toast.makeText(MainActivity.this, "No Results!", Toast.LENGTH_SHORT).show();
                    adapter.setFilter(dataList);
                }
            }

            @Override
            public void onSearchBehavior(boolean isFirst) {
                if (isFirst) {
                    motion.transitionToEnd();
                } else {
                    final List<Data> filteredItem = setFilterSingleQuery(dataList, "");
                    if (filteredItem.size() > 0){
                        adapter.setPlayAnimation(false);
                        adapter.setFilter(filteredItem);
                    }
                }
            }

            @Override
            public void onSearchTabIsEmpty() {
                motion.transitionToStart();
                KeyboardHelper.INSTANCE.hideKeyboard(MainActivity.this);
                final List<Data> filteredItem = setFilterSingleQuery(dataList, "");
                if (filteredItem.size() > 0){
                    adapter.setPlayAnimation(false);
                    adapter.setFilter(filteredItem);
                }
            }
        });

    }
    private List<Data> setFilterSingleQuery(List<Data> models, String query){
        query = query.toLowerCase();
        final List<Data> filteredList = new ArrayList<>();
        for (Data data : models){
            final String title = data.getDescription().toLowerCase();
            if (title.contains(query)) {
                filteredList.add(data);
            }
        }
        return filteredList;
    }

    private void initViews() {
        recyclerView = findViewById(R.id.list);
        searchView = findViewById(R.id.search_view);
        motion = findViewById(R.id.motion_base);
    }
    private void getData(){
        String URL = "https://raw.githubusercontent.com/rafslab/dl-drakor-test/master/Series/Korean/drama-all.json";
        AndroidNetworking.get(URL)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for (int i = 0; i<response.length(); i++){
                                JSONObject object = response.getJSONObject(i);
                                Data data = new Data();
                                data.setTitle(object.getString("title"));
                                data.setDescription(object.getString("categories"));
                                data.setProfile(object.getString("poster"));
                                dataList.add(data);
                                recyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this, 3));
                                recyclerView.setAdapter(adapter);
                            }
                        } catch (JSONException e){
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {

                    }
                });
    }
}