package com.vivek.filemanager.fragment;

import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vivek.filemanager.MainActivity;
import com.vivek.filemanager.R;
import com.vivek.filemanager.adapter.SearchAdapter;
import com.vivek.filemanager.databinding.FragmentSearchBinding;
import com.vivek.filemanager.utils.OpenFile;
import com.vivek.filemanager.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.concurrent.Executors;


public class SearchFragment extends Fragment implements SearchAdapter.onFileClick{

    private FragmentSearchBinding binding;
    private ArrayList<File> fileList = new ArrayList<>();
    private SearchAdapter searchAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Utils.setStatusBarColor(R.color.white,getActivity(),true);
        binding = FragmentSearchBinding.inflate(inflater);

        searchAdapter = new SearchAdapter(getActivity(),this);
        binding.rv.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rv.setAdapter(searchAdapter);

        getData();

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
                    searchAdapter.getFilter().filter(s);
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

    private void getData() {
        Executors.newSingleThreadExecutor().execute(() -> {
            if (Utils.EXTERNAL_FILE != null) {
                fileList.addAll(shortFile(getFile(Utils.EXTERNAL_FILE)));
            } else if (Utils.SD_CARD_FILE != null) {
                fileList.addAll(shortFile(getFile(Utils.SD_CARD_FILE)));
            }

            searchAdapter.addAll(fileList);

        });
    }

    ArrayList<File> shortFile(ArrayList<File> files) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            files.sort(Comparator.comparing(File::lastModified));
        }
        return files;
    }

    ArrayList<File> getFile(File file) {

        ArrayList<File> fileArrayList = new ArrayList<>();
        File[] files = file.listFiles();
        if (files != null) {
            for (File singleFile : files) {
                if (singleFile.isDirectory() && !singleFile.isHidden()) {
                    fileArrayList.addAll(getFile(singleFile));
                }                                           else {
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

        return fileArrayList;
    }

    @Override
    public void onClick(File file) {
        if (file.isDirectory()){
            Bundle bundle = new Bundle();
            bundle.putString("path", file.getAbsolutePath());
            StorageFragment storageFragment = new StorageFragment();
            storageFragment.setArguments(bundle);
            getActivity().getSupportFragmentManager().beginTransaction().add(MainActivity.MAIN_CONTAINER, storageFragment).addToBackStack(null).commit();
        }else {
            OpenFile.open(file);
        }
    }
}