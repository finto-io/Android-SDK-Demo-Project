package com.kyc_sdk_demo_android;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.GsonBuilder;

import kyc.ob.Api;
import kyc.ob.SelfieAutoCaptureFragment;
import kyc.ob.responses.DocumentInspectionResponse;


public class SelfieFragment extends Fragment implements SelfieAutoCaptureFragment.SelfieListener {

    public SelfieFragment() {}



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Please take a selfie");
        SelfieAutoCaptureFragment fragment = SelfieAutoCaptureFragment.newInstance();
        fragment.setLivelinessListener(this);
        FragmentManager manager = getActivity().getSupportFragmentManager();

        FragmentTransaction transaction = manager.beginTransaction();

        transaction.add(R.id.selfie_fragment_container, fragment);

        transaction.commit();
        return inflater.inflate(R.layout.fragment_selfie, container, false);
    }

    @Override
    public void onSelfieCaptured(Bitmap bitmap) {

        NavHostFragment navHostFragment = (NavHostFragment)getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_content_main);
        NavController navController = navHostFragment.getNavController();
        Api api = new Api(getContext(), "https://bl4-dev-02.baelab.net/api/BAF3E974-52AA-7598-FF04-56945EF93500/48EE4790-8AEF-FEA5-FFB6-202374C61700");
        api.inspectDocument(new Api.InspectDocumentsCallback() {
            @Override
            public void onSuccess(DocumentInspectionResponse response) {
                Bundle bundle = new Bundle();
                bundle.putString("result",  new GsonBuilder().setPrettyPrinting().create().toJson(response, DocumentInspectionResponse.class));

                navController.navigate(R.id.documentInspectionFragment, bundle);


            }

            @Override
            public void onFail(String code) {

            }
        });
    }

    @Override
    public void onNoCameraPermission() {

    }
}