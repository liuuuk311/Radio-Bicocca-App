package com.radiobicocca.android.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.radiobicocca.android.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class TeamAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private List<Person> teamList = new ArrayList<Person>();
    private Context context;

    public TeamAdapter(Context context, List<Person> teamList) {
        this.teamList = teamList;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.team_list_content, parent, false);
        return new TeamAdapter.ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final TeamAdapter.ItemViewHolder itemViewHolder = (TeamAdapter.ItemViewHolder) holder;

        try{
            Picasso.with(context)
                    .load(teamList.get(position).getUrlImg())
                    .into(itemViewHolder.mImageView);
        }
        catch (IllegalArgumentException e){
            Log.e("MyAdapterProg", "W: "
                    + itemViewHolder.mImageView.getMeasuredWidth() +
                    " H: " + itemViewHolder.mImageView.getMeasuredHeight() +
                    e.getLocalizedMessage());
        }

        itemViewHolder.mName.setText(teamList.get(position).getName());
        itemViewHolder.mRole.setText(teamList.get(position).getRole());
        itemViewHolder.mDesc.setText(teamList.get(position).getDesc());
    }

    @Override
    public int getItemCount() {
        return teamList.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView mName;
        TextView mRole;
        TextView mDesc;
        ImageView mImageView;

        ItemViewHolder(View view) {
            super(view);
            mName = view.findViewById(R.id.name_text);
            mRole = view.findViewById(R.id.role_text);
            mDesc = view.findViewById(R.id.desc_text);
            mImageView =  view.findViewById(R.id.profile_pic);
        }
    }

    public static class Person{
        private String name;
        private String desc;
        private String urlImg;
        private String role;

        public Person(String name, String desc, String urlImg, String role) {
            this.name = name;
            this.desc = desc;
            this.urlImg = urlImg;
            this.role = role;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public String getUrlImg() {
            return urlImg;
        }

        public void setUrlImg(String urlImg) {
            this.urlImg = urlImg;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }
    }
}
