package com.example.jamessmith.marvelcomics.backend.observable;

import com.example.jamessmith.marvelcomics.backend.Model;

import retrofit.http.GET;
import retrofit.http.Query;
import rx.Observable;

/**
 * Created by James on 03/07/2017.
 */

public interface CustomisedObservable {
    @GET("/v1/public/comics")
    Observable<Model> getComics(@Query("limit") String limit, @Query("ts") String timeStamp,
                                @Query("apikey") String apiKey, @Query("hash") String hash);
}
