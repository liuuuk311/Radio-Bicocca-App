package com.radiobicocca.android.ui;

import android.content.Intent;
import android.os.Bundle;

import android.support.v7.widget.Toolbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.liuguangqiang.swipeback.SwipeBackLayout;
import com.radiobicocca.android.Common.Common;
import com.radiobicocca.android.Interface.BlogPostImageService;
import com.radiobicocca.android.Model.Media;
import com.radiobicocca.android.Model.WPPost;
import com.radiobicocca.android.R;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * An activity representing a single Article detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link ArticleListActivity}.
 */
public class ArticleDetailActivity extends AppCompatActivity {


    BlogPostImageService mService;


    public static final String ARG_ITEM_ID = "item_id";
    private WPPost mItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_article_detail);
        setContentView(R.layout.new_article_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);

        setSupportActionBar(toolbar);

        mItem = BlogPostListActivity.ITEM_MAP.get(
                getIntent().getStringExtra(ArticleDetailActivity.ARG_ITEM_ID));

        final String articleLink = mItem.getLink();

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent sendIntent = new Intent();
//                sendIntent.setAction(Intent.ACTION_SEND);
//                sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_article_string)
//                        + "\n\n" + articleLink);
//                sendIntent.setType("text/plain");
//                startActivity(sendIntent);
//            }
//        });

        ((TextView)findViewById(R.id.article_title)).setText(mItem.getTitle().getRendered());
//        ((TextView)findViewById(R.id.article_text)).setText(mItem.getContent().getRendered());

        ((WebView) findViewById(R.id.article_text)).loadData(mItem.getContent().getRendered(),
                "text/html", null);

        mService = Common.getBlogPostImageService();

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }



        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            final ImageView imageView = (ImageView) findViewById(R.id.articleImageView);

            Integer featuredMedia  = BlogPostListActivity.ITEM_MAP.get(
                    getIntent().getStringExtra(ArticleDetailFragment.ARG_ITEM_ID))
                    .getFeaturedMedia();

            if(featuredMedia != 0) {
                mService.getMedia(featuredMedia).enqueue(new Callback<Media>() {
                    @Override
                    public void onResponse(Call<Media> call, Response<Media> response) {
                        if(response.body() != null) {
                            Picasso.with(getBaseContext())
                                    .load(response.body().getSourceUrl())
                                    .into(imageView);
                        }
                    }

                    @Override
                    public void onFailure(Call<Media> call, Throwable t) {

                    }
                });
            }
            else{
                Picasso.with(getBaseContext())
                        .load(R.drawable.logo)
                        .into(imageView);
            }



//            Bundle arguments = new Bundle();
//            arguments.putString(ArticleDetailFragment.ARG_ITEM_ID,
//                    getIntent().getStringExtra(ArticleDetailFragment.ARG_ITEM_ID));
//            ArticleDetailFragment fragment = new ArticleDetailFragment();
//            fragment.setArguments(arguments);
//            getSupportFragmentManager().beginTransaction()
//                    .add(R.id.article_detail_container, fragment)
//                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            navigateUpTo(new Intent(this, BlogPostListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
