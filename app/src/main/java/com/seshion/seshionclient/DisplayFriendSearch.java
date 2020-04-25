package com.seshion.seshionclient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class DisplayFriendSearch extends AppCompatActivity {


    RecyclerView recyclerView;
    DisplayFriendsRVAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_friend_search);

        Intent intent = getIntent();
        String listOfFriends = intent.getStringExtra("ListOfFriends");

        Gson gson = new Gson();
        JsonParser jsonParser = new JsonParser();
        JsonArray jsonListOfFriends = jsonParser.parse(listOfFriends).getAsJsonArray();
        List<UserAccount> returnedFriends = gson.fromJson(jsonListOfFriends, new TypeToken<List<UserAccount>>() {}.getType());

        /* our recycler view */
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        /* display returned friends in on post execute*/
        myAdapter = new DisplayFriendsRVAdapter(this, (ArrayList) returnedFriends);
        recyclerView.setAdapter(myAdapter);
    }
}
