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

    class HiNewsViewHolder extends RecyclerView.ViewHolder {
        private TextView titleTextView;
        private TextView descriptionTextView;
        private TextView pubDateTextView;
        private ImageView imageView;
        private TextView creatorTextView;

        HiNewsViewHolder(View view) {
            super(view);
            titleTextView = view.findViewById(R.id.title_news);
            descriptionTextView = view.findViewById(R.id.description_news);
            pubDateTextView = view.findViewById(R.id.pubdate_news);
            imageView = view.findViewById(R.id.image_news);
            creatorTextView = view.findViewById(R.id.creator_news);
        }

        void bind(RssItem item, int position) {
            titleTextView.setText(item.getTitle());
            descriptionTextView.setText(Html.fromHtml(item.getDescription(), 0));
            pubDateTextView.setText(item.getPublishDate().toString());
            creatorTextView.setText(item.getCreator());
            itemView.setOnClickListener(view -> AboutActivity.start(context, position, pageNumber));
            Glide.with(itemView.getContext())
                    .load(item.getPreviewImagePath())
                    .centerCrop()
                    .into(imageView);
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
        holder.bind(list.get(position), position);
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }
}