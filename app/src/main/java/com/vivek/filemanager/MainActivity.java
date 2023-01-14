    package com.vivek.filemanager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.view.WindowManager;

import com.vivek.filemanager.adapter.HomeAdapter;
import com.vivek.filemanager.databinding.ActivityMainBinding;
import com.vivek.filemanager.db.DbHelper;
import com.vivek.filemanager.fragment.MainFragment;
import com.vivek.filemanager.utils.OpenFile;
import com.vivek.filemanager.utils.Utils;

import java.io.File;
import java.util.ArrayList;

    public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private HomeAdapter adapter;
    private String[] apps = {"Images", "Audio", "Videos", "Zips", "Apps", "Document", "Download", "More"};
    public static int MAIN_CONTAINER;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        MAIN_CONTAINER = binding.mainContainer.getId();
        getSupportFragmentManager().beginTransaction().replace(binding.mainContainer.getId(), new MainFragment()).commit();


        DbHelper.getInstance(this);
        OpenFile.init(this);

       /* getTotalInternalMemorySize();
        getAvailableInternalMemorySize();
        Utils.EXTERNAL_FILE = Environment.getExternalStorageDirectory();

        Log.d("FILEMANHBDJBHDJ", "onCreate: "+Formatter.formatFileSize(this,sdCardTotal_bytes()));

        if (Utils.hasStorage(true)) {

            File[] externalDir = new File[0];
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                externalDir = this.getExternalCacheDirs();
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


        }


        binding.rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new HomeAdapter(this, apps);

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

                Log.d("FILEMANHBDJBHDJ", "run: img : " + Formatter.formatFileSize(MainActivity.this, Utils.EXT_IMAGE_SIZE) + "\n" +
                        "audio : " + Formatter.formatFileSize(MainActivity.this, Utils.EXT_AUDIO_SIZE) + "\n" +
                        "Video : " + Formatter.formatFileSize(MainActivity.this, Utils.EXT_VIDEO_SIZE) + "\n" +
                        "Zips : " + Formatter.formatFileSize(MainActivity.this, Utils.EXT_ZIPS_SIZE) + "\n" +
                        "Apps : " + Formatter.formatFileSize(MainActivity.this, Utils.EXT_APPS_SIZE) + "\n" +
                        "Document : " + Formatter.formatFileSize(MainActivity.this, Utils.EXT_DOCUMENT_SIZE) + "\n" +
                        "Download : " + Formatter.formatFileSize(MainActivity.this, Utils.EXT_DOWNLOAD_SIZE) + "\n" +
                        "TOTAL : " + Formatter.formatFileSize(MainActivity.this, Utils.TOTAL_EXTERNAL_SIZE) + "\n" +
                        "AVAIABLE : " + Formatter.formatFileSize(MainActivity.this, Utils.TOTAL_AVAILABLE_EXTERNAL_SIZE) + "\n" +
                        "ISVAVAVAA : " + Utils.hasStorage(true));

                if (Utils.hasStorage(true)) {

                    Utils.SD_IMAGE_SIZE = 0;
                    Utils.SD_AUDIO_SIZE = 0;
                    Utils.SD_VIDEO_SIZE = 0;
                    Utils.SD_ZIPS_SIZE = 0;
                    Utils.SD_APPS_SIZE = 0;
                    Utils.SD_DOCUMENT_SIZE = 0;

                    getSdCardFile(Utils.SD_CARD_FILE);

                    Log.d("FILEMANHBDJBHDJ", "run SD: img : " + Formatter.formatFileSize(MainActivity.this, Utils.SD_IMAGE_SIZE) + "\n" +
                            "audio : " + Formatter.formatFileSize(MainActivity.this, Utils.SD_AUDIO_SIZE) + "\n" +
                            "Video : " + Formatter.formatFileSize(MainActivity.this, Utils.SD_VIDEO_SIZE) + "\n" +
                            "Zips : " + Formatter.formatFileSize(MainActivity.this, Utils.SD_ZIPS_SIZE) + "\n" +
                            "Apps : " + Formatter.formatFileSize(MainActivity.this, Utils.SD_APPS_SIZE) + "\n" +
                            "Document : " + Formatter.formatFileSize(MainActivity.this, Utils.SD_DOCUMENT_SIZE) + "\n" +
                            "TOTAL : " + Formatter.formatFileSize(MainActivity.this, Utils.TOTAL_SD_CARD_SIZE) + "\n" +
                            "AVAIABLE : " + Formatter.formatFileSize(MainActivity.this, Utils.TOTAL_AVAILABLE_SD_CARD_SIZE));

                }

                adapter.notifyAppData();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyItemChanged(2);
                    }
                });


            }
        });*/

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
        if (Utils.hasStorage(true)) {
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
        if (Utils.hasStorage(true)) {
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


}