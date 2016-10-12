package com.example.posted.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.posted.R;


public class NoItemsFragment extends Fragment {

    public static NoItemsFragment newInstance() {
        NoItemsFragment phonesFragment = new NoItemsFragment();
        return phonesFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_no_items, container, false);
        return view;
    }

}
