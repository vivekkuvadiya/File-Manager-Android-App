package com.vivek.filemanager.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.vivek.filemanager.MainActivity;
import com.vivek.filemanager.R;
import com.vivek.filemanager.adapter.StorageAdapter;
import com.vivek.filemanager.databinding.FragmentFavouriteBinding;
import com.vivek.filemanager.db.DbHelper;
import com.vivek.filemanager.interfaces.ActionModeListener;
import com.vivek.filemanager.interfaces.FileSelectedListener;
import com.vivek.filemanager.model.MediaModel;
import com.vivek.filemanager.utils.OpenFile;
import com.vivek.filemanager.utils.Utils;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.Executors;


public class FavouriteFragment extends Fragment implements FileSelectedListener, ActionModeListener {

    private FragmentFavouriteBinding binding;
    private ArrayList<File> files = new ArrayList<>();
    private StorageAdapter adapter;
    public static ActionModeListener actionModeListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Utils.setStatusBarColor(R.color.white,getActivity(),true);
        binding = FragmentFavouriteBinding.inflate(inflater);

        actionModeListener = this;

        binding.rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new StorageAdapter(getActivity(), this,"storage");
        binding.rv.setAdapter(adapter);

        setData();

        binding.ivBack.setOnClickListener(v -> getActivity().onBackPressed());

        return binding.getRoot();
    }

    private void setData() {
        files.clear();
        Executors.newSingleThreadExecutor().execute(() -> {
            files.addAll(DbHelper.getFavFile());
            adapter.addAll(files,this,null,binding.noData);
        });
    }

    @Override
    public void onFileSelect(ImageView imageView, File file) {
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

    @Override
    public void onBind(ImageView imageView, File file) {

    }

    @Override
    public void onIvSelectClick(ArrayList<File> files, int position, ImageView imageView) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("file", (Serializable) files);
        bundle.putInt("position", position);
        SelectFavouriteFragment selectFavouriteFragment = new SelectFavouriteFragment();
        selectFavouriteFragment.setArguments(bundle);
        getActivity().getSupportFragmentManager().beginTransaction().add(MainActivity.MAIN_CONTAINER, selectFavouriteFragment).addToBackStack(null).commit();
    }

    @Override
    public void onIvMediaSelectClick(ArrayList<MediaModel> files, int position, ImageView imageView) {

    }

    @Override
    public void onEventListener(int event) {
        if (event == Utils.EVENT_DELETE) {
            setData();
        }
    }
}