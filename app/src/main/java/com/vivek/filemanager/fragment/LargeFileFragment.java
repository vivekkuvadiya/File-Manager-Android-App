package com.vivek.filemanager.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.vivek.filemanager.MainActivity;
import com.vivek.filemanager.R;
import com.vivek.filemanager.adapter.StorageAdapter;
import com.vivek.filemanager.databinding.FragmentLargeFileBinding;
import com.vivek.filemanager.interfaces.ActionModeListener;
import com.vivek.filemanager.interfaces.FileSelectedListener;
import com.vivek.filemanager.model.MediaModel;
import com.vivek.filemanager.utils.OpenFile;
import com.vivek.filemanager.utils.Utils;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.Executors;

public class LargeFileFragment extends Fragment implements FileSelectedListener, ActionModeListener {

    private FragmentLargeFileBinding binding;
    private StorageAdapter adapter;
    private ArrayList<File> files = new ArrayList<>();
    public static ActionModeListener actionModeListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Utils.setStatusBarColor(R.color.white, getActivity(), true);
        binding = FragmentLargeFileBinding.inflate(inflater);

        actionModeListener = this;

        binding.rv.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new StorageAdapter(getActivity(), this, "largeFile");
        binding.rv.setAdapter(adapter);

        binding.loader.setVisibility(View.VISIBLE);
        Executors.newSingleThreadExecutor().execute(() -> {
            if (Utils.EXTERNAL_FILE != null) {
                files.addAll(getFile(Utils.EXTERNAL_FILE));
            }
            if (Utils.SD_CARD_FILE != null) {
                files.addAll(getFile(Utils.SD_CARD_FILE));
            }
//            getActivity().runOnUiThread(() -> binding.loader.setVisibility(View.GONE));
            adapter.addAll(files, this, binding.loader,binding.noData);
        });


        binding.ivBack.setOnClickListener(v -> getActivity().onBackPressed());
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        binding.et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        binding.et.addTextChangedListener(null);
    }

    ArrayList<File> getFile(File file) {

        ArrayList<File> fileArrayList = new ArrayList<>();
        File[] files = file.listFiles();
        if (files != null) {
            for (File singleFile : files) {
                if (singleFile.isDirectory() && !singleFile.isHidden()) {
                    fileArrayList.addAll(getFile(singleFile));
                } else {
                    if (singleFile.length() > 50000000) {
                        Log.d("LARGEFILE", "getFile: " + singleFile);
                        if (!singleFile.isHidden() && singleFile.getName().toLowerCase().endsWith(".zip") || singleFile.getName().toLowerCase().endsWith(".zipx")) {
                            fileArrayList.add(singleFile);
                        } else if (!singleFile.isHidden() && singleFile.getName().toLowerCase().endsWith(".jpg") || singleFile.getName().toLowerCase().endsWith(".jpeg") || singleFile.getName().toLowerCase().endsWith(".png")) {
                            fileArrayList.add(singleFile);
                        } else if (!singleFile.isHidden() && singleFile.getName().toLowerCase().endsWith(".mp4")) {
                            fileArrayList.add(singleFile);
                        } else if (!singleFile.isHidden() && singleFile.getName().toLowerCase().endsWith(".mp3") || singleFile.getName().toLowerCase().endsWith(".wav")) {
                            fileArrayList.add(singleFile);
                        } else if (!singleFile.isHidden() && singleFile.getName().toLowerCase().endsWith(".apk")) {
                            fileArrayList.add(singleFile);
                        } else if (!singleFile.isHidden() && singleFile.getName().toLowerCase().endsWith(".doc") || singleFile.getName().toLowerCase().endsWith(".docx") || singleFile.getName().toLowerCase().endsWith(".xls") || singleFile.getName().toLowerCase().endsWith(".xlsx") || singleFile.getName().toLowerCase().endsWith(".txt") || singleFile.getName().toLowerCase().endsWith(".ppt") || singleFile.getName().toLowerCase().endsWith(".pdf")) {
                            fileArrayList.add(singleFile);
                        }
                    }
                }
            }
        }
        return fileArrayList;
    }

    @Override
    public void onFileSelect(ImageView imageView, File file) {
        if (file.isFile()){
            OpenFile.open(file);
        }
    }

    @Override
    public void onBind(ImageView imageView, File file) {

    }

    @Override
    public void onIvSelectClick(ArrayList<File> files, int position, ImageView imageView) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("List", (Serializable) files);
        bundle.putInt("Position", position);
        bundle.putString("From", "Large");
        SelectAppsFragment selectAppsFragment = new SelectAppsFragment();
        selectAppsFragment.setArguments(bundle);
        getActivity().getSupportFragmentManager().beginTransaction().add(MainActivity.MAIN_CONTAINER, selectAppsFragment).addToBackStack(null).commit();
    }

    @Override
    public void onIvMediaSelectClick(ArrayList<MediaModel> files, int position, ImageView imageView) {

    }

    @Override
    public void onEventListener(int event) {
        if (event == Utils.EVENT_ADD_TO_FAV){
            getActivity().onBackPressed();
        }
    }
}