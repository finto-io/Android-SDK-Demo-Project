package com.kyc_sdk_demo_android;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.kyc_sdk_demo_android.databinding.FragmentFirstBinding;
import com.google.gson.GsonBuilder;

import kyc.FilePicker;
import kyc.Uploader;
import kyc.ob.Api;
import kyc.ob.BaeInitializer;
import kyc.ob.DocumentScanBackFragment;
import kyc.ob.DocumentScanFrontFragment;
import kyc.ob.SelfieAutoCaptureFragment;
import kyc.ob.VideoFragment;
import kyc.ob.responses.DocumentInspectionResponse;

public class FirstFragment extends Fragment implements VideoFragment.VideoRecordListener,
        DocumentScanFrontFragment.DocumentScanListener, DocumentScanBackFragment.DocumentScanListener, SelfieAutoCaptureFragment.SelfieListener {

    private FragmentFirstBinding binding;
    private NavController navController;
    private  NavHostFragment navHostFragment;
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        BaeInitializer di = new BaeInitializer(getContext(), R.raw.iengine);
        di.initialize();
        binding = FragmentFirstBinding.inflate(inflater, container, false);
        navHostFragment = (NavHostFragment)getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_content_main);
        navController = navHostFragment.getNavController();
        return binding.getRoot();

    }


    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();

                        String baseUrl = "https://bl4-dev-02.baelab.net/api/BAF3E974-52AA-7598-FF04-56945EF93500/48EE4790-8AEF-FEA5-FFB6-202374C61700";
                        Uploader uploader = new Uploader(getContext(), baseUrl);
                        uploader.addDocument(data.getData());


                        Bundle bundle = new Bundle();
                        bundle.putString("url", "Loading...");
                        navController.navigate(R.id.uploaderFragment, bundle);



                        uploader.uploadDocuments(uri -> {
      
                            // Get a handler that can be used to post to the main thread
                            Handler mainHandler = new Handler(Looper.getMainLooper());

                            Runnable myRunnable = ()-> {
                                    FragmentManager supportFragmentManager =getActivity().getSupportFragmentManager();
                                    NavHostFragment navHostFragment = (NavHostFragment) supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main);
                                    navHostFragment.getChildFragmentManager().getFragments().get(0);
                                    UploaderFragment uploaderFragment = (UploaderFragment) navHostFragment.getChildFragmentManager().getFragments().get(0);
                                    uploaderFragment.setUrl(uri.fileUrl);
                            };
                            mainHandler.post(myRunnable);



                        });
                    }
                }
            });

    public void startVideo(){
        VideoFragment fragment = new VideoFragment();
        fragment.setVideoRecordListener(this);

        navController.navigate(R.id.videoFragment);
        FragmentManager manager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        transaction.replace(R.id.fragment_container_view, (Fragment)fragment);
        transaction.commit();
    }
    private  ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {

                } else {
                    // Explain to the user that the feature is unavailable because the
                    // features requires a permission that the user has denied. At the
                    // same time, respect the user's decision. Don't link to system
                    // settings in an effort to convince the user to change their
                    // decision.
                }
            });
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Api api = new Api(getContext(), "");
        api.createUser();
        binding.buttonFirst.setOnClickListener(e->{
            Intent intent = new Intent(getContext() , FilePicker.class);
            String[] mimetypes = {"image/gif", "application/pdf"};
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
            someActivityResultLauncher.launch(intent);
        });


        binding.buttonVideo.setOnClickListener(e->{



            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_DENIED) {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA);

            }else {
                startVideo();
            }



        });
        binding.onboarding.setOnClickListener(e->{
            DocumentScanFrontFragment fragment =  DocumentScanFrontFragment.newInstance();
            fragment.setDocumentScanListener(this);

            navController.navigate(R.id.onboarding_fragment);
            FragmentManager manager = getActivity().getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();

            transaction.replace(R.id.fragment_container_view, (Fragment)fragment);
            transaction.commit();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onVideoRecordSuccess(String url) {
        Handler mainHandler = new Handler(Looper.getMainLooper());

        mainHandler.post( ()-> {


            FragmentManager supportFragmentManager =getActivity().getSupportFragmentManager();
            NavHostFragment navHostFragment = (NavHostFragment) supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main);
            navHostFragment.getChildFragmentManager().getFragments().get(0);
            UploaderFragment uploaderFragment = (UploaderFragment) navHostFragment.getChildFragmentManager().getFragments().get(0);
            uploaderFragment.setUrl(url);

        });


    }
    @Override
    public void onVideoRecordLoadingStarted(){
        Handler mainHandler = new Handler(Looper.getMainLooper());


        mainHandler.post( ()-> {

            navController.popBackStack();
            Bundle bundle = new Bundle();
            bundle.putString("url", "Loading...");
            navController.navigate(R.id.uploaderFragment, bundle);




        });

    }

    @Override
    public void onVideoRecordFailed() {

    }

    @Override
    public void onDocumentScanFrontSuccess(Bitmap bitmap) {
        Handler mainHandler = new Handler(Looper.getMainLooper());


        mainHandler.post( ()-> {


            DocumentScanBackFragment fragment =  DocumentScanBackFragment.newInstance();
            fragment.setDocumentScanListener(this);

            navController.navigate(R.id.scan_back);
            FragmentManager manager = getActivity().getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();

            transaction.replace(R.id.fragment_container_view, (Fragment)fragment);
            transaction.commit();


        });
    }

    @Override
    public void onDocumentScanBackSuccess(Bitmap bitmap) {
        Handler mainHandler = new Handler(Looper.getMainLooper());


        mainHandler.post( ()-> {


            SelfieAutoCaptureFragment fragment =  SelfieAutoCaptureFragment.newInstance();
            fragment.setLivelinessListener(this);

            navController.navigate(R.id.selfie);
            FragmentManager manager = getActivity().getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();

            transaction.replace(R.id.fragment_container_view, (Fragment)fragment);
            transaction.commit();


        });
    }

    @Override
    public void onSelfieCaptured(Bitmap bitmap) {
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