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

import com.vivek.filemanager.adapter.AudioFolderAdapter;
import com.vivek.filemanager.databinding.FragmentSongFolderBinding;
import com.vivek.filemanager.interfaces.ActionModeListener;
import com.vivek.filemanager.interfaces.FileSelectedListener;
import com.vivek.filemanager.model.MediaModel;
import com.vivek.filemanager.utils.Utils;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.Executors;

public class SongFolderFragment extends Fragment implements FileSelectedListener, ActionModeListener {


    private FragmentSongFolderBinding binding;
    private String path;
    private String mParam2;
    public static onBacks onBacks;
    private AudioFolderAdapter imageFolderAdapter;
    ArrayList<MediaModel> f = new ArrayList<>();
    public static ActionModeListener actionModeListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            path = getArguments().getString("Path");
//            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentSongFolderBinding.inflate(inflater);

        actionModeListener = this;

        binding.rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        imageFolderAdapter = new AudioFolderAdapter(getActivity(), "aud", this);
        binding.rv.setAdapter(imageFolderAdapter);

        /*File file;

        if (path != null) {
            binding.rv.setVisibility(View.GONE);
            file = new File(path);
        } else {
            file = Utils.EXTERNAL_FILE;

            if (Utils.EXTERNAL_FILE != null) {
                allAudio.addAll(audio(Utils.EXTERNAL_FILE));
            }
            if (Utils.SD_CARD_FILE != null) {
                allAudio.addAll(audio(Utils.SD_CARD_FILE));
            }

        }*/

        binding.loader.setVisibility(View.VISIBLE);
        setData();

        return binding.getRoot();
    }

    private void setData() {
        f.clear();
        Executors.newSingleThreadExecutor().execute(() -> {
//                = f(file);
            if (path != null) {
                f.addAll(f(new File(path)));
            } else {
                if (Utils.EXTERNAL_FILE != null) {
                    f.addAll(f(Utils.EXTERNAL_FILE));
                }
                if (Utils.SD_CARD_FILE != null) {
                    f.addAll(f(Utils.SD_CARD_FILE));
                }
            }
//            getActivity().runOnUiThread(()->binding.loader.setVisibility(View.GONE));
            imageFolderAdapter.addAll(f,this,binding.loader,binding.noData);
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
                imageFolderAdapter.getFilter().filter(s);
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

    private ArrayList<MediaModel> f(File file) {
        ArrayList<MediaModel> fileArrayList = new ArrayList<>();

        File[] files = file.listFiles();

        if (files != null) {

            for (File file1 : files) {

                if (file1.isDirectory() && !file1.isHidden()) {
                    File direcory = isDirecory(file1);
                    if (direcory != null) {
                        fileArrayList.add(new MediaModel(direcory, getInt(direcory)));
                    }
                }
            }
        }

        return fileArrayList;
    }

    private int getInt(File file) {
        int count = 0;
        File[] files = file.listFiles();
        for (File file1 : files) {
            if (file1.getName().toLowerCase().endsWith(".mp3") || file1.getName().toLowerCase().endsWith(".wav") || file1.getName().toLowerCase().endsWith(".amr")) {
                count += 1;
            }
        }
        return count;
    }

    private File isDirecory(File file) {
        File[] files = file.listFiles();

        if (files != null) {

            for (File file1 : files) {

                if (file1.isDirectory() && !file1.isHidden()) {
                    File dir = isDir(file1);
                    if (dir != null) {
                        return file1;
                    }

                } else {
                    if (!file1.isHidden() && file1.getName().toLowerCase().endsWith(".mp3") || file1.getName().toLowerCase().endsWith(".wav") || file1.getName().toLowerCase().endsWith(".amr")) {
                        return file;
                    }
                }

            }

        }
        return null;
    }

    private File isDir(File file) {
        File[] files = file.listFiles();

        if (files != null) {

            for (File file1 : files) {

                if (file1.isDirectory() && !file1.isHidden()) {
                    isDir(file1);

                } else {
                    if (!file1.isHidden() && file1.getName().toLowerCase().endsWith(".mp3")  || file1.getName().toLowerCase().endsWith(".wav") || file1.getName().toLowerCase().endsWith(".amr")) {
                        return file1;
                    }
                }
            }

        }
        return null;
    }

    @Override
    public void onFileSelect(ImageView imageView, File file) {
        if (file.isDirectory()) {
            Bundle bundle = new Bundle();
            bundle.putString("Path", file.getAbsolutePath());
            SongFragment songFragment = new SongFragment();
            songFragment.setArguments(bundle);
            getActivity().getSupportFragmentManager().beginTransaction().add(binding.con.getId(), songFragment).addToBackStack(null).commit();
        }
    }

    @Override
    public void onBind(ImageView imageView, File file) {

    }

    @Override
    public void onIvSelectClick(ArrayList<File> files, int position, ImageView imageView) {

    }

    @Override
    public void onIvMediaSelectClick(ArrayList<MediaModel> files, int position, ImageView imageView) {
        Bundle bundle = new Bundle();
        bundle.putInt("position", position);
        bundle.putSerializable("val", (Serializable) files);
        bundle.putString("from", "aud");
        SelectVideoFolderFragment selectVideoFolderFragment = new SelectVideoFolderFragment();
        selectVideoFolderFragment.setArguments(bundle);
        getActivity().getSupportFragmentManager().beginTransaction().add(ImageFragment.CON, selectVideoFolderFragment).addToBackStack(null).commit();
    }

    @Override
    public void onEventListener(int event) {

        switch (event) {
            case Utils.EVENT_DELETE:
                setData();
                getActivity().onBackPressed();
                break;
            case Utils.EVENT_ADD_TO_FAV:
            case Utils.EVENT_CLOSE:
                getActivity().onBackPressed();
                break;
            case Utils.EVENT_COPY:
            case Utils.EVENT_MOVE:
                setData();
                new Handler(Looper.getMainLooper()).post(() -> getActivity().onBackPressed());
                break;
        }
    }

    public interface onBacks {
        void onBacks();
    }

}
