package com.example.jamessmith.marvelcomics.description;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jamessmith.marvelcomics.R;
import com.example.jamessmith.marvelcomics.backend.files.FileManager;
import com.example.jamessmith.marvelcomics.backend.services.BackgroundService;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by James on 03/08/2017.
 */

public class ComicDescription extends Fragment{

    @BindView(R.id.iv_desc_image) ImageView _image;
    @BindView(R.id.tv_desc_title) TextView _title;
    @BindView(R.id.tv_desc_description) TextView _description;
    @BindView(R.id.tv_desc_price) TextView _price;
    @BindView(R.id.tv_desc_page_count) TextView _pageCount;
    @BindView(R.id.tv_desc_author) TextView _author;

    private BroadcastReceiver broadcastReceiver;
    private IntentFilter intentFilter;
    private FileManager fileManager;
    private DescriptionModel descriptionModel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_comic_description, container, false);
        ButterKnife.bind(this, view);

        Bundle bundle = this.getArguments();

        if (bundle != null) {
            Intent intent = new Intent(getContext(), BackgroundService.class);
            intent.putExtra("requestedOperation", "DescriptionFragment");
            intent.putExtra("index", bundle.getInt("index"));
            getActivity().startService(intent);
        }

        intentFilter = new IntentFilter("updateDescriptionFragment");
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent != null){
                    descriptionModel = intent.getParcelableExtra("descriptionData");

                    if(descriptionModel != null){
                        fileManager = new FileManager(null, "image", getContext());


                        new AsyncTask<Void, Void, Void>(){
                            private Bitmap bitmap;
                            @Override
                            protected Void doInBackground(Void... voids) {

                                bitmap = fileManager.getFile(descriptionModel.getImageURI());
                                return null;
                            }

                            @Override
                            protected void onPostExecute(Void aVoid) {
                                super.onPostExecute(aVoid);
                                if(bitmap != null){
                                    _image.setImageBitmap(bitmap);
                                }
                            }
                        }.execute();


                        String description = descriptionModel.getDescription();
                        description = description.replace("and", "&").replace("--", "'").replace("<br>", "");
                        _description.setMovementMethod(new ScrollingMovementMethod());

                        _title.setText(descriptionModel.getTitle());
                        _description.setText("Description: " + description);
                        _price.setText("Cost: " + descriptionModel.getPrice());
                        _pageCount.setText("Pages: " + descriptionModel.getPageCount());
                        _author.setText("Author: " + descriptionModel.getAuthor());


                    }
                }
            }
        };

        getContext().registerReceiver(broadcastReceiver, intentFilter);
        return view;
    }

    @Override
    public void onStop() {
        super.onStop();

        try{
            getContext().unregisterReceiver(broadcastReceiver);
        }catch(Exception e){}
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try{
            getContext().unregisterReceiver(broadcastReceiver);
        }catch(Exception e){}
    }
}
