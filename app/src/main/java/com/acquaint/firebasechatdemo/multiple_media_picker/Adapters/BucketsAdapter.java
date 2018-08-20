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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.acquaint.firebasechatdemo.BuildConfig;
import com.acquaint.firebasechatdemo.GlobalData;
import com.acquaint.firebasechatdemo.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;
import java.util.List;

public class BucketsAdapter extends RecyclerView.Adapter<BucketsAdapter.MyViewHolder> {
    private List<String> bucketNames, bitmapList;
    private Context context;
    Boolean pdf=false;

    public BucketsAdapter(List<String> bucketNames, List<String> bitmapList, Context context) {
        this.bucketNames = bucketNames;
        this.bitmapList = bitmapList;
        this.context = context;
    }
    public BucketsAdapter(List<String> bucketNames, List<String> bitmapList, Context context, Boolean pdf) {
        this.bucketNames = bucketNames;
        this.bitmapList = bitmapList;
        this.context = context;
        this.pdf=pdf;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.album_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        //bucketNames.get(position);
        holder.title.setText(bucketNames.get(position));
        if(pdf){
           /* File file = new File(bitmapList.get(position));*/
            holder.pdfView.setVisibility(View.GONE);
            holder.thumbnail.setVisibility(View.VISIBLE);
            File file = new File(bitmapList.get(position));
            Uri uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider",file);
            Bitmap bitmap = GlobalData.generateImageFromPdf(uri,context,holder.thumbnail);


        }
        else {
            holder.pdfView.setVisibility(View.GONE);
            holder.pdfView.setVisibility(View.GONE);
            holder.thumbnail.setVisibility(View.VISIBLE);
            Glide.with(context).load(bitmapList.get(position))

                    .apply(new RequestOptions().override(300, 300).centerCrop().error(android.R.drawable.stat_notify_error))
                    .into(holder.thumbnail);
        }

    }

    @Override
    public int getItemCount() {
        return bucketNames.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public ImageView thumbnail;
        public PDFView pdfView;
        public LinearLayout ll_pdf;
        public MyViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.title);
            thumbnail = view.findViewById(R.id.image);
            pdfView=view.findViewById(R.id.pdf);
            ll_pdf=view.findViewById(R.id.ll_pdf);
        }
    }

}

