package com.vivek.filemanager.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vivek.filemanager.databinding.FragmentSongFoldBinding;

import java.io.File;
import java.util.ArrayList;


public class SongFoldFragment extends Fragment {

    private FragmentSongFoldBinding binding;
    private static final String ARG_PARAM1 = "param1";
    public static int CONT;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSongFoldBinding.inflate(inflater);

        CONT =  binding.container.getId();
//        ArrayList<File> listFiles = f(Utils.EXTERNAL_FILE);

        getChildFragmentManager().beginTransaction().add(CONT,new SongFolderFragment()).addToBackStack(null).commit();

        /*Log.d("ASDASDDASDA", "onCreateView: " + listFiles.size());
        for (int i = 0; i < listFiles.size(); i++) {
            Log.d("ASDASDDASDA", "onCreateView: " + listFiles.get(i));
        }*/


        return binding.getRoot();
    }

    public static ArrayList<File> getListFiles(File parentDir) {
        ArrayList<File> inFiles = new ArrayList<File>();
        File[] files;
        files = parentDir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.getName().endsWith(".mp4") ||
                        file.getName().endsWith(".gif")) {
                    if (!inFiles.contains(file)) inFiles.add(file);

                    if (!inFiles.contains(file)) inFiles.add(file);
                }
            }
        }
        return inFiles;
    }

    ArrayList<String> findVideos(File dir) {
        ArrayList<String> list = new ArrayList<>();
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    list.addAll(findVideos(file));
                } else if (file.getAbsolutePath().contains(".mp4")) {
                    list.add(file.getAbsolutePath());
                }
            }
        }

        return list;
    }

    /*private void searchTXT(File dir) {
        File[] files = dir.listFiles();

        for (File file : files) {
            if (file.isFile() && isTXT(file)) {
                allTXT.add(file);
                Log.i("TXT", file.getName());
            } else if (file.isDirectory()) {
                searchTXT(file.getAbsoluteFile());
            }
        }
    }*/

    private ArrayList<File> f(File file){
        ArrayList<File> fileArrayList  = new ArrayList<>();

        File[] files = file.listFiles();

        if (files != null){

            for (File file1 : files){

                if (file1.isDirectory()){
                    boolean direcory = isDirecory(file1);
                    if (direcory){
                        fileArrayList.add(file1);
                    }
                }

            }

        }

        return fileArrayList;

    }

    private boolean isDirecory(File file){
        File[] files = file.listFiles();

        if (files != null){

            for (File file1 : files){
                if (file1.getName().endsWith(".mp4")){
                    return true;
                }
            }

        }
        return false;
    }

    private boolean isTXT(File file){
        boolean is = false;
        if(file.getName().endsWith(".mp4")){
            is = true;
            Log.e("", "mp4 files -->"+is);
        }
        return is;
    }


}