package com.radiobicocca.android.Interface;

import com.radiobicocca.android.Model.WPComment;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by lucap on 2/24/2018.
 */

public interface CommentService {

    @GET("/wp-json/wp/v2/comments?per_page=100&orderby=date&order=asc")
    Call<List<WPComment>> getComments(@Query("post") int post_id,
                                      @Query("password") String password);

    @POST("/wp-json/wp/v2/comments")
    @FormUrlEncoded
    Call<WPComment> postComment(@Field("author_name") String name,
                                    @Field("author_email") String email,
                                    @Field("content") String content,
                                    @Field("post") Integer post_id,
                                    @Query("password") String password);
}
