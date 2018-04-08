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
public class BlankFragment extends Fragment {


    private static final String TAG = "Fragment";
    private RecyclerView mRecyclerView;
    private BlogAdapter mAdapter;
    private List<Blog> blogList = new ArrayList<>();
    public DatabaseReference mref;
    private RelativeLayout spinner;
    private ChildEventListener eventListner;

    public BlankFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
         mref = database.getReference("/posts");

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
                mAdapter.notifyDataSetChanged();
                Log.d(TAG, "onChildAdded: notified"+blogList.size());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
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
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_blank, container, false);
        rootView.setTag(TAG);
        spinner = (RelativeLayout)rootView.findViewById(R.id.loadingPanel);
        mRecyclerView = (RecyclerView)rootView.findViewById(R.id.recycler_view);
        mAdapter = new BlogAdapter(getContext(),blogList, "posts");
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL));
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        return rootView;
    }



}
