package com.sas_apps.reddit.retrofit;
/*
 * Created by Shashank Shinde.
 */

import com.sas_apps.reddit.model.comment.CheckComment;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PostCommentApi {


    @POST("{comment}")
    Call<CheckComment> postComment(
            @HeaderMap Map<String, String> headers,
            @Path("comment") String comment,
            @Query("parent") String parent,
            @Query("amp;text") String text
    );
}
