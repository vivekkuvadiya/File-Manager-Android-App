package com.vivek.filemanager.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.core.content.ContextCompat;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class Utils {

    public static File EXTERNAL_FILE;
    public static File SD_CARD_FILE;
    public static boolean IS_SD_CARD_EXIST;

    public static long TOTAL_EXTERNAL_SIZE = 0;
    public static long TOTAL_AVAILABLE_EXTERNAL_SIZE = 0;

    public static long TOTAL_SD_CARD_SIZE = 0;
    public static long TOTAL_AVAILABLE_SD_CARD_SIZE = 0;

    public static long EXT_IMAGE_SIZE = 0;
    public static long EXT_AUDIO_SIZE = 0;
    public static long EXT_VIDEO_SIZE = 0;
    public static long EXT_ZIPS_SIZE = 0;
    public static long EXT_APPS_SIZE = 0;
    public static long EXT_DOCUMENT_SIZE = 0;
    public static long EXT_DOWNLOAD_SIZE = 0;

    public static long SD_IMAGE_SIZE = 0;
    public static long SD_AUDIO_SIZE = 0;
    public static long SD_VIDEO_SIZE = 0;
    public static long SD_ZIPS_SIZE = 0;
    public static long SD_APPS_SIZE = 0;
    public static long SD_DOCUMENT_SIZE = 0;

    public static final String TABLE_FAV = "TABLE_FAV";

    public static final int EVENT_CLOSE = 0;
    public static final int EVENT_COPY = 1;
    public static final int EVENT_MOVE = 2;
    public static final int EVENT_ADD_TO_FAV = 3;
    public static final int EVENT_DELETE = 4;
    public static final int EVENT_DETAILS = 5;


    static public boolean hasStorage(boolean requireWriteAccess) {
        String state = Environment.getExternalStorageState();
//        Environment.isExternalStorageRemovable()
        Log.d("TAG", "hasStorage: " + Environment.isExternalStorageRemovable());
        if (Environment.MEDIA_MOUNTED.equals(state) && Environment.isExternalStorageRemovable()) {
           /* if (requireWriteAccess) {
                boolean writable = checkFsWritable();
                return checkFsWritable();
            } else {*/
            return true;
//            }
        } else if (!requireWriteAccess && Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    private static boolean checkFsWritable() {
        String directoryName = Environment.getExternalStorageDirectory().toString() + "/DCIM";
        File directory = new File(directoryName);
        if (!directory.isDirectory()) {
            if (!directory.mkdirs()) {
                return false;
            }
        }
        return directory.canWrite();
    }

    public static void setStatusBarColor(int color, Activity activity, boolean isDark) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS|WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            if (!isDark) window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE);
            else window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            window.setStatusBarColor(ContextCompat.getColor(activity, color));
        }
    }

    public static float convertPixelsToDp(float px, Context context) {
        return px / ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    public static float convertDpToPixel(float dp, Context context) {
        return dp * ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    public static ArrayList<File> shortFile(ArrayList<File> files) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            files.sort(Comparator.comparing(File::lastModified).reversed());
        }
        return files;
    }

    public static String getDurationBreakdown(long millis) {
        if (millis < 0) {
            return "0";
        }

        long days = TimeUnit.MILLISECONDS.toDays(millis);
        millis -= TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        millis -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);

        StringBuilder sb = new StringBuilder(64);
      /*sb.append(days);
        sb.append(" Days ");
        sb.append(hours);
        sb.append(" Hours ");
        sb.append(minutes);
        sb.append(" Minutes ");
        sb.append(seconds);
        sb.append(" Seconds");*/

        if (days != 0) {
            sb.append(days + ":");
        }
        if (hours != 0) {
            sb.append(hours + ":");
        }
        sb.append(minutes + ":");
        sb.append(seconds);

        return (sb.toString());
    }

    public static String[] getStorageDirectories(Context context) {
        String[] storageDirectories;
        String rawSecondaryStoragesStr = System.getenv("SECONDARY_STORAGE");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            List<String> results = new ArrayList<String>();
            File[] externalDirs = context.getExternalFilesDirs(null);
            for (File file : externalDirs) {
                String path = null;
                try {
                    path = file.getPath().split("/Android")[0];
                } catch (Exception e) {
                    e.printStackTrace();
                    path = null;
                }
                if (path != null) {
                    if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && Environment.isExternalStorageRemovable(file))
                            || rawSecondaryStoragesStr != null && rawSecondaryStoragesStr.contains(path)) {
                        results.add(path);
                    }
                }
            }
            storageDirectories = results.toArray(new String[0]);
        } else {
            final Set<String> rv = new HashSet<String>();

            if (!TextUtils.isEmpty(rawSecondaryStoragesStr)) {
                final String[] rawSecondaryStorages = rawSecondaryStoragesStr.split(File.pathSeparator);
                Collections.addAll(rv, rawSecondaryStorages);
            }
            storageDirectories = rv.toArray(new String[rv.size()]);
        }
        return storageDirectories;
    }

    public static void aud(Context context) {

        String retArray[] = getStorageDirectories(context);
        if (retArray.length == 0) {
            IS_SD_CARD_EXIST = false;
        } else {
            for (int i = 0; i < retArray.length; i++) {
                IS_SD_CARD_EXIST = true;
                SD_CARD_FILE = new File(new File(retArray[i]).getPath().split("/Android")[0]);
            }
        }
    }

    public static String[] getSize(long size) {
        long kilo = 1024;
        long mega = kilo * kilo;
        long giga = mega * kilo;
        long tera = giga * kilo;
        String s = "";
        String name = "";
        double kb = (double) size / kilo;
        double mb = kb / kilo;
        double gb = mb / kilo;
        double tb = gb / kilo;
        if (size < kilo) {
            s = size + "";
            name = "Bytes";
        } else if (size >= kilo && size < mega) {
            s = String.format("%.2f", kb);
            name = "KB";
        } else if (size >= mega && size < giga) {
            s = String.format("%.2f", mb);
            name = "MB";
        } else if (size >= giga && size < tera) {
            s = String.format("%.2f", gb);
            name = "GB";
        } else if (size >= tera) {
            s = String.format("%.2f", tb);
            name  = "TB";
        }
        return new String[]{s,name};
    }

}
