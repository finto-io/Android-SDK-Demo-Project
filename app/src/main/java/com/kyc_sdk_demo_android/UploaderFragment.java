package com.kyc_sdk_demo_android;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;


public class UploaderFragment extends Fragment {
    private static final String ARG_PARAM1 = "url";
    private TextView textView = null;
    private Button button = null;
    private String url;

    public UploaderFragment() {
    }


    public static UploaderFragment newInstance(String url) {
        UploaderFragment fragment = new UploaderFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, url);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            url = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Result");
        return inflater.inflate(R.layout.fragment_uploader, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        textView = view.findViewById(R.id.uploaderText);
        textView.setText(url);
        button = view.findViewById(R.id.open_link);
        button.setVisibility(View.GONE);
        button.setOnClickListener(e -> {
            ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Copy link", textView.getText().toString());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(getContext(), "Copied to clipboard", Toast.LENGTH_LONG).show();

        });
    }

    public static void openCustomTab(Activity activity, CustomTabsIntent customTabsIntent, Uri uri) {
        String packageName = "com.android.chrome";
        if (packageName != null) {
            customTabsIntent.intent.setPackage(packageName);
            customTabsIntent.launchUrl(activity, uri);
        } else activity.startActivity(new Intent(Intent.ACTION_VIEW, uri));
    }


    public void setUrl(String url) {
        textView.setText(url);
        button.setVisibility(View.VISIBLE);
    }
}