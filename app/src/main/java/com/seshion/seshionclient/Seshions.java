package com.seshion.seshionclient;


import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.clans.fab.FloatingActionButton;
import com.google.gson.Gson;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.security.PublicKey;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.crypto.Cipher;


/**
 * A simple {@link Fragment} subclass.
 */
public class Seshions extends Fragment {

    RecyclerView recyclerView;
    SeshionsRVAdapter seshionsRVAdapter;

    /* to get data from the parcel */
    private String PARCEL_KEY = "data";

    public Seshions() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_seshions, container, false);

        /* get the parcel from dashboard activity */
        Bundle bundle = getArguments();
        StateParcel stateParcel = bundle.getParcelable(PARCEL_KEY);
        List<UserSession> seshions = stateParcel.getAllOpenSessions();

        recyclerView = view.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        seshionsRVAdapter = new SeshionsRVAdapter(getActivity(), seshions);
        recyclerView.setAdapter(seshionsRVAdapter);

        FloatingActionButton fab1 = (FloatingActionButton) view.findViewById(R.id.fab1);
        FloatingActionButton fab2 = (FloatingActionButton) view.findViewById(R.id.fab2);

        fab1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                launchActivity();
            }
        });

        return view;
    }

    public void launchActivity() {

        Intent intent = new Intent(getContext(), NewSeshion.class);
        intent.putExtra("USER_NAME", "Twizzy");
        startActivity(intent);

    }


    private ArrayList<UserSession> getSeshionsList() {
        ArrayList<UserSession> seshions = new ArrayList<UserSession>();

//        UserSession sesh = new UserSession("Scazi", "TwizzyBomb",
//                41.063378, -73.546277,
//                -73.546277, 41.062885,
//                41.063378, -73.545720,
//                41.062885, -73.545720,
//                LocalDate.now(), LocalTime.NOON,
//                false);
//        sesh.setImg(R.drawable.scazismall);
//        seshions.add(sesh);
//
//        UserSession sesh2 = new UserSession("Calf Pasture Beach Skatepark", "TwizzyBomb",
//                41.084396, -73.396121,
//                41.084020, -73.395482,
//                LocalDate.now(), LocalTime.NOON,
//                false);
//        sesh2.setImg(R.drawable.calfpasturesmall);
//        seshions.add(sesh2);
//
//        UserSession sesh3 = new UserSession("Danbury Skatepark", "TwizzyBomb",
//                41.396016, -73.450117,
//                41.395187, -73.449259,
//                LocalDate.now(), LocalTime.NOON,
//                false);
//        sesh3.setImg(R.drawable.danburysmall);
//        seshions.add(sesh3);
//
//        UserSession sesh4 = new UserSession("Newtown Skatepark", "TwizzyBomb",
//                41.397028, -73.301002,
//                41.395187, -73.449259,
//                LocalDate.now(), LocalTime.NOON,
//                false);
//        sesh4.setImg(R.drawable.newtownsmall);
//        seshions.add(sesh4);

        return seshions;
    }
}
