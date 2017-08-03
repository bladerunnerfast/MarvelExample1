package com.example.jamessmith.marvelcomics.comics;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.jamessmith.marvelcomics.R;
import com.example.jamessmith.marvelcomics.api.model.Model;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by James on 03/08/2017.
 */

public class ComicAdapter extends RecyclerView.Adapter<ComicAdapter.CustomViewHolder>{

    protected Model model;
    private Context context;

    public ComicAdapter(Model model, Context context){
        this.model = model;
        this.context = context;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comic_recycler_list, parent, false);
        return new CustomViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {

        if(model.getData().getResults().get(position).getComicImages() != null) {
            if (model.getData().getResults().get(position).getComicImages().size() > 0) {
                holder.image = model.getData().getResults().get(position).getThumbnail().getPath();
                holder.extension = "." + model.getData().getResults().get(position).getThumbnail().getExtension();

                Glide.with(context)
                        .load(holder.image + holder.extension)
                        .asBitmap()
                        .centerCrop()
                        .into(holder._thumbnail);
            }
        }

        holder._title.setText(model.getData().getResults().get(position).getTitle());
        holder._author.setText(model.getData().getResults().get(position).getUpc());
    }

    @Override
    public int getItemCount() {
        return model.getData().getResults() == null ? 0 : model.getData().getResults().size();
    }

    public static class CustomViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.iv_thumbnail) ImageView _thumbnail;
        @BindView(R.id.tv_title) TextView _title;
        @BindView(R.id.tv_author) TextView _author;
        @BindView(R.id.card_view) CardView cardView;

        private Intent intent;
        private String image, extension;

        private CustomViewHolder(View view, final Context context){
            super(view);
            ButterKnife.bind(this, view);

            intent = new Intent();

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    intent.setAction("updateMain");
                    intent.putExtra("indexValue", getAdapterPosition());
                    context.sendBroadcast(intent);
                }
            });
        }
    }
}
