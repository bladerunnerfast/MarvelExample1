package com.example.jamessmith.marvelcomics.backend.files;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.ExecutionException;


/**
 * Created by James on 10/08/2017.
 */

public class FileManager {

    private String path, imageURL;
    private Context context;
    private Bitmap image;
    private boolean hasSaved = false;
    protected File file;

    public FileManager(String imageURL, String imagePart, Context context) {

        this.context = context;
        this.imageURL = imageURL;
        path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)+ "/ComicImages/"+ imagePart+"/";
    }

    public boolean saveFile() {

        if (verifyDir()) {
            OutputStream outputStream;
            try {

                image = Glide.with(context)
                        .load(imageURL)
                        .asBitmap()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(200, 200)
                        .get();

                outputStream = new FileOutputStream(file);
                image.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                outputStream.flush();
                outputStream.close();
                hasSaved = true;

            } catch (IOException | InterruptedException | ExecutionException e) {
                Log.v(FileManager.class.getName(), e.toString());
                hasSaved = false;
            }
        }else{
            hasSaved = false;
        }
        return hasSaved;
    }

    private boolean verifyDir(){

        file = new File(path);
        boolean success = true;

        if (!file.exists()) {
            Log.v(FileManager.class.getName(), "Path does not exist.");
            success = file.mkdirs();
        }else {
            Log.v(FileManager.class.getName(), "Path successfully created");
        }

        if (success) {
            file = new File(path +"/" + getSubString(imageURL));
            return true;
        } else {
            return false;
        }
    }


    public Bitmap getFile(final String filename){

                try {
                    file = new File(path + "/" + filename);

                    image = Glide.with(context)
                            .load(file)
                            .asBitmap()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(500, 500)
                            .get();

                } catch (final ExecutionException | InterruptedException e) {
            image = null;
            Log.e(FileManager.class.getName(), e.getMessage());
        }
        return image;
    }

    @NonNull
    private String getSubString(String imageUrl){
        return imageUrl.substring(imageUrl.lastIndexOf('/') + 1);
    }
}