package com.example.jamessmith.marvelcomics.backend.database;


/**
 * Created by James on 10/08/2017.
 */

public class DatabaseModel {

    private String id;
    private String title ;
    private String imageURL;
    private String description;
    private double price;
    private String thumbnail;
    private int pageCount;
    private String author;

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

    public void setTitle(String title) {
        this.title = title;
    }

    public void setImage(String imageURL) {
        this.imageURL = imageURL;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public DatabaseModel() {

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
