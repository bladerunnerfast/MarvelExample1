package com.example.jamessmith.marvelcomics.comics;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jamessmith.marvelcomics.R;
import com.example.jamessmith.marvelcomics.backend.files.FileManager;
import com.example.jamessmith.marvelcomics.backend.services.BackgroundService;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ComicsFragment extends Fragment {

    private ComicAdapter comicAdapter;
    private BroadcastReceiver broadcastReceiver;
    private IntentFilter intentFilter;
    private ArrayList<ComicModel> allParcelables;

    @BindView(R.id.rv_list) RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comics, container, false);
        ButterKnife.bind(this, view);

        Intent intent = new Intent(getContext(), BackgroundService.class);
        intent.putExtra("requestedOperation", "comicsFragment");
        getContext().startService(intent);

        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int density = metrics.densityDpi;
        int gridRowCount;

        if (density == DisplayMetrics.DENSITY_HIGH) {
            gridRowCount = 3;
        }
        else if (density == DisplayMetrics.DENSITY_MEDIUM) {
           gridRowCount = 2;
        }
        else if (density == DisplayMetrics.DENSITY_LOW) {
           gridRowCount = 1;
        }
        else {
            gridRowCount = 1;
        }

        if(recyclerView != null){
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), gridRowCount));
            recyclerView.setItemAnimator(new DefaultItemAnimator());
        }

        intentFilter = new IntentFilter("updateComicFragment");

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(final Context context, Intent intent) {
                if (intent != null) {
                    allParcelables = intent.getParcelableArrayListExtra("comicData");
                    if (allParcelables != null) {
                        new AsyncTask<Void, Void, Void>() {
                            private Bitmap[] bitmaps = new Bitmap[allParcelables.size()];
                            private FileManager fileManager = new FileManager(null, "image", context);
                            private Bitmap bitmap;

                            @Override
                            protected Void doInBackground(Void... params) {

                                    for (int i = 0; i < allParcelables.size(); i++) {
                                        if(fileManager.getFile(allParcelables.get(i).getImageURI()) != null) {
                                            bitmap = fileManager.getFile(allParcelables.get(i).getImageURI());
                                            if(bitmap.getByteCount() > 0) {
                                                bitmaps[i] = bitmap;
                                            }
                                        }
                                    }
                            return null;
                        }

                            @Override
                            protected void onPostExecute(Void aVoid) {
                                super.onPostExecute(aVoid);
                                setRecyclerView(bitmaps, allParcelables);
                            }

                    }.execute();
                    }
                }
            }
        };
        getContext().registerReceiver(broadcastReceiver, intentFilter);

        return view;
    }

    private void setRecyclerView(Bitmap[] bitmaps, ArrayList<ComicModel> descriptionModelList){

        comicAdapter = new ComicAdapter(bitmaps, descriptionModelList, getContext());
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                recyclerView.setAdapter(comicAdapter);
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();

        try {
            getContext().unregisterReceiver(broadcastReceiver);
        }catch(Exception e){}
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        try {
            getContext().unregisterReceiver(broadcastReceiver);
        }catch(Exception e){}
    }
}
