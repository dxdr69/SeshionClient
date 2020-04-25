package com.seshion.seshionclient;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SeshionsRVAdapter extends RecyclerView.Adapter<SeshionsRVHolder> {

    Context c;
    List<UserSession> userSeshions; //this array list creates a list of array which parameters define in our model class

    public SeshionsRVAdapter(Context c, List<UserSession> userSeshions) {
        this.c = c;
        this.userSeshions = userSeshions;
    }

    @NonNull
    @Override
    public SeshionsRVHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.seshions_row, null); // this line inflates
        // our friends_row

        return new SeshionsRVHolder(view);// this will return our view to holder class
    }

    @Override
    public void onBindViewHolder(@NonNull SeshionsRVHolder holder, int position) {
        int i = position;
        holder.userNameTextView.setText(userSeshions.get(i).getName()); // set the username
        //holder.currentSeshionTextView.setText(userAccounts.get(i).getJoinedSessions().get(0).getName());
        holder.currentSeshionTextView.setText(userSeshions.get(i).getDescription());

        holder.imageView.setImageResource(userSeshions.get(i).getImg()); // here we used image resource
//        ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(100, 100);
//        params.setMargins(0, 0, 0, 0);
//        holder.imageView.setLayoutParams(params);
        // because we will use images in our resource folder which is drawable

    }

    @Override
    public int getItemCount() {
        return userSeshions.size();
    }
}
