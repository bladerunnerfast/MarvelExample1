package com.example.jamessmith.marvelcomics.comics;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jamessmith.marvelcomics.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by James on 03/08/2017.
 */

public class ComicAdapter extends RecyclerView.Adapter<ComicAdapter.CustomViewHolder>{

    private final ArrayList<ComicModel> comicModels;
    private final Context context;
    private final Bitmap[] bitmaps;

    public ComicAdapter(Bitmap[] bitmaps, ArrayList<ComicModel> comicModels, Context context){
        this.comicModels = comicModels;
        this.context = context;
        this.bitmaps = bitmaps;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comic_recycler_list, parent, false);
        return new CustomViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {

        if(bitmaps != null) {
            holder._thumbnail.setImageBitmap(bitmaps[position]);
        }

        holder._title.setText(comicModels.get(position).getTitle());
        holder._author.setText("Price: " + String.valueOf(comicModels.get(position).getPrice()));
    }

    @Override
    public int getItemCount() {
        return comicModels == null ? 0 : comicModels.size();
    }

    public static class CustomViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.iv_thumbnail) ImageView _thumbnail;
        @BindView(R.id.tv_title) TextView _title;
        @BindView(R.id.tv_author) TextView _author;
        @BindView(R.id.card_view) CardView cardView;

        private final Intent intent;

        private CustomViewHolder(View view, final Context context){
            super(view);
            ButterKnife.bind(this, view);

            intent = new Intent();

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    intent.setAction("updateMain");
                    intent.putExtra("indexValue", String.valueOf(getAdapterPosition()));
                    context.sendBroadcast(intent);
                }
            });
        }
    }
}
