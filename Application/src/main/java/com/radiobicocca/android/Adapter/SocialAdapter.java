package com.radiobicocca.android.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.radiobicocca.android.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lucap on 3/20/2018.
 */

public class SocialAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Context context;
    private static List<Social> socialList;

    private class Social{
        private int logo;
        private String name;
        private String url;

        public Social(int logo, String name, String url) {
            this.logo = logo;
            this.name = name;
            this.url = url;
        }

        public int getLogo() {
            return logo;
        }

        public void setLogo(int logo) {
            this.logo = logo;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }



    public SocialAdapter(Context context, List<ProgrammiAdapter.Programma> programmaList) {
        this.context = context;
        socialList = new ArrayList<Social>();

        socialList.add(new Social(R.mipmap.ic_facebook,
                                "Facebook",
                                context.getString(R.string.url_facebook)));


    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.programmi_list_content, parent, false);
        return new SocialAdapter.ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final ProgrammiAdapter.ItemViewHolder itemViewHolder = (ProgrammiAdapter.ItemViewHolder) holder;

        itemViewHolder.mProgrmmaTitle.setText(socialList.get(position).getName());

    }

    @Override
    public int getItemCount() {
        return socialList.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView mProgrmmaTitle;
        TextView mHostText;
        TextView mDayText;
        TextView mTimeText;
        ImageView mImageView;

        ItemViewHolder(View view) {
            super(view);
            mProgrmmaTitle = view.findViewById(R.id.name_text);
            mHostText = view.findViewById(R.id.desc_text);
            mDayText = view.findViewById(R.id.role_text);
            mTimeText = view.findViewById(R.id.time_text);
            mImageView =  view.findViewById(R.id.profile_pic);
        }
    }

}
