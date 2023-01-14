package com.vivek.filemanager.fragment;

import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vivek.filemanager.MainActivity;
import com.vivek.filemanager.R;
import com.vivek.filemanager.interfaces.HomeAppClickListener;
import com.vivek.filemanager.interfaces.StorageClickListener;
import com.vivek.filemanager.adapter.HomeAdapter;
import com.vivek.filemanager.databinding.FragmentMainBinding;
import com.vivek.filemanager.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.Executors;

public class MainFragment extends Fragment implements StorageClickListener, HomeAppClickListener {

    private FragmentMainBinding binding;
    private HomeAdapter adapter;
    private String[] apps = {"Images", "Audio", "Videos", "Zips", "Apps", "Document", "Download", "More"};


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMainBinding.inflate(inflater);
        Utils.setStatusBarColor(R.color.main_status_bar, getActivity(), false);
        getTotalInternalMemorySize();
        getAvailableInternalMemorySize();
        Utils.EXTERNAL_FILE = Environment.getExternalStorageDirectory();

        Log.d("FILEMANHBDJBHDJ", "onCreate: " + Formatter.formatFileSize(getContext(), sdCardTotal_bytes()));

/*
        PackageManager packageManager = getContext().getPackageManager();
        Method[] methods = packageManager.getClass().getDeclaredMethods();
        for (Method m : methods) {
            if (m.getName().equals("freeStorage")) {
                // Found the method I want to use
                Log.d("FILEMANHBDJBHDJ", "onCreate: " + m.getName());
                try {
                    long desiredFreeStorage = 8 * 1024 * 1024 * 1024; // Request for 8GB of free space
                    m.invoke(packageManager, desiredFreeStorage , null);
                } catch (Exception e) {
                    // Method invocation failed. Could be a permission problem
                }
                break;
            }
        }*/


        Utils.aud(getActivity());

        /*if (Utils.hasStorage(true)) {

            File[] externalDir = new File[0];
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                externalDir = getContext().getExternalCacheDirs();
            }
            for (File file1 : externalDir) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    if (Environment.isExternalStorageRemovable(file1)) {
                        Utils.SD_CARD_FILE = new File(file1.getPath().split("/Android")[0]);
                        break;
                    }
                }
            }

            getTotalExternalMemorySize();
            getAvailableExternalMemorySize();


        }*/

        if (Utils.IS_SD_CARD_EXIST) {
            getTotalExternalMemorySize();
            getAvailableExternalMemorySize();
        }


        binding.rv.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new HomeAdapter(getActivity(), apps, this, this);

        binding.rv.setAdapter(adapter);


        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {

                Utils.EXT_IMAGE_SIZE = 0;
                Utils.EXT_AUDIO_SIZE = 0;
                Utils.EXT_VIDEO_SIZE = 0;
                Utils.EXT_ZIPS_SIZE = 0;
                Utils.EXT_APPS_SIZE = 0;
                Utils.EXT_DOCUMENT_SIZE = 0;
                Utils.EXT_DOWNLOAD_SIZE = 0;

                getFiles(Utils.EXTERNAL_FILE);
                countDownload(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS));

                Log.d("FILEMANHBDJBHDJ", "run: img : " + Formatter.formatFileSize(getContext(), Utils.EXT_IMAGE_SIZE) + "\n" +
                        "audio : " + Formatter.formatFileSize(getContext(), Utils.EXT_AUDIO_SIZE) + "\n" +
                        "Video : " + Formatter.formatFileSize(getContext(), Utils.EXT_VIDEO_SIZE) + "\n" +
                        "Zips : " + Formatter.formatFileSize(getContext(), Utils.EXT_ZIPS_SIZE) + "\n" +
                        "Apps : " + Formatter.formatFileSize(getContext(), Utils.EXT_APPS_SIZE) + "\n" +
                        "Document : " + Formatter.formatFileSize(getContext(), Utils.EXT_DOCUMENT_SIZE) + "\n" +
                        "Download : " + Formatter.formatFileSize(getContext(), Utils.EXT_DOWNLOAD_SIZE) + "\n" +
                        "TOTAL : " + Formatter.formatFileSize(getContext(), Utils.TOTAL_EXTERNAL_SIZE) + "\n" +
                        "AVAIABLE : " + Formatter.formatFileSize(getContext(), Utils.TOTAL_AVAILABLE_EXTERNAL_SIZE) + "\n" +
                        "ISVAVAVAA : " + Utils.hasStorage(true));

                if (Utils.IS_SD_CARD_EXIST) {
                    Utils.SD_IMAGE_SIZE = 0;
                    Utils.SD_AUDIO_SIZE = 0;
                    Utils.SD_VIDEO_SIZE = 0;
                    Utils.SD_ZIPS_SIZE = 0;
                    Utils.SD_APPS_SIZE = 0;
                    Utils.SD_DOCUMENT_SIZE = 0;

                    getSdCardFile(Utils.SD_CARD_FILE);

                    Log.d("FILEMANHBDJBHDJ", "run SD: img : " + Formatter.formatFileSize(getContext(), Utils.SD_IMAGE_SIZE) + "\n" +
                            "audio : " + Formatter.formatFileSize(getContext(), Utils.SD_AUDIO_SIZE) + "\n" +
                            "Video : " + Formatter.formatFileSize(getContext(), Utils.SD_VIDEO_SIZE) + "\n" +
                            "Zips : " + Formatter.formatFileSize(getContext(), Utils.SD_ZIPS_SIZE) + "\n" +
                            "Apps : " + Formatter.formatFileSize(getContext(), Utils.SD_APPS_SIZE) + "\n" +
                            "Document : " + Formatter.formatFileSize(getContext(), Utils.SD_DOCUMENT_SIZE) + "\n" +
                            "TOTAL : " + Formatter.formatFileSize(getContext(), Utils.TOTAL_SD_CARD_SIZE) + "\n" +
                            "AVAIABLE : " + Formatter.formatFileSize(getContext(), Utils.TOTAL_AVAILABLE_SD_CARD_SIZE));

                }

                adapter.notifyAppData();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyItemChanged(2);
                    }
                });


            }
        });


        return binding.getRoot();
    }


    public void getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = 0;
        long availableBlocks = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {

            blockSize = stat.getBlockSizeLong();
            availableBlocks = stat.getAvailableBlocksLong();
        }

        Utils.TOTAL_AVAILABLE_EXTERNAL_SIZE = availableBlocks * blockSize;

//        return formatSize(availableBlocks * blockSize);
    }

    public void getTotalInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = 0;
        long totalBlocks = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            blockSize = stat.getBlockSizeLong();
            totalBlocks = stat.getBlockCountLong();
        }
        Utils.TOTAL_EXTERNAL_SIZE = totalBlocks * blockSize;

    }

    public void getAvailableExternalMemorySize() {
        if (Utils.IS_SD_CARD_EXIST) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(Utils.SD_CARD_FILE.getPath());
            long blockSize = 0;
            long availableBlocks = 0;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                blockSize = stat.getBlockSizeLong();
                availableBlocks = stat.getAvailableBlocksLong();
            }
            Utils.TOTAL_AVAILABLE_SD_CARD_SIZE = blockSize * availableBlocks;
        }
    }

    public void getTotalExternalMemorySize() {
        if (Utils.IS_SD_CARD_EXIST) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(Utils.SD_CARD_FILE.getPath());
            long blockSize = 0;
            long totalBlocks = 0;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                blockSize = stat.getBlockSizeLong();
                totalBlocks = stat.getBlockCountLong();
            }

            Utils.TOTAL_SD_CARD_SIZE = blockSize * totalBlocks;
        }
    }


    private ArrayList<File> getFiles(File file) {
        ArrayList<File> arrayList = new ArrayList<>();
        File[] files = file.listFiles();

        if (files != null) {
            for (File singleFile : files) {
                if (singleFile.isDirectory() && !singleFile.isHidden()) {
                    arrayList.addAll(getFiles(singleFile));
                } else {
                    if (singleFile.getName().toLowerCase().endsWith(".jpeg") || singleFile.getName().toLowerCase().endsWith(".jpg") || singleFile.getName().toLowerCase().endsWith(".png")) {
                        Utils.EXT_IMAGE_SIZE += singleFile.length();
                    } else if (singleFile.getName().toLowerCase().endsWith(".mp4")) {
                        Utils.EXT_VIDEO_SIZE += singleFile.length();
                    } else if (singleFile.getName().toLowerCase().endsWith(".mp3") || singleFile.getName().toLowerCase().endsWith(".wav")) {
                        Utils.EXT_AUDIO_SIZE += singleFile.length();
                    } else if (singleFile.getName().toLowerCase().endsWith(".zip") || singleFile.getName().toLowerCase().endsWith(".zipx")) {
                        Utils.EXT_ZIPS_SIZE += singleFile.length();
                    } else if (singleFile.getName().toLowerCase().endsWith(".apk")) {
                        Utils.EXT_APPS_SIZE += singleFile.length();
                    } else if (singleFile.getName().toLowerCase().endsWith(".doc") || singleFile.getName().toLowerCase().endsWith(".docx") || singleFile.getName().toLowerCase().endsWith(".xls") || singleFile.getName().toLowerCase().endsWith(".xlsx") || singleFile.getName().toLowerCase().endsWith(".txt") || singleFile.getName().toLowerCase().endsWith(".ppt") || singleFile.getName().toLowerCase().endsWith(".pdf")) {
                        Utils.EXT_DOCUMENT_SIZE += singleFile.length();
                    }
                }
            }

           /* for (File singleFile : files) {
                if (singleFile.getName().toLowerCase().endsWith(".jpeg")) {
                    Utils.EXT_IMAGE_SIZE += singleFile.length();
                }
            }*/
        }

        return arrayList;
    }

    private ArrayList<File> countDownload(File file) {
        ArrayList<File> arrayList = new ArrayList<>();
        File[] files = file.listFiles();

        if (files != null) {
            for (File singleFile : files) {
                if (singleFile.isDirectory() && !singleFile.isHidden()) {
                    arrayList.addAll(countDownload(singleFile));
                } else {
                    Utils.EXT_DOWNLOAD_SIZE += singleFile.length();
/*
                    if (singleFile.getName().toLowerCase().endsWith(".jpeg") || singleFile.getName().toLowerCase().endsWith(".jpg") || singleFile.getName().toLowerCase().endsWith(".png")) {
                        Utils.EXT_IMAGE_SIZE += singleFile.length();
                    } else if (singleFile.getName().toLowerCase().endsWith(".mp4")) {
                        Utils.EXT_VIDEO_SIZE += singleFile.length();
                    } else if (singleFile.getName().toLowerCase().endsWith(".mp3") || singleFile.getName().toLowerCase().endsWith(".wav")) {
                        Utils.EXT_AUDIO_SIZE += singleFile.length();
                    } else if (singleFile.getName().toLowerCase().endsWith(".zip") || singleFile.getName().toLowerCase().endsWith(".zipx")) {
                        Utils.EXT_ZIPS_SIZE += singleFile.length();
                    } else if (singleFile.getName().toLowerCase().endsWith(".apk")) {
                        Utils.EXT_APPS_SIZE += singleFile.length();
                    } else if (singleFile.getName().toLowerCase().endsWith(".doc") || singleFile.getName().toLowerCase().endsWith(".docx") || singleFile.getName().toLowerCase().endsWith(".xls") || singleFile.getName().toLowerCase().endsWith(".xlsx") || singleFile.getName().toLowerCase().endsWith(".txt") || singleFile.getName().toLowerCase().endsWith(".ppt") || singleFile.getName().toLowerCase().endsWith(".pdf")) {
                        Utils.EXT_DOCUMENT_SIZE += singleFile.length();
                    }
*/
                }
            }

           /* for (File singleFile : files) {
                if (singleFile.getName().toLowerCase().endsWith(".jpeg")) {
                    Utils.EXT_IMAGE_SIZE += singleFile.length();
                }
            }*/
        }
        return arrayList;
    }


    private ArrayList<File> getSdCardFile(File file) {
        ArrayList<File> arrayList = new ArrayList<>();
        File[] files = file.listFiles();

        if (files != null) {
            for (File singleFile : files) {
                if (singleFile.isDirectory() && !singleFile.isHidden()) {
                    arrayList.addAll(getFiles(singleFile));
                } else {
                    if (singleFile.getName().toLowerCase().endsWith(".jpeg") || singleFile.getName().toLowerCase().endsWith(".jpg") || singleFile.getName().toLowerCase().endsWith(".png")) {
                        Utils.SD_IMAGE_SIZE += singleFile.length();
                    } else if (singleFile.getName().toLowerCase().endsWith(".mp4")) {
                        Utils.SD_VIDEO_SIZE += singleFile.length();
                    } else if (singleFile.getName().toLowerCase().endsWith(".mp3") || singleFile.getName().toLowerCase().endsWith(".wav")) {
                        Utils.SD_AUDIO_SIZE += singleFile.length();
                    } else if (singleFile.getName().toLowerCase().endsWith(".zip") || singleFile.getName().toLowerCase().endsWith(".zipx")) {
                        Utils.SD_ZIPS_SIZE += singleFile.length();
                    } else if (singleFile.getName().toLowerCase().endsWith(".apk")) {
                        Utils.SD_APPS_SIZE += singleFile.length();
                    } else if (singleFile.getName().toLowerCase().endsWith(".doc") || singleFile.getName().toLowerCase().endsWith(".docx") || singleFile.getName().toLowerCase().endsWith(".xls") || singleFile.getName().toLowerCase().endsWith(".xlsx") || singleFile.getName().toLowerCase().endsWith(".txt") || singleFile.getName().toLowerCase().endsWith(".ppt") || singleFile.getName().toLowerCase().endsWith(".pdf")) {
                        Utils.SD_DOCUMENT_SIZE += singleFile.length();
                    }
                }
            }

           /* for (File singleFile : files) {
                if (singleFile.getName().toLowerCase().endsWith(".jpeg")) {
                    Utils.EXT_IMAGE_SIZE += singleFile.length();
                }
            }*/
        }

        return arrayList;
    }

    public static long sdCardFree_bytes() {
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long free_memory = 0; //return value is in bytes

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            free_memory = stat.getAvailableBlocksLong() * stat.getBlockSizeLong();
        } else {
            free_memory = stat.getAvailableBlocks() * stat.getBlockSize();
        }

        return free_memory;
    }

    public static long sdCardTotal_bytes() {

        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long free_memory = 0;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            free_memory = stat.getBlockCountLong() * stat.getBlockSizeLong(); //return value is in bytes
        } else {
            free_memory = stat.getBlockCount() * stat.getBlockSize(); //return value is in bytes
        }

        return free_memory;
    }

    @Override
    public void onStorageClicked(int i) {
       /* Bundle bundle = new Bundle();
        bundle.putInt("FROM",i);
        StorageSpaceFragment storageSpaceFragment = new StorageSpaceFragment();
        storageSpaceFragment.setArguments(bundle);
        getActivity().getSupportFragmentManager().beginTransaction().replace(MainActivity.MAIN_CONTAINER,storageSpaceFragment).addToBackStack(null).commit();*/
        if (i == 0) {
            Bundle bundle = new Bundle();
            bundle.putString("path", Utils.EXTERNAL_FILE.getAbsolutePath());
            StorageFragment storageFragment = new StorageFragment();
            storageFragment.setArguments(bundle);
            getActivity().getSupportFragmentManager().beginTransaction().replace(MainActivity.MAIN_CONTAINER, storageFragment).addToBackStack(null).commit();
        } else if (i == 1) {
            Bundle bundle = new Bundle();
            bundle.putString("path", Utils.SD_CARD_FILE.getAbsolutePath());
            StorageFragment storageFragment = new StorageFragment();
            storageFragment.setArguments(bundle);
            getActivity().getSupportFragmentManager().beginTransaction().replace(MainActivity.MAIN_CONTAINER, storageFragment).addToBackStack(null).commit();
        } else if (i == 2) {
            Bundle bundle = new Bundle();
            bundle.putInt("FROM", i);
            StorageSpaceFragment storageSpaceFragment = new StorageSpaceFragment();
            storageSpaceFragment.setArguments(bundle);
            getActivity().getSupportFragmentManager().beginTransaction().replace(MainActivity.MAIN_CONTAINER, storageSpaceFragment).addToBackStack(null).commit();
        }else if (i == 3){
            Bundle bundle = new Bundle();
            bundle.putInt("FROM", i);
            StorageSpaceFragment storageSpaceFragment = new StorageSpaceFragment();
            storageSpaceFragment.setArguments(bundle);
            getActivity().getSupportFragmentManager().beginTransaction().replace(MainActivity.MAIN_CONTAINER, storageSpaceFragment).addToBackStack(null).commit();
        }
    }

    @Override
    public void onHomeAppClick(String name) {


        if (apps[0].equals(name) || apps[1].equals(name) || apps[2].equals(name)) {
            Bundle bundle = new Bundle();
            bundle.putString("FILE", name);
            ImageFragment imageFragment = new ImageFragment();
            imageFragment.setArguments(bundle);
            getActivity().getSupportFragmentManager().beginTransaction().replace(MainActivity.MAIN_CONTAINER, imageFragment, "AllImageFragment").addToBackStack(null).commit();
        } else if (apps[3].equals(name)) {
            getActivity().getSupportFragmentManager().beginTransaction().replace(MainActivity.MAIN_CONTAINER, new ZipFragment()).addToBackStack(null).commit();
        } else if (apps[4].equals(name)) {

            /*Bundle bundle = new Bundle();
            bundle.putString("APPS",name);
            AppsFragment appsFragment = new AppsFragment();
            appsFragment.setArguments(bundle);*/
            getActivity().getSupportFragmentManager().beginTransaction().replace(MainActivity.MAIN_CONTAINER, new AppsFragment()).addToBackStack(null).commit();
        } else if (apps[5].equals(name)) {
            getActivity().getSupportFragmentManager().beginTransaction().replace(MainActivity.MAIN_CONTAINER, new DocumentFragment()).addToBackStack(null).commit();
        } else if (apps[6].equals(name)) {
            getActivity().getSupportFragmentManager().beginTransaction().replace(MainActivity.MAIN_CONTAINER, new DownloadFragment()).addToBackStack(null).commit();
        } else if (apps[7].equals(name)) {
            getActivity().getSupportFragmentManager().beginTransaction().replace(MainActivity.MAIN_CONTAINER, new MoreFragment()).addToBackStack(null).commit();
        }
    }
}