package com.itempire.citytocitytravelandroidapp.user;


import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.itempire.citytocitytravelandroidapp.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentUserProfile extends Fragment {

    Context context;
    View view;

    public FragmentUserProfile() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = container.getContext();
        // Inflate the layout for this fragment
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_user_profile, container, false);


        }
        return view;
    }

}
