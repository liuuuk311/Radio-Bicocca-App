package com.radiobicocca.android.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.radiobicocca.android.Common.Common;
import com.radiobicocca.android.Interface.BlogPostImageService;
import com.radiobicocca.android.Interface.ILoadMore;
import com.radiobicocca.android.Model.Media;
import com.radiobicocca.android.Model.WPPost;
import com.radiobicocca.android.R;
import com.radiobicocca.android.ui.ArticleDetailActivity;
import com.radiobicocca.android.ui.BlogPostListActivity;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by lucap on 2/18/2018.
 */

public class MyAdapter
        extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_TYPE_ITEM = 0, VIEW_ITEM_LOADING = 1;
    ILoadMore loadMore;

    public boolean isLoading() {
        return isLoading;
    }

    private boolean isLoading;
    int visibleThreshold = 8;
    int lastVisibleItem, totalItemCount;

    private final BlogPostListActivity mParentActivity;
    private final List<WPPost> mValues;
    private Context context;

    private BlogPostImageService mService;


    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            WPPost item = (WPPost) view.getTag();

            Context context = view.getContext();
            Intent intent = new Intent(context, ArticleDetailActivity.class);
//            intent.putExtra(ArticleDetailFragment.ARG_ITEM_ID, String.valueOf(item.getId()));
            intent.putExtra(ArticleDetailActivity.ARG_ITEM_ID, String.valueOf(item.getId()));
            context.startActivity(intent);

        }
    };

    public MyAdapter(Context context,
                 BlogPostListActivity parent,
                 List<WPPost> items,
                 RecyclerView recyclerView) {
        mValues = items;
        mParentActivity = parent;
        this.context = context;

        mService = Common.getBlogPostImageService();

        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager)recyclerView.getLayoutManager();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();

                if(!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)){
                    if (loadMore != null){
                        loadMore.onLoadMore();
                    }
                }
                isLoading = true;
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        return mValues.get(position) == null ? VIEW_ITEM_LOADING: VIEW_TYPE_ITEM;
    }


    public void setLoadMore(ILoadMore loadMore) {
        this.loadMore = loadMore;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.article_list_content, parent, false);
        return new ItemViewHolder(view);
    }

    public void setLoaded() {
        isLoading = false;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        final ItemViewHolder itemViewHolder = (ItemViewHolder) holder;

        Integer fetured_media_id = Integer.valueOf(mValues.get(position).getFeaturedMedia());
        mService.getMedia(fetured_media_id)
                .enqueue(new Callback<Media>() {
                    @Override
                    public void onResponse(Call<Media> call, Response<Media> response) {
                        if(response.body() != null){
                            Transformation transformation = new RoundedTransformationBuilder()
                                    .borderColor(Color.WHITE)
                                    .borderWidthDp(1)
                                    .cornerRadiusDp(3)
                                    .oval(false)
                                    .build();

                            try{
                                Picasso.with(context)
                                        .load(response.body().getMediaDetails()
                                                .getSizes().getThumbnail().getSourceUrl())
                                        .centerCrop()
                                        .transform(transformation)
                                        .resize(itemViewHolder.mImageView.getMeasuredWidth(),
                                                itemViewHolder.mImageView.getMeasuredHeight())
                                        .into(itemViewHolder.mImageView);
                            }
                            catch (IllegalArgumentException e){
                                Log.e("MyAdapter", "W: "
                                                + itemViewHolder.mImageView.getMeasuredWidth() +
                                        " H: " + itemViewHolder.mImageView.getMeasuredHeight() +
                                        e.getLocalizedMessage());
                            }

                        }
                    }

                    @Override
                    public void onFailure(Call<Media> call, Throwable t) {

                    }
                });


        itemViewHolder.mIdView.setText(mValues.get(position).getTitle().getRendered());
        itemViewHolder.mContentView.setText(mValues.get(position).getExcerpt().getRendered());
        itemViewHolder.mDateView.setText(mValues.get(position).getDate());
        itemViewHolder.itemView.setTag(mValues.get(position));
        itemViewHolder.itemView.setOnClickListener(mOnClickListener);

    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView mIdView;
        TextView mContentView;
        TextView mDateView;
        ImageView mImageView;

        ItemViewHolder(View view) {
            super(view);
            mIdView = (TextView) view.findViewById(R.id.titleTextView);
            mContentView = (TextView) view.findViewById(R.id.detailsTextView);
            mDateView = (TextView) view.findViewById(R.id.dateTextView);
            mImageView = (ImageView) view.findViewById(R.id.thumbnailImageView);
        }
    }
}