package com.example.root.medium;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class UserPosts extends Fragment {

    private static final String TAG = "Fragment2";
    private RelativeLayout spinner;
    private RecyclerView mRecyclerView;
    private List<Blog> blogList = new ArrayList<>();
    private FirebaseAuth mAuth;
    private DatabaseReference mref;
    private BlogAdapter nAdapter;
    private ChildEventListener eventListner;

    public UserPosts() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mref = database.getReference("/user-posts/"+mAuth.getCurrentUser().getDisplayName());
        init();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mref.removeEventListener(eventListner);
    }

    private void init() {

        eventListner =new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                spinner.setVisibility(View.GONE);
                Blog blog = dataSnapshot.getValue(Blog.class);
                blogList.add(blog);
                nAdapter.notifyDataSetChanged();
                Log.d(TAG, "onChildAdded UserPosts: notified"+blogList.get(0).title);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Blog blog = dataSnapshot.getValue(Blog.class);
                blogList.remove(blog);
                nAdapter.notifyDataSetChanged();
                Log.d(TAG, "onChildRemoved: notified"+blogList.size());
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mref.addChildEventListener(eventListner);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView =  inflater.inflate(R.layout.fragment_user_posts, container, false);
        rootView.setTag(TAG);
        spinner = (RelativeLayout)rootView.findViewById(R.id.loadingPanel);
        mRecyclerView = (RecyclerView)rootView.findViewById(R.id.recycler);
        nAdapter = new BlogAdapter(getContext(),blogList,"userposts");
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL));
        mRecyclerView.setLayoutManager(mLayoutManager);
         mRecyclerView.setAdapter(nAdapter);
        return rootView;
    }

}
