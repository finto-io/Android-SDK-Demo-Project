package com.kyc_sdk_demo_android;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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

import com.kyc_sdk_demo_android.databinding.FragmentMainBinding;

import kyc.BaeError;
import kyc.FilePicker;
import kyc.Uploader;
import kyc.UploaderFile;
import kyc.ob.Api;
import kyc.ob.BaeInitializer;
import kyc.ob.VideoFragment;

public class MainFragment extends Fragment implements VideoFragment.VideoRecordListener {

    private FragmentMainBinding binding;
    private NavController navController;
    private NavHostFragment navHostFragment;

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    startVideo();
                }
            });

    private ActivityResultLauncher<Intent> filePickerLauncher = registerForActivityResult(
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
                        uploader.uploadDocuments(new UploaderFile.UploaderCallback() {
                            @Override
                            public void onSuccess(UploaderFile uploaderFile) {
                                Handler mainHandler = new Handler(Looper.getMainLooper());

                                Runnable myRunnable = () -> {
                                    FragmentManager supportFragmentManager = getActivity()
                                            .getSupportFragmentManager();
                                    NavHostFragment navHostFragment = (NavHostFragment) supportFragmentManager
                                            .findFragmentById(R.id.nav_host_fragment_content_main);
                                    navHostFragment.getChildFragmentManager().getFragments().get(0);
                                    UploaderFragment uploaderFragment = (UploaderFragment) navHostFragment
                                            .getChildFragmentManager()
                                            .getFragments()
                                            .get(0);
                                    uploaderFragment.setUrl(uploaderFile.fileUrl);
                                };

                                mainHandler.post(myRunnable);
                            }

                            @Override
                            public void onFailed(BaeError baeError) {
                                Toast.makeText(getContext(), baeError.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("KYC SDK DEMO");

        BaeInitializer di = new BaeInitializer(getContext(), R.raw.iengine);
        di.initialize();
        binding = FragmentMainBinding.inflate(inflater, container, false);
        navHostFragment = (NavHostFragment) getActivity()
                .getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_content_main);
        navController = navHostFragment.getNavController();
        navController.popBackStack();
        return binding.getRoot();
    }

    public void startVideo() {
        VideoFragment fragment = new VideoFragment();
        fragment.setVideoRecordListener(this);
        navController.navigate(R.id.videoFragment);
        Handler mainHandler = new Handler(Looper.getMainLooper());

        Runnable myRunnable = () -> {
            FragmentManager manager = getActivity().getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.add(R.id.fragment_container_view, fragment);
            transaction.commit();
        };
        mainHandler.post(myRunnable);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Api api = new Api(getContext(), "");
        api.createUser();
        binding.buttonFirst.setOnClickListener(e -> {
            Intent intent = new Intent(getContext(), FilePicker.class);
            String[] mimetypes = {"image/gif", "application/pdf"};
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
            filePickerLauncher.launch(intent);
        });
        binding.buttonVideo.setOnClickListener(e -> {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_DENIED) {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA);
            } else startVideo();
        });
        binding.onboarding.setOnClickListener(e -> navController.navigate(R.id.onboarding_fragment));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onVideoRecordSuccess(String url) {
        Handler mainHandler = new Handler(Looper.getMainLooper());

        mainHandler.post(() -> {
            FragmentManager supportFragmentManager = getActivity().getSupportFragmentManager();
            NavHostFragment navHostFragment = (NavHostFragment) supportFragmentManager
                    .findFragmentById(R.id.nav_host_fragment_content_main);
            navHostFragment.getChildFragmentManager().getFragments().get(0);
            UploaderFragment uploaderFragment = (UploaderFragment) navHostFragment
                    .getChildFragmentManager()
                    .getFragments()
                    .get(0);
            uploaderFragment.setUrl(url);
        });
    }

    @Override
    public void onVideoRecordFailed(BaeError baeError) {
        Toast.makeText(getContext(), baeError.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onVideoRecordLoadingStarted() {
        Handler mainHandler = new Handler(Looper.getMainLooper());
        mainHandler.post(() -> {
            navController.popBackStack();
            Bundle bundle = new Bundle();
            bundle.putString("url", "Loading...");
            navController.navigate(R.id.uploaderFragment, bundle);
        });
    }


}