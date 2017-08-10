package com.example.jamessmith.marvelcomics.comics;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by User on 09/08/2017.
 */

public class ComicModel implements Parcelable {

    private String imageURI, title;
    double price;

    public ComicModel(String imageURI, String title, double price) {
        this.imageURI = imageURI;
        this.title = title;
        this.price = price;
    }

    public String getImageURI() {
        return imageURI;
    }

    public String getTitle() {
        return title;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.imageURI);
        dest.writeString(this.title);
        dest.writeDouble(this.price);
    }

    protected ComicModel(Parcel in) {
        this.imageURI = in.readString();
        this.title = in.readString();
        this.price = in.readDouble();
    }

    public static final Parcelable.Creator<ComicModel> CREATOR = new Parcelable.Creator<ComicModel>() {
        public ComicModel createFromParcel(Parcel source) {
            return new ComicModel(source);
        }

        public ComicModel[] newArray(int size) {
            return new ComicModel[size];
        }
    };
}
