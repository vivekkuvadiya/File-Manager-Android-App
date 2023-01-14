package com.vivek.filemanager.fragment;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.vivek.filemanager.R;
import com.vivek.filemanager.adapter.StorageAdapter;
import com.vivek.filemanager.databinding.FragmentSelectFavouriteBinding;
import com.vivek.filemanager.db.DbHelper;
import com.vivek.filemanager.interfaces.FileSelectedListener;
import com.vivek.filemanager.model.MediaModel;
import com.vivek.filemanager.utils.Utils;

import java.io.File;
import java.util.ArrayList;


public class SelectFavouriteFragment extends Fragment implements FileSelectedListener {

    private FragmentSelectFavouriteBinding binding;
    private ArrayList<File> files;
    int position;
    private ArrayList<File> selectedList = new ArrayList<>();

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null){
            files = (ArrayList<File>) getArguments().getSerializable("file");
            position = getArguments().getInt("position");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSelectFavouriteBinding.inflate(inflater);
        selectedList.add(files.get(position));
        binding.rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        StorageAdapter storageAdapter = new StorageAdapter(getActivity(),this,"storage");
        binding.rv.setAdapter(storageAdapter);
        storageAdapter.addAll(files,this,null,null);
        binding.rv.scrollToPosition(position);

        binding.lyCancel.setOnClickListener(v -> getActivity().onBackPressed());

        binding.lyAction.setOnClickListener(v -> {
            for (File file : selectedList){
                DbHelper.removeFavourite(file.getAbsolutePath());
            }
            FavouriteFragment.actionModeListener.onEventListener(Utils.EVENT_DELETE);
            getActivity().onBackPressed();
        });

        return binding.getRoot();
    }

    @Override
    public void onFileSelect(ImageView imageView, File file) {

    }

    @Override
    public void onBind(ImageView imageView, File file) {
        if (isAvailable(file)) {
            imageView.setImageResource(R.drawable.ic_selected);
        } else {
            imageView.setImageResource(R.drawable.no_select);
        }
    }

    @Override
    public void onIvSelectClick(ArrayList<File> files, int position, ImageView imageView) {
        if (imageView.getDrawable().getConstantState().equals(getResources().getDrawable(R.drawable.ic_selected).getConstantState())) {
            selectedList.remove(files.get(position));
            imageView.setImageResource(R.drawable.no_select);
        } else {
            selectedList.add(files.get(position));
            imageView.setImageResource(R.drawable.ic_selected);
        }
        binding.tvCounter.setText(String.format("%d Selected", selectedList.size()));
    }

    @Override
    public void onIvMediaSelectClick(ArrayList<MediaModel> files, int position, ImageView imageView) {

    }

    private boolean isAvailable(File file) {
        for (int i = 0; i < selectedList.size(); i++) {
            if (file.equals(selectedList.get(i))) {
                return true;
            }
        }
        return false;
    }
}