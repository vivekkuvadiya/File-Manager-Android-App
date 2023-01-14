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

import com.vivek.filemanager.adapter.SongAdapter;
import com.vivek.filemanager.databinding.FragmentSongBinding;
import com.vivek.filemanager.interfaces.ActionModeListener;
import com.vivek.filemanager.interfaces.FileSelectedListener;
import com.vivek.filemanager.model.MediaModel;
import com.vivek.filemanager.utils.Utils;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.Executors;


public class SongFragment extends Fragment implements FileSelectedListener, ActionModeListener {

    private static final String TAG = "AllAUDIOFILES";
    private FragmentSongBinding binding;

    private static final String ARG_PARAM1 = "Path";
    private static final String ARG_PARAM2 = "param2";

    ArrayList<File> allAudio = new ArrayList<>();
    SongAdapter songAdapter;

    private String mParam1;
    private String mParam2;
    public static ActionModeListener actionModeListener;

    private static boolean isStartActionMode = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSongBinding.inflate(inflater);

        actionModeListener = this;

        binding.rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        songAdapter = new SongAdapter(getActivity(), this);
        binding.rv.setAdapter(songAdapter);

        binding.loader.setVisibility(View.VISIBLE);
        setData();


//        getActivity().getSupportFragmentManager().beginTransaction().add(ImageFragment.CON, new SongSelectFragment()).addToBackStack(null).commit();

        /*Bundle bundle = new Bundle();
        bundle.putString("FILE", "Images");
        ImageFragment imageFragment = new ImageFragment();
        imageFragment.setArguments(bundle);
        getChildFragmentManager().beginTransaction().replace(binding.container.getId(),imageFragment).addToBackStack(null).commit();*/


        return binding.getRoot();
    }

    private void setData() {
        allAudio.clear();
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {

                if (mParam1 != null) {

                    allAudio.addAll(audio(new File(mParam1)));

                } else {

                    if (Utils.EXTERNAL_FILE != null) {
                        allAudio.addAll(audio(Utils.EXTERNAL_FILE));
                    }
                    if (Utils.SD_CARD_FILE != null) {
                        allAudio.addAll(audio(Utils.SD_CARD_FILE));
                    }
                }
//                getActivity().runOnUiThread(()->binding.loader.setVisibility(View.GONE));
                songAdapter.addAll(allAudio,SongFragment.this,binding.loader,binding.noData);
            }
        });
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
                songAdapter.getFilter().filter(s);
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

    ArrayList<File> audio(File file) {
        ArrayList<File> fileArrayList = new ArrayList<>();
        File[] files = file.listFiles();
        if (files != null) {
            for (File singleFile : files) {
                if (singleFile.isDirectory() && !singleFile.isHidden()) {
                    fileArrayList.addAll(audio(singleFile));
                } else {
                    if (!singleFile.isHidden() && singleFile.getName().toLowerCase().endsWith(".mp3") || singleFile.getName().toLowerCase().endsWith(".wav")) {
                        fileArrayList.add(singleFile);
                    }
                }
            }
        }

        return fileArrayList;
    }


    @Override
    public void onFileSelect(ImageView imageView, File file) {
        Bundle bundle = new Bundle();
        bundle.putInt("position", allAudio.indexOf(file));
        bundle.putSerializable("val", (Serializable) allAudio);
        SongSelectFragment songSelectFragment = new SongSelectFragment();
        songSelectFragment.setArguments(bundle);
        getActivity().getSupportFragmentManager().beginTransaction().add(ImageFragment.CON, songSelectFragment).addToBackStack(null).commit();
    }

    @Override
    public void onBind(ImageView imageView, File file) {

    }

    @Override
    public void onIvSelectClick(ArrayList<File> files, int position, ImageView imageView) {

    }

    @Override
    public void onIvMediaSelectClick(ArrayList<MediaModel> files, int position, ImageView imageView) {

    }

    @Override
    public void onEventListener(int event) {
        switch (event) {
            case Utils.EVENT_DELETE:
                setData();
                getActivity().onBackPressed();
                break;
            case Utils.EVENT_ADD_TO_FAV:
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