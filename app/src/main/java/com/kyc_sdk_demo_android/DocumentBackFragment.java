package com.kyc_sdk_demo_android;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import kyc.BaeError;
import kyc.ob.DocumentScanBackFragment;

public class DocumentBackFragment extends Fragment
        implements DocumentScanBackFragment.DocumentScanListener {

    DocumentScanBackFragment fragment;
    public DocumentBackFragment() {
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_back, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Scan back of your ID");
        fragment = DocumentScanBackFragment.newInstance();
        fragment.setDocumentScanListener(this);
        FragmentManager manager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        transaction.replace(R.id.back_fragment_container, fragment);
        transaction.commit();
    }

    @Override
    public void onDocumentScanBackSuccess(Bitmap bitmap) {
        NavHostFragment navHostFragment = (NavHostFragment) getActivity()
                .getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_content_main);
        NavController navController = navHostFragment.getNavController();
        Handler mainHandler = new Handler(Looper.getMainLooper());
        mainHandler.post(() -> {
            navController.navigate(R.id.selfie);
        });
    }

    @Override
    public void onDocumentScanBackFailed(BaeError baeError) {
        Handler mainHandler = new Handler(Looper.getMainLooper());
        mainHandler.post(() -> {
            Toast.makeText(getContext(), baeError.getMessage(), Toast.LENGTH_SHORT).show();
        });

    }

    @Override
    public void onNoCameraPermission() {
    }
}