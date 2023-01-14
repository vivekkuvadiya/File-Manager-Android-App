package com.vivek.filemanager.fragment;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vivek.filemanager.adapter.NestVideoAdapter;
import com.vivek.filemanager.databinding.FragmentVideoBinding;
import com.vivek.filemanager.model.ImgModel;
import com.vivek.filemanager.model.ImgShortModel;
import com.vivek.filemanager.utils.Utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.concurrent.Executors;


public class VideoFragment extends Fragment {

    private FragmentVideoBinding binding;
    private ArrayList<ImgModel> videos = new ArrayList<>();
    private ArrayList<ImgShortModel> imgShortModels = new ArrayList<>();
    String path;
//    MediaMetadataRetriever retriever;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            path = getArguments().getString("PATH");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentVideoBinding.inflate(inflater);

//        retriever = new MediaMetadataRetriever();


        binding.rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        NestVideoAdapter adapter = new NestVideoAdapter(getActivity(), 1, 0);
        binding.rv.setAdapter(adapter);

        binding.loader.setVisibility(View.VISIBLE);

        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {

                if (path != null) {
                    videos.addAll(vid(new File(path)));
                } else {
                    if (Utils.EXTERNAL_FILE != null) {
                        videos.addAll(vid(Utils.EXTERNAL_FILE));
                    }
                    if (Utils.SD_CARD_FILE != null) {
                        videos.addAll(vid(Utils.SD_CARD_FILE));
                    }
                }

                Collections.sort(videos, (o1, o2) -> String.valueOf(o2.file.lastModified()).compareTo(String.valueOf(o1.file.lastModified())));


        /*for (ImgModel imgModel:videos){
            ArrayList<ImgModel> img  = new ArrayList<>();
            for (ImgModel imgModel1 : videos){
                if (imgModel.date.equals(imgModel1.date)){
                    img.add(new ImgModel(imgModel1.file, imgModel1.date));
                }
            }
            imgShortModels.add(new ImgShortModel(img));
        }*/

                ArrayList<String> date = new ArrayList<>();
                for (ImgModel imgModel1 : videos) {

                    if (isCont(date, imgModel1.date)) {
                        date.add(imgModel1.date);
                    }

                }
                for (int i = 0; i < date.size(); i++) {
                    ArrayList<ImgModel> imgModels = new ArrayList<>();
                    for (ImgModel imgModel1 : videos) {
                        if (date.get(i).equals(imgModel1.date)) {
//                            retriever.setDataSource(imgModel1.file.getPath());
//                            imgModels.add(new ImgModel(imgModel1.file, imgModel1.date, Utils.getDurationBreakdown(Long.parseLong(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)))));
                            imgModels.add(new ImgModel(imgModel1.file, imgModel1.date, Utils.getDurationBreakdown(getDuration(imgModel1.file.getAbsolutePath()))));
                        }
                    }
                    if (date.get(i).equals(new SimpleDateFormat("dd/MM/yyyy").format(new Date().getTime()))) {
                        imgShortModels.add(new ImgShortModel(imgModels, "Today"));
                    } else if (date.get(i).equals(getYesterday())) {
                        imgShortModels.add(new ImgShortModel(imgModels, "Yesterday"));
                    } else {
                        imgShortModels.add(new ImgShortModel(imgModels, date.get(i)));
                    }
                }

//                getActivity().runOnUiThread(()->binding.loader.setVisibility(View.GONE));

//                if (videos.size() > 0) {
                adapter.addAll(imgShortModels, VideoFragment.this, binding.loader, binding.noData);
//                }
            }
        });


        return binding.getRoot();
    }

    private long getDuration(String path) {
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Video.VideoColumns.DURATION};
        String selection = MediaStore.Video.VideoColumns.DATA + "=?";
        String[] selectionArgs = new String[]{path};


        if (getActivity() != null) {
            Cursor c = getActivity().getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (c != null) {
                while (c.moveToNext()) {
                    long duration = c.getLong(c.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.DURATION));
                    c.close();
                    return duration;
                }
            } else {
                c.close();
                return 0;
            }
            c.close();
        }
        return 0;
    }

    String getYesterday() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        return new SimpleDateFormat("dd/MM/yyyy").format(calendar.getTime());
    }

    private boolean isCont(ArrayList<String> list, String s) {
        for (String lists : list) {
            if (lists.equals(s)) {
                return false;
            }
        }
        return true;
    }

    ArrayList<ImgModel> vid(File file) {
        ArrayList<ImgModel> fileArrayList = new ArrayList<>();
        File[] files = file.listFiles();
        if (files != null) {
            for (File singleFile : files) {
                if (singleFile.isDirectory() && !singleFile.isHidden() && !singleFile.getParent().equals("Telegram") && !singleFile.getName().equals("Telegram")) {
                    fileArrayList.addAll(vid(singleFile));
                } else {
                    if (!singleFile.isHidden() && !singleFile.getParent().equals("Telegram") && singleFile.getName().toLowerCase().endsWith(".mp4")) {
                        fileArrayList.add(new ImgModel(singleFile, String.valueOf(DateFormat.format("dd/MM/yyyy", singleFile.lastModified())), null));
                    }
                }
            }
        }
        return fileArrayList;
    }

}