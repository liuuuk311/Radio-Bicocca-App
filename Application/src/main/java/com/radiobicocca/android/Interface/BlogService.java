package com.radiobicocca.android.Interface;

import com.radiobicocca.android.Model.WPPost;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by lucap on 2/21/2018.
 */

public interface BlogService {

    @GET("/wp-json/wp/v2/posts?per_page=35")
    Call<List<WPPost>> getPosts(@Query("page") int page_id);
}
