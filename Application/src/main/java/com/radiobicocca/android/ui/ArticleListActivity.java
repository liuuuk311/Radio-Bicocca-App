package com.radiobicocca.android.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.radiobicocca.android.Model.Article;
import com.radiobicocca.android.Model.WPAuthor;
import com.radiobicocca.android.Adapter.EndlessRecyclerViewScrollListener;
import com.radiobicocca.android.Model.Media;
import com.radiobicocca.android.Adapter.MyAdapter;
import com.radiobicocca.android.Model.WPPost;
import com.radiobicocca.android.R;
import com.radiobicocca.android.Interface.RetrofitArrayAPI;

import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * An activity representing a list of Articles. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ArticleDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class ArticleListActivity extends AppCompatActivity {

    private static final String TAG = "LIST ACTIVITY";

    // Store a member variable for the listener
    private EndlessRecyclerViewScrollListener scrollListener;
    private static final String BASE_URL = "http://radiobicocca.it/";

    public static List<Article> list = new ArrayList<Article>();
    public int global_index = 0;
    public static Map<String, Article> ITEM_MAP = new HashMap<String, Article>();
    Article a;
    private MyAdapter adapter;



    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

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

        RecyclerView recyclerView = findViewById(R.id.article_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

//        adapter =  new MyAdapter(recyclerView, this, list, mTwoPane);

        recyclerView.setAdapter(adapter);

        if(list.size() == 0)
            loadNextDataFromApi(1);

        // Retain an instance so that you can call `resetState()` for fresh searches
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                loadNextDataFromApi(page);
            }
        };
        // Adds the scroll listener to RecyclerView
        recyclerView.addOnScrollListener(scrollListener);


    }

    // Append the next page of data into the adapter
    // This method probably sends out a network request and appends new data items to your adapter.
    public void loadNextDataFromApi(int offset) {
        // Send an API request to retrieve appropriate paginated data
        //  --> Send the request including an offset value (i.e `page`) as a query parameter.
        //  --> Deserialize and construct new model objects from the API response
        //  --> Append the new data objects to the existing set of items inside the array of items
        //  --> Notify the adapter of the new items made with `notifyItemRangeInserted()`
        Retrofit rf = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Log.d(TAG, "Offset page " + offset);

        RetrofitArrayAPI service = rf.create(RetrofitArrayAPI.class);
        Call<List<WPPost>> call = service.getPostInfo(offset);

        call.enqueue(new Callback<List<WPPost>>() {
            @Override
            public void onResponse(Call<List<WPPost>> call, retrofit2.Response<List<WPPost>> response) {
                for (int i = 0; i < response.body().size(); i++){
                    WPPost wpPost = response.body().get(i);
                    String id = String.valueOf(wpPost.getId());
                    String title = wpPost.getTitle().getRendered();
                    String summary = wpPost.getExcerpt().getRendered();
                    String content = wpPost.getContent().getRendered();
                    String date = wpPost.getDate();

                    title = android.text.Html.fromHtml(title).toString();
                    summary = android.text.Html.fromHtml(summary).toString();
                    content = android.text.Html.fromHtml(content).toString();


                    // Modifica il formato della data
                    String splitted[] = date.split("T");
                    date = splitted[0] + " " + splitted[1];
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yyyy");
                    try {
                        Date d = df.parse(date);
                        date = df2.format(d);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

//                    Integer authorId = wpPost.getAuthor();
//                    if (authorId != 0 && authorId != null)
//                        getAuthor(authorId, id, global_index);

//                    Bitmap img = null;
//                    Integer mediaId = wpPost.getFeaturedMedia();
//                    if (mediaId != 0 && mediaId != null)
//                        getMedia(mediaId, id, global_index);
//                    else
//                        img = BitmapFactory.decodeResource(getResources(), R.drawable.logo);

//                    Article a = new Article(id, title, summary, content,
//                            null, img, "", date, "");

                    // TODO: Rimuovere articoli inziali per evitare che la lista aumenti indefinitivamente
                    list.add(global_index,a);
                    global_index++;
                    ITEM_MAP.put(id, a);
                }
            }

            @Override
            public void onFailure(Call<List<WPPost>> call, Throwable t) {

            }
        });

    }

    private void getAuthor(Integer authorUrl, final String id, final int index) {
        Retrofit rf = new Retrofit.Builder()
                .baseUrl("http://radiobicocca.it/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitArrayAPI service = rf.create(RetrofitArrayAPI.class);
        Call<WPAuthor> call = service.getAuthor(authorUrl);

        call.enqueue(new Callback<WPAuthor>() {
            @Override
            public void onResponse(Call<WPAuthor> call, retrofit2.Response<WPAuthor> response) {
                if( response.body() != null) {
                    Article a = ITEM_MAP.get(id);
                    a.author = response.body().getName();
                    updateArticleContent(a, id, index);
                }
            }

            @Override
            public void onFailure(Call<WPAuthor> call, Throwable t) {

            }
        });

    }

    private void getMedia(Integer mediaUrl, final String id, final int index) {
        Retrofit rf = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitArrayAPI service = rf.create(RetrofitArrayAPI.class);
        Call<Media> call = service.getMedia(mediaUrl);

        call.enqueue(new Callback<Media>() {
            @Override
            public void onResponse(Call<Media> call, retrofit2.Response<Media> response) {

                if(response.body() != null) {
                    a = ITEM_MAP.get(id);
                    a.imgUrl = response.body().getMediaDetails().getSizes().getThumbnail().getSourceUrl();
                    a.bigImgUrl = response.body().getMediaDetails().getSizes().getFull().getSourceUrl();
                    updateArticleContent(a, id, index);
                    new DownloadImage().execute(a.imgUrl, id, String.valueOf(index));
                }
            }

            @Override
            public void onFailure(Call<Media> call, Throwable t) {

            }
        });
    }
    // DownloadImage AsyncTask
    private class DownloadImage extends AsyncTask<String, Void, Bitmap> {
        String id;
        int index;
        @Override
        protected Bitmap doInBackground(String... URL) {

            String imageURL = URL[0];
            id = URL[1];
            index = Integer.valueOf(URL[2]);
            Bitmap bitmap = null;
            BitmapFactory.Options bmOptions;
            bmOptions = new BitmapFactory.Options();
            bmOptions.inSampleSize = 2;
            try {
                // Download Image from URL
                InputStream input = new java.net.URL(imageURL).openStream();
                // Decode Bitmap
                bitmap = BitmapFactory.decodeStream(input, null, bmOptions);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            // Set the bitmap into list
            a = ITEM_MAP.get(id);
//            a.imgBitmap = result;
            updateArticleContent(a, id, index);
            adapter.notifyDataSetChanged();
        }
    }

    private void updateArticleContent(Article a, String id, int index){
        ITEM_MAP.put(id, a);
        list.remove(index);
        list.add(index, a);
    }


}

