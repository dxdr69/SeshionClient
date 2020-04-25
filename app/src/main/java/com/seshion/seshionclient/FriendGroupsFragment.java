//package com.seshion.seshionclient;
//
//
//import android.content.Intent;
//import android.os.Bundle;
//
//import androidx.fragment.app.Fragment;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//
//import com.github.clans.fab.FloatingActionButton;
//
//import java.util.List;
//
//
///**
// * A simple {@link Fragment} subclass.
// */
//public class FriendGroupsFragment extends Fragment {
//
//
//    RecyclerView recyclerView;
//    FriendsRVAdapter myAdapter;
//
//    private String PARCEL_KEY = "data";
//
//    public FriendGroupsFragment() {
//        // Required empty public constructor
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//
//        // Inflate the layout for this fragment
//        View view = inflater.inflate(R.layout.fragment_friend_groups, container, false);
//
//        /* get the parcel from dashboard activity */
//        Bundle bundle = getArguments();
//        StateParcel stateParcel = bundle.getParcelable(PARCEL_KEY);
//        List<UserAccount> friendGroups = stateParcel.getFriends();
//        //List<UserAccount> users = stateParcel.getFriendsListUserAccounts();
//
//        /* our recycler view */
//        recyclerView = view.findViewById(R.id.recyclerview);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
//
//        FloatingActionButton fab1 = (FloatingActionButton) view.findViewById(R.id.fab1);
//        FloatingActionButton fab2 = (FloatingActionButton) view.findViewById(R.id.fab2);
//
//        fab1.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View view) {
//
//                launchFriendSearch();
//            }
//        });
//
//        myAdapter = new FriendsRVAdapter(getActivity(), friends);
//        recyclerView.setAdapter(myAdapter);
//        return view;
//    }
//
//    public void launchFriendSearch() {
//
//        Intent intent = new Intent(getContext(), AddFriend.class);
//        intent.putExtra("USER_NAME", "Twizzy");
//        startActivity(intent);
//
//    }
//
//
//
//}
