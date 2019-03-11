package com.hinews.data.service;

import com.hinews.data.item.RssFeed;

import retrofit2.Call;
import retrofit2.http.GET;

public interface RssService {
    @GET("/feed")
    Call<RssFeed> getRss();
}
