package com.hinews.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hinews.R;
import com.hinews.activity.AboutActivity;
import com.hinews.item.RssItem;

import java.util.List;

public class NewsRecyclerViewAdapter extends RecyclerView.Adapter<NewsRecyclerViewAdapter.HiNewsViewHolder> {
    private List<RssItem> list;
    private Context context;
    private int pageNumber;

    static class HiNewsViewHolder extends RecyclerView.ViewHolder {
        private TextView titleTextView;
        private TextView descriptionTextView;
        private TextView pubDateTextView;
        private ImageView imageView;
        private TextView creatorView;

        HiNewsViewHolder(View view) {
            super(view);
            titleTextView = view.findViewById(R.id.title_news);
            descriptionTextView = view.findViewById(R.id.description_news);
            pubDateTextView = view.findViewById(R.id.pubdate_news);
            imageView = view.findViewById(R.id.image_news);
            creatorView = view.findViewById(R.id.creator_news);
        }
    }

    public NewsRecyclerViewAdapter(List<RssItem> list, Context context, int pageNumber) {
        this.context = context;
        this.list = list;
        this.pageNumber = pageNumber;
    }

    @NonNull
    @Override
    public HiNewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_news, parent, false);
        return new HiNewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HiNewsViewHolder holder, int position) {
        RssItem item = list.get(position);
        holder.titleTextView.setText(item.getTitle());
        holder.descriptionTextView.setText(Html.fromHtml(item.getDescription(), 0));
        holder.pubDateTextView.setText(item.getPublishDate().toString());
        holder.creatorView.setText(item.getCreator());

        Glide.with(holder.itemView.getContext())
                .load(item.getImage())
                .centerCrop()
                .into(holder.imageView);

        holder.itemView.setOnClickListener(view -> AboutActivity.start(context, position, pageNumber));
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }
}
