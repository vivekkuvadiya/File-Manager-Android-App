package com.vivek.filemanager.utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import java.io.File;

public class OpenFile {

    public static OpenFile openFile = null;
    private Context context;

    public static OpenFile init(Context context){
        if (openFile == null){
            openFile = new OpenFile(context);
        }
        return openFile;
    }

    public OpenFile(Context context) {
        this.context = context;
    }

    public static void open(File file){

        try {


            Uri uri = FileProvider.getUriForFile(openFile.context, openFile.context.getPackageName() + ".provider", file);

            Intent intent = new Intent(Intent.ACTION_VIEW);
            if (uri.toString().contains(".doc") || uri.toString().contains(".docx")) {
                intent.setDataAndType(uri, "application/msword");
            } else if (uri.toString().contains(".jpg") || uri.toString().contains(".jpeg") || uri.toString().contains(".png")) {
                intent.setDataAndType(uri, "image/*");
            } else if (uri.toString().contains(".mp3") || uri.toString().contains(".wav")) {
                intent.setDataAndType(uri, "audio/*");
            } else if (uri.toString().contains(".mp4")) {
                intent.setDataAndType(uri, "video/*");
            } else if (uri.toString().contains(".apk")) {
                intent.setDataAndType(uri, "application/vnd.android.package-archive");
            } else if (uri.toString().contains(".pdf")) {
                intent.setDataAndType(uri, "application/pdf");
            } else if (uri.toString().contains(".xls")) {
                intent.setDataAndType(uri, "vnd.ms-excel");
            } else if (uri.toString().contains(".ppt") || uri.toString().contains(".pptx")) {
                intent.setDataAndType(uri, "vnd.ms-powerpoint");
            } else if (uri.toString().contains(".txt")) {
                intent.setDataAndType(uri, "text/plain");
            } else if (uri.toString().contains(".zip") || uri.toString().contains(".rar")) {
                intent.setDataAndType(uri, "application/x-wav");
            } else {
                intent.setDataAndType(uri, "*/*");
            }
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            openFile.context.startActivity(intent);
        }catch (ActivityNotFoundException e){
            Toast.makeText(openFile.context, "No App Found", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

    }
}
