package com.sas_apps.reddit.model.post;
/*
 * Created by Shashank Shinde.
 */

public class Post {

    private String title;
    private String author;
    private String dateUpdated;
    private String postUrl;
    private String thumbnailUrl;
    private String id;

    public Post(String title, String author, String dateUpdated, String postUrl, String thumbnailUrl, String id) {
        this.title = title;
        this.author = author;
        this.dateUpdated = dateUpdated;
        this.postUrl = postUrl;
        this.thumbnailUrl = thumbnailUrl;
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDateUpdated() {
        return dateUpdated;
    }

    public void setDateUpdated(String dateUpdated) {
        this.dateUpdated = dateUpdated;
    }

    public String getPostUrl() {
        return postUrl;
    }

    public void setPostUrl(String postUrl) {
        this.postUrl = postUrl;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
