package com.hinews.manager;

import android.support.annotation.NonNull;

import com.hinews.service.RssService;
import com.hinews.item.RssFeed;
import com.hinews.item.RssItem;
import com.hinews.parsing.RssConverterFactory;

import java.util.Collections;
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
    private static final ReentrantLock LOCK = new ReentrantLock();
    private static AtomicBoolean isInitialized = new AtomicBoolean(false);
    private static NewsManager instance;
    private List<RssItem> rssItems;

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
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();
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
                rssItems = Optional.of(response).map(Response::body).map(RssFeed::getItems).orElseGet(Collections::emptyList);
                listener.success();
            }

            @Override
            public void onFailure(@NonNull Call<RssFeed> call, @NonNull Throwable t) {
                listener.failure();
            }
        });
    }

    public List<RssItem> getRssItems() {
        return rssItems;
    }

    public interface LoadRssNewsListener {
        void start();

        void success();

        void failure();
    }
}