package com.radiobicocca.android.Model;

/**
 * Created by lucap on 2/24/2018.
 */
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WPComment {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("post")
    @Expose
    private Integer post;
    @SerializedName("parent")
    @Expose
    private Integer parent;
    @SerializedName("author")
    @Expose
    private Integer author;
    @SerializedName("author_name")
    @Expose
    private String authorName;
    @SerializedName("author_url")
    @Expose
    private String authorUrl;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("date_gmt")
    @Expose
    private String dateGmt;
    @SerializedName("content")
    @Expose
    private Content content;
    @SerializedName("link")
    @Expose
    private String link;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("author_avatar_urls")
    @Expose
    private AuthorAvatarUrls authorAvatarUrls;
    @SerializedName("meta")
    @Expose
    private List<Object> meta = null;
    @SerializedName("_links")
    @Expose
    private Links links;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPost() {
        return post;
    }

    public void setPost(Integer post) {
        this.post = post;
    }

    public Integer getParent() {
        return parent;
    }

    public void setParent(Integer parent) {
        this.parent = parent;
    }

    public Integer getAuthor() {
        return author;
    }

    public void setAuthor(Integer author) {
        this.author = author;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorUrl() {
        return authorUrl;
    }

    public void setAuthorUrl(String authorUrl) {
        this.authorUrl = authorUrl;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDateGmt() {
        return dateGmt;
    }

    public void setDateGmt(String dateGmt) {
        this.dateGmt = dateGmt;
    }

    public Content getContent() {
        return content;
    }

    public void setContent(Content content) {
        this.content = content;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public AuthorAvatarUrls getAuthorAvatarUrls() {
        return authorAvatarUrls;
    }

    public void setAuthorAvatarUrls(AuthorAvatarUrls authorAvatarUrls) {
        this.authorAvatarUrls = authorAvatarUrls;
    }

    public List<Object> getMeta() {
        return meta;
    }

    public void setMeta(List<Object> meta) {
        this.meta = meta;
    }

    public Links getLinks() {
        return links;
    }

    public void setLinks(Links links) {
        this.links = links;
    }

    public class AuthorAvatarUrls {

        @SerializedName("24")
        @Expose
        private String _24;
        @SerializedName("48")
        @Expose
        private String _48;
        @SerializedName("96")
        @Expose
        private String _96;

        public String get24() {
            return _24;
        }

        public void set24(String _24) {
            this._24 = _24;
        }

        public String get48() {
            return _48;
        }

        public void set48(String _48) {
            this._48 = _48;
        }

        public String get96() {
            return _96;
        }

        public void set96(String _96) {
            this._96 = _96;
        }

    }

    public class Collection {

        @SerializedName("href")
        @Expose
        private String href;

        public String getHref() {
            return href;
        }

        public void setHref(String href) {
            this.href = href;
        }

    }

    public static class Content {

        public Content(String rendered) {
            this.rendered = rendered;
        }

        @SerializedName("rendered")
        @Expose
        private String rendered;

        public String getRendered() {
            return rendered;
        }

        public void setRendered(String rendered) {
            this.rendered = rendered;
        }

    }

    public class Links {

        @SerializedName("self")
        @Expose
        private List<Self> self = null;
        @SerializedName("collection")
        @Expose
        private List<Collection> collection = null;
        @SerializedName("up")
        @Expose
        private List<Up> up = null;

        public List<Self> getSelf() {
            return self;
        }

        public void setSelf(List<Self> self) {
            this.self = self;
        }

        public List<Collection> getCollection() {
            return collection;
        }

        public void setCollection(List<Collection> collection) {
            this.collection = collection;
        }

        public List<Up> getUp() {
            return up;
        }

        public void setUp(List<Up> up) {
            this.up = up;
        }

    }

    public class Self {

        @SerializedName("href")
        @Expose
        private String href;

        public String getHref() {
            return href;
        }

        public void setHref(String href) {
            this.href = href;
        }

    }

    public class Up {

        @SerializedName("embeddable")
        @Expose
        private Boolean embeddable;
        @SerializedName("post_type")
        @Expose
        private String postType;
        @SerializedName("href")
        @Expose
        private String href;

        public Boolean getEmbeddable() {
            return embeddable;
        }

        public void setEmbeddable(Boolean embeddable) {
            this.embeddable = embeddable;
        }

        public String getPostType() {
            return postType;
        }

        public void setPostType(String postType) {
            this.postType = postType;
        }

        public String getHref() {
            return href;
        }

        public void setHref(String href) {
            this.href = href;
        }

    }
}


