package com.vivek.filemanager.adapter;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vivek.filemanager.MainActivity;
import com.vivek.filemanager.R;
import com.vivek.filemanager.databinding.RecentItemBinding;
import com.vivek.filemanager.fragment.CleanerFragment;
import com.vivek.filemanager.fragment.ImageFragment;
import com.vivek.filemanager.fragment.SearchFragment;
import com.vivek.filemanager.interfaces.HomeAppClickListener;
import com.vivek.filemanager.interfaces.StorageClickListener;
import com.vivek.filemanager.databinding.CleanerItemBinding;
import com.vivek.filemanager.databinding.RvItemBinding;
import com.vivek.filemanager.databinding.SearchbarItemBinding;
import com.vivek.filemanager.databinding.StorageSpaceItemBinding;
import com.vivek.filemanager.utils.Utils;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.concurrent.Executors;

public class HomeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    HomeAppAdapter homeAppAdapter;
    String[] apps;
    Activity mainActivity;
    int width;
    int sdWidth;
    StorageClickListener storageClickListener;
    HomeAppClickListener homeAppClickListener;

    public HomeAdapter(Activity mainActivity, String[] apps, StorageClickListener storageClickListener, HomeAppClickListener homeAppClickListener) {
        this.apps = apps;
        this.mainActivity = mainActivity;
        this.homeAppClickListener = homeAppClickListener;
        homeAppAdapter = new HomeAppAdapter(mainActivity, apps, homeAppClickListener);
        this.storageClickListener = storageClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 0)
            return new SearchBarViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.searchbar_item, parent, false));
        else if (viewType == 1)
            return new MainMenuViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.rv_item, parent, false));
        else if (viewType == 2) {
            return new StorageSpaceViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.storage_space_item, parent, false));
        } else if (viewType == 3){
            return new CleanerViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.cleaner_item, parent, false));
        }else
            return new RecentItemViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),R.layout.recent_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder.getItemViewType() == 0) {
            SearchBarViewHolder holder1 = (SearchBarViewHolder) holder;
            holder1.onBind();
        } else if (holder.getItemViewType() == 1) {

            MainMenuViewHolder holder1 = (MainMenuViewHolder) holder;
//            holder1.onBind();

            holder1.binding.rv.setLayoutManager(new GridLayoutManager(holder1.itemView.getContext(), 4));
            holder1.binding.rv.setAdapter(homeAppAdapter);

        } else if (holder.getItemViewType() == 2) {

            StorageSpaceViewHolder viewHolder = (StorageSpaceViewHolder) holder;
            viewHolder.onBind();
        } else if (holder.getItemViewType() == 3){
                    CleanerViewHolder cleanerViewHolder = (CleanerViewHolder) holder;
                    cleanerViewHolder.onBind();
        }else {


            RecentItemViewHolder recentItemViewHolder  = (RecentItemViewHolder) holder;
            recentItemViewHolder.onBind();

        }
    }

    ArrayList<File> img(File file) {
        ArrayList<File> fileArrayList = new ArrayList<>();
        File[] files = file.listFiles();
        if (files != null) {
            for (File singleFile : files) {
                if (singleFile.isDirectory() && !singleFile.isHidden()) {
                    fileArrayList.addAll(img(singleFile));
                } else {
                    if (!singleFile.isHidden() && singleFile.getName().toLowerCase().endsWith(".jpg") || singleFile.getName().toLowerCase().endsWith(".jpeg") || singleFile.getName().toLowerCase().endsWith(".png")) {
                        fileArrayList.add(singleFile);
                    }
                }
            }
        }
        return fileArrayList;
    }

    ArrayList<File> shortFile(ArrayList<File> files) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            files.sort(Comparator.comparing(File::lastModified).reversed());
        }
        return files;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return 0;
        } else if (position == 1) {
            return 1;
        } else if (position == 2) {
            return 2;
        } else if (position == 3) {
            return 3;
        } else {
            return 4;
        }
    }

    @Override
    public int getItemCount() {
        return 5;
    }

    public void notifyAppData() {
        homeAppAdapter.notifyApps();
    }

    class SearchBarViewHolder extends RecyclerView.ViewHolder {

        SearchbarItemBinding binding;

        public SearchBarViewHolder(@NonNull SearchbarItemBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }

        public void onBind() {

            binding.ly.setOnClickListener(v -> {
                ((AppCompatActivity) mainActivity).getSupportFragmentManager().beginTransaction().replace(MainActivity.MAIN_CONTAINER, new SearchFragment()).addToBackStack(null).commit();
            });

        }
    }

    class MainMenuViewHolder extends RecyclerView.ViewHolder {

        RvItemBinding binding;

        public MainMenuViewHolder(@NonNull RvItemBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }

        /*public void onBind() {

//            String  internalStorage = System.getenv("EXTERNAL_STORAGE");

            ArrayList<File> files = new ArrayList<>();
            files.addAll(getFiles(Utils.EXTERNAL_FILE));
            files.addAll(getFiles(Utils.SD_CARD_FILE));

            long size = 0;
            ArrayList<File> shortFile = shortFile(files);
            if (shortFile.size() > 0) {
                for (File fil : shortFile) {
                    Log.d("FILEMANHBDJBHDJ", "onBind: " + fil);
                    size += fil.length();
                }
            }
        }*/
    }

    class StorageSpaceViewHolder extends RecyclerView.ViewHolder {

        StorageSpaceItemBinding binding;

        public StorageSpaceViewHolder(@NonNull StorageSpaceItemBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }

        public void onBind() {

            ViewTreeObserver treeObserver = binding.linearLayout.getViewTreeObserver();
            treeObserver.addOnGlobalLayoutListener(() -> {
                binding.linearLayout.getViewTreeObserver().removeOnGlobalLayoutListener(null);
                int measuredHeight = binding.linearLayout.getMeasuredHeight();
                int measuredWidth = binding.linearLayout.getMeasuredWidth();
                Log.d("MAINADAPER", "onBind: " + measuredHeight + "----------" + measuredWidth);

                width = measuredWidth;
            });


            long freeSpace = Utils.TOTAL_AVAILABLE_EXTERNAL_SIZE;//= Utils.TOTAL_EXTERNAL_SIZE - (Utils.EXT_IMAGE_SIZE + Utils.EXT_AUDIO_SIZE + Utils.EXT_VIDEO_SIZE + Utils.EXT_ZIPS_SIZE+Utils.EXT_APPS_SIZE+Utils.EXT_DOCUMENT_SIZE);
            long total = Utils.TOTAL_EXTERNAL_SIZE;

            long others = total - (Utils.EXT_IMAGE_SIZE + Utils.EXT_AUDIO_SIZE + Utils.EXT_VIDEO_SIZE + Utils.EXT_ZIPS_SIZE + Utils.EXT_APPS_SIZE + Utils.EXT_DOCUMENT_SIZE + Utils.TOTAL_AVAILABLE_EXTERNAL_SIZE);
            long free = freeSpace;                 // 80
            long img = Utils.EXT_IMAGE_SIZE;        // 10
            long audio = Utils.EXT_AUDIO_SIZE;      // 10
            long vid = Utils.EXT_VIDEO_SIZE;         // 10
            long zips = Utils.EXT_ZIPS_SIZE;          // 10
            long apps = Utils.EXT_APPS_SIZE;          // 10
            long doc = Utils.EXT_DOCUMENT_SIZE;       // 10

            double freePre = ((double) Utils.TOTAL_AVAILABLE_EXTERNAL_SIZE * 100 / (double) total);
            double imgPre = ((double) img * 100 / (double) total);
            double audPre = ((double) audio * 100 / (double) total);
            double vidPre = ((double) vid * 100 / (double) total);
            double zipPre = ((double) zips * 100 / (double) total);
            double appPre = ((double) apps * 100 / (double) total);
            double docPre = ((double) doc * 100 / (double) total);
            double otherPre = ((double) others * 100 / (double) total);

            Log.d("MAINADAPER", "onBind:\n FREE : " + freePre + "\n" +
                    "IMG : " + imgPre + "\n" +
                    "AUD : " + audPre + "\n" +
                    "VID : " + vidPre + "\n" +
                    "ZIP : " + zipPre + "\n" +
                    "APPS : " + appPre + "\n" +
                    "DOC : " + docPre);

            Log.d("MAINADAPER", "+++++++++: " + (width * freePre) / 100);

            binding.textView5.setText(String.format("%s / %s", Formatter.formatFileSize(mainActivity, Utils.TOTAL_AVAILABLE_EXTERNAL_SIZE), Formatter.formatFileSize(mainActivity, Utils.TOTAL_EXTERNAL_SIZE)));
            binding.tvPr.setText(String.format("%d%%", 100 - (int) (((float) Utils.TOTAL_AVAILABLE_EXTERNAL_SIZE / (float) Utils.TOTAL_EXTERNAL_SIZE) * 100)));

            ViewGroup.LayoutParams params = binding.prImage.getLayoutParams();
            params.width = (int) (width * imgPre) / 100;
            binding.prImage.setLayoutParams(params);

            ViewGroup.LayoutParams paramsAudio = binding.prAudio.getLayoutParams();
            paramsAudio.width = (int) (width * audPre) / 100;
            binding.prAudio.setLayoutParams(paramsAudio);

            ViewGroup.LayoutParams paramsVideo = binding.prVideo.getLayoutParams();
            paramsVideo.width = (int) (width * vidPre) / 100;
            binding.prVideo.setLayoutParams(paramsVideo);

            ViewGroup.LayoutParams paramsZips = binding.prZips.getLayoutParams();
            paramsZips.width = (int) (width * zipPre) / 100;
            binding.prZips.setLayoutParams(paramsZips);

            ViewGroup.LayoutParams paramsApps = binding.prApps.getLayoutParams();
            paramsApps.width = (int) (width * appPre) / 100;
            binding.prApps.setLayoutParams(paramsApps);

            ViewGroup.LayoutParams paramsDoc = binding.prDocument.getLayoutParams();
            paramsDoc.width = (int) (width * docPre) / 100;
            binding.prDocument.setLayoutParams(paramsDoc);

            ViewGroup.LayoutParams paramsOthers = binding.prOthers.getLayoutParams();
            paramsOthers.width = (int) (width * otherPre) / 100;
            binding.prOthers.setLayoutParams(paramsOthers);

            ViewGroup.LayoutParams paramsFree = binding.prBlank.getLayoutParams();
            paramsFree.width = (int) (width * freePre) / 100;
            binding.prBlank.setLayoutParams(paramsFree);

            binding.constraintLayout2.setOnClickListener(v -> {
                storageClickListener.onStorageClicked(0);
            });

            binding.imageView3.setOnClickListener(v -> storageClickListener.onStorageClicked(2));


            if (Utils.IS_SD_CARD_EXIST) {

                binding.sd.setVisibility(View.VISIBLE);

                ViewTreeObserver sdTreeObserver = binding.lySd.getViewTreeObserver();
                sdTreeObserver.addOnGlobalLayoutListener(() -> {
                    binding.lySd.getViewTreeObserver().removeOnGlobalLayoutListener(null);
                    int measWidth = binding.lySd.getMeasuredWidth();
                    sdWidth = measWidth;
                });


                long sdFreeSpace = Utils.TOTAL_AVAILABLE_SD_CARD_SIZE;//= Utils.TOTAL_EXTERNAL_SIZE - (Utils.EXT_IMAGE_SIZE + Utils.EXT_AUDIO_SIZE + Utils.EXT_VIDEO_SIZE + Utils.EXT_ZIPS_SIZE+Utils.EXT_APPS_SIZE+Utils.EXT_DOCUMENT_SIZE);
                long sdTotal = Utils.TOTAL_SD_CARD_SIZE;


                long sdOthers = sdTotal - (Utils.SD_IMAGE_SIZE + Utils.SD_AUDIO_SIZE + Utils.SD_VIDEO_SIZE + Utils.SD_ZIPS_SIZE + Utils.SD_APPS_SIZE + Utils.SD_DOCUMENT_SIZE + Utils.TOTAL_AVAILABLE_SD_CARD_SIZE);
                long sdFree = sdFreeSpace;                 // 80
                long sdImg = Utils.SD_IMAGE_SIZE;        // 10
                long sdAudio = Utils.SD_AUDIO_SIZE;      // 10
                long sdVid = Utils.SD_VIDEO_SIZE;         // 10
                long sdZips = Utils.SD_ZIPS_SIZE;          // 10
                long sdApps = Utils.SD_APPS_SIZE;          // 10
                long sdDoc = Utils.SD_DOCUMENT_SIZE;       // 10


                double sdFreePre = ((double) Utils.TOTAL_AVAILABLE_SD_CARD_SIZE * 100 / (double) sdTotal);
                double sdImgPre = ((double) sdImg * 100 / (double) sdTotal);
                double sdAudPre = ((double) sdAudio * 100 / (double) sdTotal);
                double sdVidPre = ((double) sdVid * 100 / (double) sdTotal);
                double sdZipPre = ((double) sdZips * 100 / (double) sdTotal);
                double sdAppPre = ((double) sdApps * 100 / (double) sdTotal);
                double sdDocPre = ((double) sdDoc * 100 / (double) sdTotal);
                double sdOtherPre = ((double) sdOthers * 100 / (double) sdTotal);

                binding.tvSd.setText(String.format("%s / %s", Formatter.formatFileSize(mainActivity, Utils.TOTAL_AVAILABLE_SD_CARD_SIZE), Formatter.formatFileSize(mainActivity, Utils.TOTAL_SD_CARD_SIZE)));
                binding.sdPr.setText(String.format("%d%%", 100 - (int) (((float) Utils.TOTAL_AVAILABLE_SD_CARD_SIZE / (float) Utils.TOTAL_SD_CARD_SIZE) * 100)));


                ViewGroup.LayoutParams sdImageParam = binding.prSdImage.getLayoutParams();
                sdImageParam.width = (int) (sdWidth * sdImgPre) / 100;
                binding.prSdImage.setLayoutParams(sdImageParam);

                ViewGroup.LayoutParams sdAudioParam = binding.prSdAudio.getLayoutParams();
                sdAudioParam.width = (int) (sdWidth * sdAudPre) / 100;
                binding.prSdAudio.setLayoutParams(sdAudioParam);

                ViewGroup.LayoutParams sdVideoParam = binding.prSdVideo.getLayoutParams();
                sdVideoParam.width = (int) (sdWidth * sdVidPre) / 100;
                binding.prSdVideo.setLayoutParams(sdVideoParam);

                ViewGroup.LayoutParams sdZipParam = binding.prSdZips.getLayoutParams();
                sdZipParam.width = (int) (sdWidth * sdZipPre) / 100;
                binding.prSdZips.setLayoutParams(sdZipParam);

                ViewGroup.LayoutParams sdAppsParam = binding.prSdApps.getLayoutParams();
                sdAppsParam.width = (int) (sdWidth * sdAppPre) / 100;
                binding.prSdApps.setLayoutParams(sdAppsParam);

                ViewGroup.LayoutParams sdDocParam = binding.prSdDocument.getLayoutParams();
                sdDocParam.width = (int) (sdWidth * sdDocPre) / 100;
                binding.prSdDocument.setLayoutParams(sdDocParam);

                ViewGroup.LayoutParams sdOtherParam = binding.prSdOthers.getLayoutParams();
                sdOtherParam.width = (int) (sdWidth * sdOtherPre) / 100;
                binding.prSdOthers.setLayoutParams(sdOtherParam);

                ViewGroup.LayoutParams sdFreeParam = binding.prSdBlank.getLayoutParams();
                sdFreeParam.width = (int) (sdWidth * sdOtherPre) / 100;
                binding.prSdBlank.setLayoutParams(sdFreeParam);

                binding.sd.setOnClickListener(v -> {
                    storageClickListener.onStorageClicked(1);
                });

                binding.ivSdNext.setOnClickListener(v -> storageClickListener.onStorageClicked(3));

            }


        }
    }

    class CleanerViewHolder extends RecyclerView.ViewHolder {

        CleanerItemBinding binding;

        public CleanerViewHolder(@NonNull CleanerItemBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }

        public void onBind() {
//            binding.constraintLayout3.setOnClickListener(v -> mainActivity.startActivity(new Intent(mainActivity, CleanerActivity.class)));
            binding.constraintLayout3.setOnClickListener(v -> ((AppCompatActivity)mainActivity).getSupportFragmentManager().beginTransaction().replace(MainActivity.MAIN_CONTAINER,new CleanerFragment()).addToBackStack(null).commit());
            ActivityManager actManager = (ActivityManager) mainActivity.getSystemService(Context.ACTIVITY_SERVICE);
            ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
            actManager.getMemoryInfo(memInfo);
            long availMem = memInfo.availMem;
            long totalMemory = memInfo.totalMem;
            binding.tvRam.setText(String.format("%s Used / %s",Formatter.formatFileSize(mainActivity,totalMemory),Formatter.formatFileSize(mainActivity,availMem)));

        }
    }

    class RecentItemViewHolder extends RecyclerView.ViewHolder{
        RecentItemBinding  binding;

        public RecentItemViewHolder(@NonNull @NotNull RecentItemBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }

        public void onBind() {
            RecentImageAdapter adapter = new RecentImageAdapter(mainActivity);
            binding.rv.setLayoutManager(new GridLayoutManager(mainActivity,4));
            binding.rv.setAdapter(adapter);
            Executors.newSingleThreadExecutor().execute(() -> {
                ArrayList<File> allFiles = new ArrayList<>();
                if (Utils.EXTERNAL_FILE != null){
                    allFiles.addAll(shortFile(img(Utils.EXTERNAL_FILE)));
                }
                if (Utils.SD_CARD_FILE != null){
                    allFiles.addAll(shortFile(img(Utils.SD_CARD_FILE)));
                }
                for (File file : allFiles){
                    Log.d("TAG", "SNJKLHNJKNDUIJNBKJND: "+file);
                }
                if (allFiles.size()>0){
                    adapter.addAll(allFiles);
                    mainActivity.runOnUiThread(() -> {
                        binding.tvNoImage.setVisibility(View.GONE);
                        binding.loader.setVisibility(View.GONE);
                        binding.textView27.setText(String.format("%d Image",allFiles.size()));
                    });
                }else {
                    mainActivity.runOnUiThread(() -> {
                        binding.rv.setVisibility(View.GONE);
                        binding.tvNoImage.setVisibility(View.VISIBLE);
                        binding.loader.setVisibility(View.GONE);
                        binding.textView27.setText("No Recent Image");
                    });
                }

            });

            binding.textView27.setOnClickListener(v -> {
                Bundle bundle = new Bundle();
                bundle.putString("FILE", "Images");
                ImageFragment imageFragment = new ImageFragment();
                imageFragment.setArguments(bundle);
                ((AppCompatActivity) mainActivity).getSupportFragmentManager().beginTransaction().replace(MainActivity.MAIN_CONTAINER,imageFragment).addToBackStack(null).commit();
            });
        }
    }

    /*private ArrayList<File> shortFile(ArrayList<File> files) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            files.sort(Comparator.comparing(File::lastModified).reversed());
        }
        return files;
    }*/


    private ArrayList<File> getFiles(File file) {
        ArrayList<File> arrayList = new ArrayList<>();
        File[] files = file.listFiles();

        if (files != null) {
            for (File singleFile : files) {
                if (singleFile.isDirectory() && !singleFile.isHidden()) {
                    arrayList.addAll(getFiles(singleFile));
                } else {
                    if (singleFile.getName().toLowerCase().endsWith(".jpeg") || singleFile.getName().toLowerCase().endsWith(".jpg") || singleFile.getName().toLowerCase().endsWith(".png")) {
                        arrayList.add(singleFile);
                    }
                }
            }

            for (File singleFile : files) {
                if (singleFile.getName().toLowerCase().endsWith(".jpeg")) {
                    arrayList.add(singleFile);
                }
            }
        }

        return arrayList;
    }
}
