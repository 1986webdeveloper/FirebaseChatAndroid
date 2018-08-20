package com.acquaint.firebasechatdemo.multiple_media_picker.Adapters;


import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.acquaint.firebasechatdemo.BuildConfig;
import com.acquaint.firebasechatdemo.GlobalData;
import com.acquaint.firebasechatdemo.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;
import java.util.List;

public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.MyViewHolder>{
    private List<String> bitmapList;
    private List<Boolean> selected;
    private Context context;
    Boolean pdf=false;

    public MediaAdapter(List<String> bitmapList, List<Boolean> selected, Context context) {
        this.bitmapList = bitmapList;
        this.context=context;
        this.selected=selected;
    }
    public MediaAdapter(List<String> bitmapList, List<Boolean> selected, Context context, Boolean pdf) {
        this.bitmapList = bitmapList;
        this.context=context;
        this.selected=selected;
        this.pdf=pdf;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.media_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {

        if(pdf){
            holder.pdfView.setVisibility(View.GONE);
            holder.thumbnail.setVisibility(View.VISIBLE);
            File file = new File(bitmapList.get(position));
            Uri uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider",file);
            Bitmap bitmap = GlobalData.generateImageFromPdf(uri,context,holder.thumbnail);



        }
        else {
            holder.pdfView.setVisibility(View.GONE);
            holder.thumbnail.setVisibility(View.VISIBLE);
            Glide.with(context).load(bitmapList.get(position))
                    .apply(new RequestOptions().override(300, 300).centerCrop().skipMemoryCache(true).error(android.R.drawable.stat_notify_error))
                    .into(holder.thumbnail);
        }

        if(selected.get(position).equals(true)){
            holder.check.setVisibility(View.VISIBLE);
            holder.check.setAlpha(150);
            holder.check.bringToFront();
        }else{
            holder.check.setVisibility(View.GONE);
        }

    }

    @Override
   public int getItemCount() {
        return bitmapList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView thumbnail,check;
        PDFView pdfView;

        public MyViewHolder(View view) {
            super(view);
            thumbnail= view.findViewById(R.id.image);
            check= view.findViewById(R.id.image2);
            pdfView=view.findViewById(R.id.pdf_item);
        }
    }

}

