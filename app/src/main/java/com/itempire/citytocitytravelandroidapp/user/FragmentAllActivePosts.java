package com.itempire.citytocitytravelandroidapp.user;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.itempire.citytocitytravelandroidapp.Constant;
import com.itempire.citytocitytravelandroidapp.FragmentInteractionListenerInterface;
import com.itempire.citytocitytravelandroidapp.R;
import com.itempire.citytocitytravelandroidapp.adapters.AdapterAllPosts;
import com.itempire.citytocitytravelandroidapp.controllers.MyFirebaseDatabaseClass;
import com.itempire.citytocitytravelandroidapp.models.Post;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentAllActivePosts extends Fragment {

    Context context;
    View view;
    RecyclerView recycler_all_posts;
    ValueEventListener valueEventListener;
    List<Post> postsList;
    AdapterAllPosts adapterAllPosts;

    private FragmentInteractionListenerInterface mListener;

    public FragmentAllActivePosts() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        context = container.getContext();
        if (mListener != null)
            mListener.onFragmentInteraction(Constant.TITLE_HOME);
        // Inflate the layout for this fragment
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_all_active_posts, container, false);

            postsList = new ArrayList<>();
            adapterAllPosts = new AdapterAllPosts(context, postsList);

            recycler_all_posts = (RecyclerView) view.findViewById(R.id.recycler_all_posts);
            recycler_all_posts.setHasFixedSize(true);
            recycler_all_posts.setLayoutManager(new LinearLayoutManager(context));
            recycler_all_posts.setAdapter(adapterAllPosts);

            initValueEventListener();


        }
        return view;
    }

    private void initValueEventListener() {
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

               postsList.clear();

                Iterable<DataSnapshot> childrenList = dataSnapshot.getChildren();
                for (DataSnapshot child : childrenList) {
                    try {
                        Post post = child.getValue(Post.class);
                        if (post != null && post.getPostStatus().equals(Constant.POST_ACTIVE_STATUS))
                            postsList.add(post);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                adapterAllPosts.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        MyFirebaseDatabaseClass.POSTS_REFERENCE.addValueEventListener(valueEventListener);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        removeValueEventListener();
    }

    private void removeValueEventListener(){
        if (valueEventListener != null)
            MyFirebaseDatabaseClass.POSTS_REFERENCE.removeEventListener(valueEventListener);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (FragmentInteractionListenerInterface) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + "must implement FragmentInteractionListenerInterface.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mListener != null)
            mListener.onFragmentInteraction(Constant.TITLE_HOME);
    }

}
