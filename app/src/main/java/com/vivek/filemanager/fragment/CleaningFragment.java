package com.vivek.filemanager.fragment;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.vivek.filemanager.R;
import com.vivek.filemanager.databinding.FragmentCleanningBinding;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Executors;

import pl.droidsonroids.gif.GifDrawable;


public class CleaningFragment extends Fragment {

    private FragmentCleanningBinding binding;
    private ArrayList<File> files;
    private long filesSize = 0;
    private String file;
    private GifDrawable gifDrawable;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            files = (ArrayList<File>) getArguments().getSerializable("list");
            filesSize = getArguments().getLong("size");
            file = getArguments().getString("file");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCleanningBinding.inflate(inflater);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        binding.tvFileName.setText(file);
        binding.tvSize.setText(Formatter.formatFileSize(getContext(), filesSize));
        binding.tvFileSize.setText(Formatter.formatFileSize(getContext(), filesSize));
        binding.btnStartClean.setText(String.format("Clean Up %s", Formatter.formatFileSize(getActivity(), filesSize)));

        binding.imageView11.setOnClickListener(v -> getActivity().onBackPressed());

        binding.btnStartClean.setOnClickListener(v -> {
            binding.btnStartClean.setVisibility(View.GONE);
            binding.tvSize.setVisibility(View.GONE);
            binding.ivGif.setVisibility(View.VISIBLE);
            if (filesSize > 0) {
                try {
                    gifDrawable = null;
                    gifDrawable = new GifDrawable(getResources(), R.drawable.clean_gif);
                    binding.ivGif.setImageDrawable(gifDrawable);
                    gifDrawable.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            Executors.newSingleThreadExecutor().execute(() -> {
                clean(files);
                getActivity().runOnUiThread(() -> {
                    try {
                        gifDrawable = null;
                        gifDrawable = new GifDrawable(getResources(), R.drawable.cleaned_gif);
                        binding.ivGif.setImageDrawable(gifDrawable);
                        gifDrawable.setLoopCount(1);
                        gifDrawable.start();
                        gifDrawable.addAnimationListener(i -> {
                            binding.tvCleaned.setText(String.format("%s Cleaned.", Formatter.formatFileSize(getActivity(), filesSize)));
                            binding.tvCleaned.setVisibility(View.VISIBLE);
                        });

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            });
        });
        return binding.getRoot();
    }

    private void clean(ArrayList<File> files) {
        for (File file : files) {
            if (file.isDirectory()) {
                try {
                    FileUtils.deleteDirectory(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                file.delete();
            }
        }
    }


   /* public static void main(String[] args) {
        *//*for (int i = 1; i <= 5; i++) {
            for (int a = i;a<=5-1; a++){
                System.out.print(" ");
            }
            for (int j = 1;j<=i;j++){  //1 <=2
                System.out.print(" *");
            }
            System.out.println();
        }*//*
    }*/
}