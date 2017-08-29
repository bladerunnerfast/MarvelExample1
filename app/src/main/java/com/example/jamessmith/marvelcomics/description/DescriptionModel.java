package com.example.jamessmith.marvelcomics.description;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by James on 03/08/2017.
 */

public class DescriptionModel implements Parcelable {


    private final String imageURI;
    private final String title;
    private final String description;
    private final String author;
    private final double price;
    private final int pageCount;

    public DescriptionModel(String imageURI, String title, String description, double price, int pageCount, String author) {
        this.imageURI = imageURI;
        this.title = title;
        this.description = description;
        this.price = price;
        this.pageCount = pageCount;
        this.author = author;
    }

    private DescriptionModel(Parcel parcel) {
        this.imageURI = parcel.readString();
        this.title = parcel.readString();
        this.description = parcel.readString();
        this.price = parcel.readDouble();
        this.pageCount = parcel.readInt();
        this.author = parcel.readString();
    }

    public String getImageURI() {
        return imageURI;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }

    public int getPageCount() {
        return pageCount;
    }

    public String getAuthor() {
        return author;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int i) {
        dest.writeString(this.imageURI);
        dest.writeString(this.title);
        dest.writeString(this.description);
        dest.writeDouble(this.price);
        dest.writeInt(this.pageCount);
        dest.writeString(this.author);
    }


    public static final Parcelable.Creator<DescriptionModel> CREATOR = new Parcelable.Creator<DescriptionModel>() {

        @Override
        public DescriptionModel createFromParcel(Parcel parcel) {
            return new DescriptionModel(parcel);
        }

        @Override
        public DescriptionModel[] newArray(int size) {
            return new DescriptionModel[size];
        }
    };
}
