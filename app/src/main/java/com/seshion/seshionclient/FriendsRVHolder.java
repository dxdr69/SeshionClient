package com.seshion.seshionclient;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FriendsRVHolder extends RecyclerView.ViewHolder {

    ImageView imageView;
    TextView userNameTextView, currentSeshionTextView;

    public FriendsRVHolder(@NonNull View itemView) {
        super(itemView);

        this.imageView = itemView.findViewById(R.id.imageview);
        this.userNameTextView = itemView.findViewById(R.id.usernametextview);
        this.currentSeshionTextView = itemView.findViewById(R.id.currentseshiontextview);

    }
}
