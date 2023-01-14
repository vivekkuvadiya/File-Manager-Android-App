package com.vivek.filemanager.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.vivek.filemanager.MainActivity;
import com.vivek.filemanager.R;
import com.vivek.filemanager.adapter.ZipAdapter;
import com.vivek.filemanager.databinding.FragmentZipBinding;
import com.vivek.filemanager.interfaces.ActionModeListener;
import com.vivek.filemanager.interfaces.FileSelectedListener;
import com.vivek.filemanager.model.MediaModel;
import com.vivek.filemanager.utils.OpenFile;
import com.vivek.filemanager.utils.Utils;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.Executors;


public class ZipFragment extends Fragment implements FileSelectedListener, ActionModeListener {

    private FragmentZipBinding binding;
    private ArrayList<File> zipList = new ArrayList<>();
    private ZipAdapter adapter;
    public static ActionModeListener actionModeListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentZipBinding.inflate(inflater);
        Utils.setStatusBarColor(R.color.white,getActivity(),true);

        binding.rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new ZipAdapter(getActivity(), this);
        binding.rv.setAdapter(adapter);

        binding.loader.setVisibility(View.VISIBLE);
        setData();

        actionModeListener = this;

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
    public void onPause() {
        super.onPause();
        binding.et.addTextChangedListener(null);
    }

    private void setData() {
        zipList.clear();
        Executors.newSingleThreadExecutor().execute(() -> {
        if (Utils.EXTERNAL_FILE != null) {
            zipList.addAll(zip(Utils.EXTERNAL_FILE));
        }
        if (Utils.SD_CARD_FILE != null) {
            zipList.addAll(zip(Utils.SD_CARD_FILE));
        }
//        getActivity().runOnUiThread(()->binding.loader.setVisibility(View.GONE));

//        if (zipList.size() > 0) {
            adapter.addAll(zipList,this,binding.loader,binding.noData);
//        }
        });
    }

    ArrayList<File> zip(File file) {

        ArrayList<File> fileArrayList = new ArrayList<>();
        File[] files = file.listFiles();
        if (files != null) {
            for (File singleFile : files) {
                if (singleFile.isDirectory() && !singleFile.isHidden()) {
                    fileArrayList.addAll(zip(singleFile));
                } else {
                    if (!singleFile.isHidden() && singleFile.getName().toLowerCase().endsWith(".zip") || singleFile.getName().toLowerCase().endsWith(".zipx")) {
                        fileArrayList.add(singleFile);
                    }
                }
            }

        }

        return fileArrayList;
    }


    @Override
    public void onFileSelect(ImageView imageView, File file) {
        OpenFile.open(file);
    }

    @Override
    public void onBind(ImageView imageView, File file) {

    }

    @Override
    public void onIvSelectClick(ArrayList<File> files, int position, ImageView imageView) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("List", (Serializable) files);
        bundle.putInt("Position", position);
        bundle.putString("From", "Zip");
        SelectAppsFragment selectAppsFragment = new SelectAppsFragment();
        selectAppsFragment.setArguments(bundle);
        getActivity().getSupportFragmentManager().beginTransaction().add(MainActivity.MAIN_CONTAINER, selectAppsFragment).addToBackStack(null).commit();

    }

    @Override
    public void onIvMediaSelectClick(ArrayList<MediaModel> files, int position, ImageView imageView) {

    }

    @Override
    public void onEventListener(int event) {
        switch (event) {
            case Utils.EVENT_ADD_TO_FAV:

            case Utils.EVENT_CLOSE:
                getActivity().onBackPressed();
                break;

            case Utils.EVENT_DELETE:
                setData();
                getActivity().onBackPressed();
                break;

            case Utils.EVENT_COPY:
            case Utils.EVENT_MOVE:
                setData();
                new Handler(Looper.getMainLooper()).post(() -> getActivity().onBackPressed());
                break;
        }
    }
}