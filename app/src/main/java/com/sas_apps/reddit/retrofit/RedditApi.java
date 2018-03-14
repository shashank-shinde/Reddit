package com.sas_apps.reddit.retrofit;
/*
 * Created by Shashank Shinde.
 */

import com.sas_apps.reddit.login.reddit_login.CheckLogin;
import com.sas_apps.reddit.model.Feed;


import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RedditApi {

    //For dynamic Url
    @Headers("Content-Type: application/rss")
    @GET("{subreddit_name}/.rss")
    Call<Feed> getFeed(@Path("subreddit_name") String subreddit);

}
