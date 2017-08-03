package com.example.jamessmith.marvelcomics.comics;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.jamessmith.marvelcomics.MainActivity;
import com.example.jamessmith.marvelcomics.R;
import com.example.jamessmith.marvelcomics.api.model.Model;
import com.example.jamessmith.marvelcomics.api.observable.CustomisedObservable;
import com.example.jamessmith.marvelcomics.api.restful.CustomisedRestAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class ComicsFragment extends Fragment {

    private ComicAdapter comicAdapter;

    @BindView(R.id.rv_list) RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comics, container, false);
        ButterKnife.bind(this, view);

        String apiKey = "3659f42361a8687afaa755865d3fa9b4";
        String hash = "6f28263d468665eea9bcf046c1417f93";
        String ts = "1491431175";

        downloadData(ts, apiKey, hash);

        return view;
    }

    private void downloadData(String ts, String apiKey, String hash) {

        final CustomisedRestAdapter restfulClient =
                new CustomisedRestAdapter();
        CustomisedObservable api = restfulClient.getRest().build().create(CustomisedObservable.class);

        restfulClient.getCompositeSubscription().add(api.getComics("100", ts, apiKey, hash)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Observer<Model>() {

                    @Override
                    public void onCompleted() {
                        restfulClient.getCompositeSubscription().unsubscribe();
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e != null) {
                            Log.v(MainActivity.class.getName(), e.toString());
                            Toast.makeText(getContext(), "Failed to Obtain media data", Toast.LENGTH_SHORT).show();
                            restfulClient.getCompositeSubscription().unsubscribe();
                        }
                    }

                    @Override
                    public void onNext(Model model) {
                        setRecyclerView(model);
                    }
                })
        );
    }

    private void setRecyclerView(Model model){

        if(recyclerView != null){
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setItemAnimator(new DefaultItemAnimator());
        }

        comicAdapter = new ComicAdapter(model, getContext());
        recyclerView.setAdapter(comicAdapter);
    }
}
