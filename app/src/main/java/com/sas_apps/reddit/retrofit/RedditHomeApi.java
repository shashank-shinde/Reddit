package com.sas_apps.reddit.retrofit;
/*
 * Created by Shashank Shinde.
 */

import com.sas_apps.reddit.model.Feed;
import com.sas_apps.reddit.model.post.HomeFeed;

import org.simpleframework.xml.Root;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;

public interface RedditHomeApi {

    final String BASE_URL = "https://www.reddit.com";
    @Root(strict=false)
    @Headers("Content-Type: application/rss")
    @GET("/.rss")
    Call<HomeFeed> getFeed( );
}
