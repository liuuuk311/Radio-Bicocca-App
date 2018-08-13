package com.radiobicocca.android.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.liuguangqiang.swipeback.SwipeBackActivity;
import com.liuguangqiang.swipeback.SwipeBackLayout;
import com.radiobicocca.android.Common.Common;
import com.radiobicocca.android.Adapter.EndlessRecyclerViewScrollListener;
import com.radiobicocca.android.Common.ConnectionDetector;
import com.radiobicocca.android.Common.Utils;
import com.radiobicocca.android.Interface.BlogService;
import com.radiobicocca.android.Adapter.MyAdapter;
import com.radiobicocca.android.R;
import com.radiobicocca.android.Model.WPPost;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.text.Html.fromHtml;

public class BlogPostListActivity extends AppCompatActivity {
    private static final String TAG = "BLOG POST LIST";

    // Minuti dopo i quali cancellare la cache e riscaricare i post
    private static final int CACHE_TIME = 1440; // Un giorno
    // TODO sistemare la cache.. non funziona

    private BlogService mSerivce;
    private MyAdapter adapter;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeLayout;

    public static List<WPPost> list = new ArrayList<WPPost>();
    public static Map<String, WPPost> ITEM_MAP = new HashMap<String, WPPost>();

    // Store a member variable for the listener
    private EndlessRecyclerViewScrollListener scrollListener;

    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setTitle(getTitle());

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        // Per il caricamento
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        //Init Cache
        initCache();

        //Init Service
        mSerivce = Common.getBlogService();

        //Inti View
        swipeLayout = findViewById(R.id.swipe_layout);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                loadBlogPosts(true, 1);
            }
        });

        // Imposta il recyclerView e l'adapter
        recyclerView = findViewById(R.id.article_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter =  new MyAdapter(this, this, list, recyclerView);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                linearLayoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setAdapter(adapter);

        // Retain an instance so that you can call `resetState()` for fresh searches
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                Log.d(TAG, "Page " + page + " Tot " + totalItemsCount);
//                fetchNewData(page);

            }
        };
        recyclerView.addOnScrollListener(scrollListener); // Adds the scroll listener to RecyclerView

        // Carica i post
        loadBlogPostsWithCache(false, 1);

    }

    private void loadBlogPosts(boolean isRefreshed, int page){
        if(!isRefreshed){
            if (page == 1) {// solo se stiamo caricando i primi articoli
                progressBar.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }
            Log.d(TAG, "Fetching data");
            // Fetch new data
            fetchNewData(page);
        }else{
            recyclerView.setVisibility(View.GONE);
            recyclerView.getRecycledViewPool().clear();
            list.clear();
            // Fetch new data
            fetchNewData(1);
            swipeLayout.setRefreshing(false);
        }
    }


    private void loadBlogPostsWithCache(boolean isRefreshed, int page) {

        if(!isRefreshed){
            String cache = Paper.book().read("cache");

            if(cache != null && !cache.isEmpty()){ // Se possiamo leggere la cache
                WPPost[] list = new Gson().fromJson(cache, WPPost[].class);
                Log.d(TAG, "Loading Cache");
                for(int i=0; i<list.length;i++){
                    ITEM_MAP.put(list[i].getId().toString(), list[i]);
                }

                adapter = new MyAdapter(getBaseContext(), this, Arrays.asList(list), recyclerView);
                adapter.notifyDataSetChanged();
                recyclerView.setAdapter(adapter);
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
            else{ // Se non c'e la cache
                if (page == 1) {// solo se stiamo caricando i primi articoli
                    progressBar.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }
                Log.d(TAG, "Fetching data");
                // Fetch new data
                fetchNewData(page);

            }
        }
        else{ // if from swipe to refresh
            Log.d(TAG, "Refreshing");
            recyclerView.setVisibility(View.GONE);
            recyclerView.getRecycledViewPool().clear();
            list.clear();
            // Fetch new data
            fetchNewData(1);
            swipeLayout.setRefreshing(false);
        }
    }

    private void fetchNewData(int page) {
        Log.d(TAG, "Page: " + page);
        mSerivce.getPosts(page).enqueue(new Callback<List<WPPost>>() {
            @Override
            public void onResponse(Call<List<WPPost>> call, Response<List<WPPost>> response) {
                Log.d(TAG, response.toString());
                if(response.isSuccessful()){
                    findViewById(R.id.err_no_news).setVisibility(View.GONE);
                    fromWPPostToArticle(response);


                    Log.d(TAG, "Writing new Cache ok");
                    //Save to cache
                    Paper.book().delete("cache"); // delete old cache
                    Paper.book().delete("cache_time");
                    Paper.book().write("cache", new Gson().toJson(list));
                    Calendar calNow = Calendar.getInstance();
                    calNow.add(Calendar.MINUTE, CACHE_TIME);
                    Paper.book().write("cache_time", calNow.getTime());
                }
                else{
                    findViewById(R.id.err_no_news).setVisibility(View.VISIBLE);
                }

                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                // Dismiss refresh progressing
            }

            @Override
            public void onFailure(Call<List<WPPost>> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });

    }

    private void fromWPPostToArticle(Response<List<WPPost>> response){
        Log.d(TAG, "Response: " + response.toString());

        if(response.body() == null)
            return; // Errore

        for (int i = 0; i < response.body().size(); i++){
            WPPost wpPost = response.body().get(i);

            String title = wpPost.getTitle().getRendered();
            String summary = wpPost.getExcerpt().getRendered();
            String content = wpPost.getContent().getRendered();
            String date = wpPost.getDate();

            wpPost.getTitle().setRendered(fromHtml(title).toString());
            wpPost.getExcerpt().setRendered(fromHtml(summary).toString());
//            wpPost.getContent().setRendered(fromHtml(content).toString());


            // Modifica il formato della data
            wpPost.setDate(Utils.formatDate(date));

            Integer authorId = wpPost.getAuthor();

            // TODO: Rimuovere articoli inziali per evitare che la lista aumenti indefinitivamente
            list.add(wpPost);
            ITEM_MAP.put(wpPost.getId().toString(), wpPost);
        }


        recyclerView.getRecycledViewPool().clear();
        adapter.notifyDataSetChanged();

    }

    private void initCache(){
        Paper.init(this);
        Date cache_time = Paper.book().read("cache_time");

        Calendar calNow = Calendar.getInstance();
        if(cache_time == null){
            calNow.add(Calendar.MINUTE, CACHE_TIME);

            Paper.book().write("cache_time", calNow.getTime());
        }
        else{
            Calendar calOld = new GregorianCalendar();
            try {
                calOld.setTime(cache_time);
                if (calNow.after(calOld)){
                    // E' ora di aggiornare la cache
                    Paper.book().delete("cache");
                    Paper.book().delete("cache_time");
                    calNow.add(Calendar.MINUTE, CACHE_TIME);
                    Paper.book().write("cache_time", calNow.getTime());
                    Log.d(TAG, "Cache deleted");
                }
                else{
                    Log.d(TAG, "Loading old cache");
                }
            }
            catch (Exception e) {
                Log.e(TAG, "Errore" + e.getLocalizedMessage());
            }
        }

    }
}
