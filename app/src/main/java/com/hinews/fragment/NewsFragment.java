package com.hinews.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hinews.R;
import com.hinews.adapter.NewsRecyclerViewAdapter;
import com.hinews.item.RssItem;
import com.hinews.manager.NewsManager;
import com.hinews.parsing.RssListPageSorter;

import java.util.List;

public class NewsFragment extends Fragment {
    public static final String EXTRA_POSITION = "item_position";
    public static final String EXTRA_MAIN_PAGE_NUMBER = "page_number";
    private int pageNumber;

    public static NewsFragment newInstance(int position) {
        Bundle args = new Bundle();
        args.putInt(EXTRA_POSITION, position);
        NewsFragment fragment = new NewsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageNumber = getArguments().getInt(EXTRA_POSITION);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment, null);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        NewsManager instance = NewsManager.getInstance();
        List<RssItem> rssItems = RssListPageSorter.sort(instance.getRssItems(), pageNumber);
        NewsRecyclerViewAdapter mHiNewsRecyclerViewAdapter = new NewsRecyclerViewAdapter(rssItems, getContext(), pageNumber);
        recyclerView.setAdapter(mHiNewsRecyclerViewAdapter);
        return view;
    }
}
