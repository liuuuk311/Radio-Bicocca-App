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

/**
 * Created by lucap on 2/23/2018.
 */

public class ProgrammiAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private static List<Programma> programmaList = new ArrayList<Programma>();


    private Context context;

    public ProgrammiAdapter(Context context, List<Programma> programmaList) {
        this.context = context;
        this.programmaList = programmaList;

        Log.d("ADAPTER Size:", " " + programmaList.size());
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.programmi_list_content, parent, false);
        return new ProgrammiAdapter.ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final ProgrammiAdapter.ItemViewHolder itemViewHolder = (ProgrammiAdapter.ItemViewHolder) holder;

//        try{
//            Picasso.with(context)
//                    .load(programmaList.get(position).getImageResource())
//                    .centerCrop()
//                    .resize(640, 640)
//                    .into(itemViewHolder.mImageView);
//        }
//        catch (IllegalArgumentException e){
//            Log.e("MyAdapterProg", "W: "
//                    + itemViewHolder.mImageView.getMeasuredWidth() +
//                    " H: " + itemViewHolder.mImageView.getMeasuredHeight() +
//                    e.getLocalizedMessage());
//        }

        itemViewHolder.mProgrmmaTitle.setText(programmaList.get(position).getTitlo());
        itemViewHolder.mHostText.setText(programmaList.get(position).getHostString());
        itemViewHolder.mDayText.setText(programmaList.get(position).getDayString());
        itemViewHolder.mTimeText.setText(programmaList.get(position).getTimeString());
//        itemViewHolder.mImageView.setImageResource();


    }

    @Override
    public int getItemCount() {
        return programmaList.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView mProgrmmaTitle;
        TextView mHostText;
        TextView mDayText;
        TextView mTimeText;
//        ImageView mImageView;

        ItemViewHolder(View view) {
            super(view);
            mProgrmmaTitle = view.findViewById(R.id.name_text);
            mHostText = view.findViewById(R.id.desc_text);
            mDayText = view.findViewById(R.id.day_text);
            mTimeText = view.findViewById(R.id.time_text);
//            mImageView =  view.findViewById(R.id.profile_pic);
        }
    }

    public static class Programma {
        String titlo;
        String hostString;
        String dayString;
        String timeString;
        String shareString;
        String imageResource;

        public Programma(String titlo, String hostString, String dayString, String timeString, String shareString, String imageResource) {
            this.titlo = titlo;
            this.hostString = hostString;
            this.dayString = dayString;
            this.timeString = timeString;
            this.shareString = shareString;

            this.imageResource = imageResource;
        }

        public String getShareString() {
            return shareString;
        }

        public void setShareString(String shareString) {
            this.shareString = shareString;
        }

        public String getTitlo() {
            return titlo;
        }

        public void setTitlo(String titlo) {
            this.titlo = titlo;
        }

        public String getHostString() {
            return hostString;
        }

        public void setHostString(String hostString) {
            this.hostString = hostString;
        }

        public String getDayString() {
            return dayString;
        }

        public void setDayString(String dayString) {
            this.dayString = dayString;
        }

        public String getTimeString() {
            return timeString;
        }

        public void setTimeString(String timeString) {
            this.timeString = timeString;
        }

        public String getImageResource() {
            return imageResource;
        }

        public void setImageResource(String imageResource) {
            this.imageResource = imageResource;
        }
    }
}
