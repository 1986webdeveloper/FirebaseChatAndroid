package com.acquaint.firebasechatdemo.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Property;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.acquaint.firebasechatdemo.R;
import com.acquaint.firebasechatdemo.listeners.onUserClickListener;
import com.acquaint.firebasechatdemo.model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by acquaint on 20/8/18.
 */

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.MyViewHolder> {
    Context mContext;
    ArrayList<User> userList = new ArrayList<>();
    onUserClickListener onItemClickListener;
    String uname1;
    public UserAdapter(Context mContext, ArrayList<User> userList, onUserClickListener onItemClickListener) {
        this.mContext = mContext;
        this.userList = userList;
        this.onItemClickListener=onItemClickListener;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        getDatafromSharedPref();
        final User user = userList.get(position);
        String username = user.getFname()+" "+user.getLname();
        if(username.equalsIgnoreCase(uname1)){
            holder.tv_uname.setVisibility(View.GONE);
        }
        else {
            holder.tv_uname.setVisibility(View.VISIBLE);
            holder.tv_uname.setText(username);
        }

        holder.tv_uname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.onUserClick(user.getFname()+ " "+user.getLname());
            }
        });

    }

    @Override
    public int getItemCount() {
       return userList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_uname;


        public MyViewHolder(View view) {
            super(view);
            tv_uname =  view.findViewById(R.id.tv_uname);

        }
    }
    private void getDatafromSharedPref() {
        SharedPreferences sharedPreferences= mContext.getSharedPreferences("my prefs", Context.MODE_PRIVATE);
        String userId=sharedPreferences.getString("id","");

        String fname=sharedPreferences.getString("fname","");
        String lname=sharedPreferences.getString("lname","");
        uname1=fname+" "+lname;


    }
}

