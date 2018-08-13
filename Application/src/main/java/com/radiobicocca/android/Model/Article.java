package com.radiobicocca.android.Model;

/**
 * Created by lucap on 2/15/2018.
 */

public class Article {

    public String id;
    public String title;
    public String details;
    public String summary;
    public String imgUrl;

    public String getBigImgUrl() {
        return bigImgUrl;
    }

    public String bigImgUrl;
    public String date;
    public String author;

    public String getTitle() {
        return title;
    }

    public String getDetails() {
        return details;
    }

    public String getSummary() {
        return summary;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public String getDate() {
        return date;
    }

    public String getAuthor() {
        return author;
    }

    public String getId() {

        return id;
    }

    public Article(String id, String title, String summary, String details, String imgUrl, String bigImgUrl, String date, String author) {
        this.id = id;
        this.title = title;
        this.summary = summary;
        this.details = details;
        this.imgUrl = imgUrl;
        this.date = date;
        this.author = author;
        this.bigImgUrl = bigImgUrl;
    }

    @Override
    public String toString() {
        return title + " _ " + author;
    }
}
