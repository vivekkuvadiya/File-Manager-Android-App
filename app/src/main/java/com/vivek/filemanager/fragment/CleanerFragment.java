package com.vivek.filemanager.fragment;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.vivek.filemanager.MainActivity;
import com.vivek.filemanager.databinding.FragmentCleanerBinding;
import com.vivek.filemanager.utils.Utils;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Executors;


public class CleanerFragment extends Fragment {

    private FragmentCleanerBinding binding;
    private long duplicateFileSize = 0;
    private long largeFileSize = 0;

    ArrayList<File> duplicateFile = null;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCleanerBinding.inflate(inflater);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getActivity().getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        Executors.newSingleThreadExecutor().execute(() -> {
            duplicateFileSize = 0;
            largeFileSize = 0;
            ArrayList<File> allFiles;
            getJunkFiles();
            allFiles = getAllFiles(new File[]{Utils.EXTERNAL_FILE, Utils.SD_CARD_FILE});
            duplicateFile = getDuplicateFile(allFiles);
            getLargeFile(allFiles);
            for (File file : duplicateFile) {
                Log.d("Cleaner Files", "onCreate: " + file);
            }
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    String[] duplicateFileSizeInfo = Utils.getSize(duplicateFileSize);
                    binding.tvDuplicateSize.setText(duplicateFileSizeInfo[0]);
                    binding.tvDuplicateSizeTxt.setText(duplicateFileSizeInfo[1]);
                    binding.textView29.setText(Formatter.formatFileSize(getContext(), (duplicateFileSize + largeFileSize)));
                    binding.btnCleanDuplicate.setEnabled(true);
                });
                getActivity().runOnUiThread(() -> {
                    String[] largeFileSizeInfo = Utils.getSize(largeFileSize);
                    binding.tvLargeFileSize.setText(largeFileSizeInfo[0]);
                    binding.tvLargeFileSizeTxt.setText(largeFileSizeInfo[1]);
                    binding.textView29.setText(Formatter.formatFileSize(getContext(), (duplicateFileSize + largeFileSize)));
                    binding.btnCleanLargeFile.setEnabled(true);
                });
            }
        });


        binding.ivBack.setOnClickListener(v -> getActivity().onBackPressed());

        binding.btnCleanDuplicate.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putLong("size", duplicateFileSize);
            bundle.putSerializable("list", (Serializable) duplicateFile);
            bundle.putString("file", "Duplicate Files");
            CleaningFragment cleaningFragment = new CleaningFragment();
            cleaningFragment.setArguments(bundle);
            getActivity().getSupportFragmentManager().beginTransaction().replace(MainActivity.MAIN_CONTAINER, cleaningFragment).addToBackStack(null).commit();
        });

        return binding.getRoot();
    }

    private ArrayList<File> getAllFiles(File[] file) {
        ArrayList<File> filesList = new ArrayList<>();
        for (File thisFile : file) {
            if (thisFile != null) {
                File[] files = thisFile.listFiles();
                if (files != null) {
                    for (File f : files) {
                        if (f.isDirectory() && !f.isHidden()) {
                            filesList.addAll(getAllFiles(new File[]{f}));
                        } else {
                            if (!f.isHidden())
                                filesList.add(f);
                        }
                    }
                }
            }
        }
        return filesList;
    }

    private ArrayList<File> getDuplicateFile(ArrayList<File> fileArrayList) {
        HashSet<String> hashSet = new HashSet<>();
        ArrayList<File> duplicateList = new ArrayList<>();
        for (File file : fileArrayList) {
            if (!hashSet.add(file.getName())) {
                duplicateList.add(file);
                duplicateFileSize += file.length();
            }
        }
        return duplicateList;
    }

    private ArrayList<File> getLargeFile(ArrayList<File> filesList) {
        ArrayList<File> largeFiles = new ArrayList<>();
        for (File file : filesList) {
            if (file.length() > 50000000) {
                largeFiles.add(file);
                largeFileSize += file.length();
            }
        }
        return largeFiles;
    }

    @SuppressLint("WrongConstant")
    private void getJunkFiles() {
        ActivityManager mActivityManager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        PackageManager pm = getContext().getPackageManager();
        List<ApplicationInfo> installedApplications = pm.getInstalledApplications(PackageManager.GET_GIDS);

        int size = installedApplications.size();

        for (int i = 0; i < size; i++) {
            ApplicationInfo applicationInfo = installedApplications.get(i);
            try {
                Context packageContext = getContext().getApplicationContext().createPackageContext(applicationInfo.packageName, Context.CONTEXT_IGNORE_SECURITY);
                count(packageContext);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void count(Context context) {
        File cache = context.getCacheDir();
        if (cache != null) {
            File appDir = new File(cache.getParent());
            if (appDir.exists()) {
                File[] children = appDir.listFiles();
                if (children != null)
                    for (File s : children) {
                        if (!s.equals("lib")) {
                            Log.d("TAG", "getJunkFiles: " + s);
                        }
                    }

            }
        }
    }


}