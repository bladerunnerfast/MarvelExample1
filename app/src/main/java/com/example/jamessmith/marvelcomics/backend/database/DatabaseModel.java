package com.example.jamessmith.marvelcomics.backend.database;


/**
 * Created by James on 10/08/2017.
 */

public class DatabaseModel {

    private String id;
    private final String title ;
    private final String imageURL;
    private String description;
    private final double price;
    private final String thumbnail;
    private final int pageCount;
    private final String author;

    public DatabaseModel(String title, String imageURL, String description, double price, String thumbnail,
                         int pageCount, String author) {

        this.title = title;
        this.imageURL = imageURL;
        this.description = description;
        this.price = price;
        this.thumbnail = thumbnail;
        this.pageCount = pageCount;
        this.author = author;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getImageURL() {
        return imageURL;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public int getPageCount() {
        return pageCount;
    }

    public String getAuthor() {
        return author;
    }
}
