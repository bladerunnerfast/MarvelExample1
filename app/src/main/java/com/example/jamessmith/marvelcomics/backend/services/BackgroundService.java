package com.example.jamessmith.marvelcomics.backend.services;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.example.jamessmith.marvelcomics.MainActivity;
import com.example.jamessmith.marvelcomics.backend.Model;
import com.example.jamessmith.marvelcomics.backend.api.adapter.CustomisedRestAdapter;
import com.example.jamessmith.marvelcomics.backend.database.DatabaseModel;
import com.example.jamessmith.marvelcomics.backend.database.DatabaseStorage;
import com.example.jamessmith.marvelcomics.backend.files.FileManager;
import com.example.jamessmith.marvelcomics.backend.observable.CustomisedObservable;
import com.example.jamessmith.marvelcomics.comics.ComicModel;
import com.example.jamessmith.marvelcomics.description.DescriptionModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by James on 10/08/2017.
 */

public class BackgroundService extends Service {

    protected static final String apiKey = "3659f42361a8687afaa755865d3fa9b4";
    protected static final String hash = "6f28263d468665eea9bcf046c1417f93";
    protected static final String ts = "1491431175";
    protected DatabaseStorage databaseStorage;
    protected DatabaseModel databaseModel;
    protected Model mModel;
    private static final String TAG = BackgroundService.class.getName();
    private String requestedService;
    private boolean isComplete = false;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null) {
            requestedService = intent.getStringExtra("requestedOperation");
            final int index = intent.getIntExtra("index", 0);

            Runnable runnable = new Runnable() {
                @Override
                public void run() {

                    databaseStorage = new DatabaseStorage(getApplicationContext());
                    mModel = new Model();

                        if ("comicsFragment".equals(requestedService)) {
                            if (verifiyUpdate()) {
                                downloadData(ts, apiKey, hash);
                            }else {
                                updateComics();
                            }
                        } else if ("DescriptionFragment".equals(requestedService)) {
                            if (verifiyUpdate()) {
                                downloadData(ts, apiKey, hash);
                            }else {
                                updateDescription(index + 2);
                            }
                        }
                }
            };

            Thread thread = new Thread(runnable);
            thread.start();
        }
        return Service.START_REDELIVER_INTENT;
    }

    private boolean verifiyUpdate() {

        Calendar calendar = Calendar.getInstance();
        long duration = databaseStorage.getLastModified() - calendar.getTimeInMillis();
        long hours = TimeUnit.MILLISECONDS.toHours(duration);
        Date date = new Date(hours);

        if ((databaseStorage.isTableExists()) && (databaseStorage.countRows() <= 0)) {
            Log.v(TAG, "hours: " + databaseStorage.countRows());
            return 300 > date.getHours();
        } else {
            return false;
        }
    }

    private void updateComics() {
        ArrayList<ComicModel> comicModels = databaseStorage.getComicData();

        if (comicModels.size() > 0) {

            Intent intent = new Intent();
            intent.setAction("updateComicFragment");
            intent.putParcelableArrayListExtra("comicData", comicModels);
            sendBroadcast(intent);
        }
    }

    private void updateDescription(int indexValue) {
        DescriptionModel databaseModel = databaseStorage.getDescriptionData(indexValue);

        if(databaseModel != null) {

            Intent intent = new Intent();
            intent.setAction("updateDescriptionFragment");
            intent.putExtra("descriptionData", databaseModel);
            sendBroadcast(intent);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void downloadData(String ts, String apiKey, String hash) {

        final CustomisedRestAdapter restfulClient = new CustomisedRestAdapter();
        CustomisedObservable api = restfulClient.getRest().build().create(CustomisedObservable.class);

        restfulClient.getCompositeSubscription().add(api.getComics("100", ts, apiKey, hash)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Observer<Model>() {

                    @Override
                    public void onCompleted() {
                        storeData();
                        restfulClient.getCompositeSubscription().unsubscribe();
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e != null) {
                            Log.v(MainActivity.class.getName(), e.toString());
                            Toast.makeText(getApplicationContext(), "Failed to download data", Toast.LENGTH_SHORT).show();
                            restfulClient.getCompositeSubscription().unsubscribe();
                        }
                    }

                    @Override
                    public void onNext(Model model) {
                        mModel = model;
                    }
                })
        );
    }

    private String getCreators(int i){
        if(mModel.getData().getResults().get(i).getCreators().getItems().size() > 0){
            return mModel.getData().getResults().get(i).getCreators().getItems().get(0).getName();
        }else{
            return "Not Available";
        }
    }

    private boolean storeData(){

        new AsyncTask<Void, Void, Void>() {
            private String imageURL, thumbnailURL;
            protected FileManager  fileManager;

            @Override
            protected Void doInBackground(Void... params) {

                    for (int i = 0; i < getResultsSize(); i++) {

                        if (getComicImagesSize(i) > 0) {
                            imageURL = getComicPath(i) + "." + getComicExtension(i);
                        } else {
                            imageURL = "http://i.annihil.us/u/prod/marvel/i/mg/b/40/image_not_available.jpg";
                        }

                        fileManager = new FileManager(imageURL, "image", getApplicationContext());

                        if(fileManager.saveFile()){
                            Log.v(TAG, "Saved images.");
                        }

                        thumbnailURL = getThumbnailPath(i) + "." + getThumbnailExtension(i);
                        fileManager = new FileManager(thumbnailURL, "thumbnail", getApplicationContext());

                        if(fileManager.saveFile()){
                            Log.v(TAG, "saved thumbnails");
                        }

                        databaseModel = new DatabaseModel(getTitle(i), imageURL.substring(imageURL.lastIndexOf('/') + 1),
                                getDescription(i), getPrice(i), thumbnailURL.substring(thumbnailURL.lastIndexOf('/') + 1),
                                getPageCount(i), getCreators(i));

                         if(databaseStorage.insertEntry(databaseModel)){
                           Log.v(TAG, "Database sucessfuly updated");
                        }else {
                         Log.v(TAG, "Database update failed.");
                        }
                    }

                    return null;
            }
            @Override
            protected void onPostExecute(Void dummy) {
                isComplete = true;
                updateComics();
            }
        }.execute();

        return isComplete;
        }

        private int getResultsSize(){
            if(mModel.getData().getResults() != null) {
                return mModel.getData().getResults().size();
            }else{
                return 0;
            }
        }

        private int getComicImagesSize(int i){
            return mModel.getData().getResults().get(i).getComicImages().size();
        }

        private String getComicPath(int i){
            return mModel.getData().getResults().get(i).getComicImages().get(0).getPath();
        }

        private String getComicExtension(int i){
            return mModel.getData().getResults().get(i).getComicImages().get(0).getExtension();
        }

        private String getThumbnailPath(int i){
            return mModel.getData().getResults().get(i).getThumbnail().getPath();
        }

        private String getThumbnailExtension(int i){
            return mModel.getData().getResults().get(i).getThumbnail().getExtension();
        }

        private String getTitle(int i){
            return replaceBadSymbols(mModel.getData().getResults().get(i).getTitle());
        }

        private String getDescription(int i){
            return replaceBadSymbols(mModel.getData().getResults().get(i).getDescription());
        }

        private String replaceBadSymbols(String str){

            if(str == null){
                str = "Not available";
            }

            str = str.replace("&", "and").replace("'", "--");
            return str;
        }

        private double getPrice(int i){
            return mModel.getData().getResults().get(i).getPrices().get(0).getPrice();
        }

        private int getPageCount(int i){
            return mModel.getData().getResults().get(i).getPageCount();
        }
}
