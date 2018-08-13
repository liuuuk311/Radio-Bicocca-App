package com.radiobicocca.android.Interface;

import com.radiobicocca.android.Model.Media;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by lucap on 2/21/2018.
 */

public interface BlogPostImageService {

    @GET("/wp-json/wp/v2/media/{id}")
    Call<Media> getMedia(@Path("id") Integer id);
}
