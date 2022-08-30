package com.kyc_sdk_demo_android;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class VideoFragment extends Fragment {
    public VideoFragment() {
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Hold the button for 5 sec");
        return inflater.inflate(R.layout.fragment_blank, container, false);
    }
}