package com.radiobicocca.android.Interface;

import com.radiobicocca.android.Model.WPAuthor;
import com.radiobicocca.android.Model.Media;
import com.radiobicocca.android.Model.WPPost;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by lucap on 2/17/2018.
 */

public interface RetrofitArrayAPI {

    @GET("/wp-json/wp/v2/posts?per_page=10")
    Call<List<WPPost>> getPostInfo(@Query("page") int page_id);

    @GET("/wp-json/wp/v2/users/{id}")
    Call<WPAuthor> getAuthor(@Path("id") Integer id);

    @GET("/wp-json/wp/v2/media/{id}")
    Call<Media> getMedia(@Path("id") Integer id);

}

