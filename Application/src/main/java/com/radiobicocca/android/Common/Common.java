package com.radiobicocca.android.Common;

import com.radiobicocca.android.Interface.BlogPostImageService;
import com.radiobicocca.android.Interface.BlogService;
import com.radiobicocca.android.Interface.CommentService;
import com.radiobicocca.android.Remote.BlogPostImageClient;
import com.radiobicocca.android.Remote.RetrofitClient;

/**
 * Created by lucap on 2/21/2018.
 */

public class Common {
    private static final String BASE_URL = "http://www.radiobicocca.it";
    private static final String DEBUG_TAG = "NetworkStatusExample";
    public static final String API_KEY = "";

    public static BlogService getBlogService(){
        return RetrofitClient.getClient(BASE_URL).create(BlogService.class);
    }

    public static BlogPostImageService getBlogPostImageService(){
        return BlogPostImageClient.getClient(BASE_URL).create(BlogPostImageService.class);
    }

    public static CommentService getCommentService(){
        return BlogPostImageClient.getClient(BASE_URL).create(CommentService.class);
    }

}
