package com.hinews.manager;

import android.support.annotation.NonNull;
import android.util.SparseArray;

import com.hinews.converter.RssConverterFactory;
import com.hinews.item.RssFeed;
import com.hinews.item.RssItem;
import com.hinews.service.RssService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class NewsManager {
    private static final String BASE_URL = "https://hi-news.ru/";
    private static final int TODAY_NEWS_PAGE_POSITION = 0;
    private static final int YESTERDAY_NEWS_PAGE_POSITION = 1;
    private static final int OTHER_NEWS_PAGE_POSITION = 2;

    private SparseArray<List<RssItem>> sparseArray;
    private static final ReentrantLock LOCK = new ReentrantLock();
    private static AtomicBoolean isInitialized = new AtomicBoolean(false);
    private static NewsManager instance;

    public static NewsManager getInstance() {
        if (!isInitialized.get()) {
            LOCK.lock();
            if (!isInitialized.get()) {
                instance = new NewsManager();
                isInitialized.set(true);
            }
            LOCK.unlock();
        }
        return instance;
    }

    public void init(final LoadRssNewsListener listener) {
        listener.start();
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(RssConverterFactory.create())
                .client(new OkHttpClient())
                .build();

        RssService service = retrofit.create(RssService.class);
        service.getRss().enqueue(new Callback<RssFeed>() {
            @Override
            public void onResponse(@NonNull Call<RssFeed> call, @NonNull Response<RssFeed> response) {
                Optional.of(response)
                        .map(Response::body)
                        .map(RssFeed::getItems)
                        .ifPresent(NewsManager.this::groupByPagePosition);
                listener.success();
            }

            @Override
            public void onFailure(@NonNull Call<RssFeed> call, @NonNull Throwable t) {
                listener.failure();
            }
        });
    }

    public List<RssItem> getPagePositionNews(int pagePosition) {
        return sparseArray.get(pagePosition);
    }

    private void groupByPagePosition(List<RssItem> list) {
        sparseArray = new SparseArray<>();
        List<RssItem> todayList = new ArrayList<>();
        List<RssItem> yesterdayList = new ArrayList<>();
        List<RssItem> otherList = new ArrayList<>();
        sparseArray.put(TODAY_NEWS_PAGE_POSITION, todayList);
        sparseArray.put(YESTERDAY_NEWS_PAGE_POSITION, yesterdayList);
        sparseArray.put(OTHER_NEWS_PAGE_POSITION, otherList);
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        list.forEach(item -> {
            LocalDate publishDate = item.getPublishDate();
            if (publishDate.isEqual(today)) {
                todayList.add(item);
            } else if (publishDate.isEqual(yesterday)) {
                yesterdayList.add(item);
            } else {
                otherList.add(item);
            }
        });
    }

    public interface LoadRssNewsListener {
        void start();

        void success();

        void failure();
    }
}