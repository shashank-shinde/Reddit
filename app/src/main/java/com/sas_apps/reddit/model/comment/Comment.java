package com.sas_apps.reddit.model.comment;
/*
 * Created by Shashank Shinde.
 */

public class Comment {

    private String id;
    private String comment;
    private String author;
    private String date;

    public Comment(String comment, String author, String date, String id) {
        this.comment = comment;
        this.author = author;
        this.date = date;
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "comment='" + comment + '\'' +
                ", author='" + author + '\'' +
                ", updated='" + date+ '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}
