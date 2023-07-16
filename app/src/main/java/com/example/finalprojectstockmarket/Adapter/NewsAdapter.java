package com.example.finalprojectstockmarket.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import com.example.finalprojectstockmarket.Model.News;
import com.example.finalprojectstockmarket.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(News newsItem, int position);
    }

    private final List<News> newsList;
    private final OnItemClickListener clickListener;


    private final Context context;


    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvNewsSrc;
        private final ImageView ivNewsImg;
        private final TextView tvLastUpdated;
        private final TextView tvNewsTitle;
        final View view;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            tvNewsSrc =  view.findViewById(R.id.tv_news_src);
            ivNewsImg =  view.findViewById(R.id.iv_news_img);
            tvNewsTitle =  view.findViewById(R.id.tv_news_title);
            tvLastUpdated =  view.findViewById(R.id.tv_news_last_updated);
        }

        public TextView getTvNewsSrc() {
            return tvNewsSrc;
        }

        public TextView getTvLastUpdated() {
            return tvLastUpdated;
        }

        public TextView getTvNewsTitle() {
            return tvNewsTitle;
        }

        public ImageView getIvNewsImg() {
            return ivNewsImg;
        }
    }


    public NewsAdapter(List<News> newsList, Context context, OnItemClickListener clickListener) {

        this.newsList = newsList;
        this.context = context;
        this.clickListener = clickListener;

    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.news_item, viewGroup, false);

        if (viewType == 0){
            view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.first_news, viewGroup, false);
        }


        return new ViewHolder(view);
    }



    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        final News news = newsList.get(position);

        viewHolder.getTvLastUpdated().setText(news.lastUpdated);
        viewHolder.getTvNewsTitle().setText(news.title);
        viewHolder.getTvNewsSrc().setText(news.src);

        ImageView ivNewsImg = (ImageView)  viewHolder.getIvNewsImg();

        if (viewHolder.getItemViewType() != 0){
            Picasso.with(context).load(news.img).resize(400, 400).into(ivNewsImg);
        }else{
            Picasso.with(context).load(news.img).resize(1500, 0).into(ivNewsImg);
        }

        viewHolder.view.setOnClickListener(v ->
                clickListener.onItemClick(news, viewHolder.getAdapterPosition()));



    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

}
