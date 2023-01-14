package com.vivek.filemanager.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.vivek.filemanager.utils.Utils;

import java.io.File;
import java.util.ArrayList;

public class DbHelper extends SQLiteOpenHelper {

    private static DbHelper mInstance = null;
    private Context context;
    private static final String DB_NAME = "FAV.DB";
    private static final int DB_VERSION = 1;
    private static final String COL_PATH = "PATH";

    public static DbHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new DbHelper(context);
        }
        return mInstance;
    }

    public DbHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + Utils.TABLE_FAV + " (" + COL_PATH + " TEXT PRIMARY KEY )");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public static void addToFavourite(String path) {
        ContentValues cv = new ContentValues();
        cv.put(COL_PATH, path);
        DbHelper.mInstance.getWritableDatabase().insert(Utils.TABLE_FAV, null, cv);
    }

    public static ArrayList<File> getFavFile() {
        ArrayList<File> favFileList = new ArrayList<>();

        Cursor cursor = mInstance.getReadableDatabase().rawQuery("SELECT * FROM " + Utils.TABLE_FAV, null);

        if (cursor != null && cursor.moveToFirst()) {
            cursor.moveToFirst();
            do {
                File file = new File(cursor.getString(0));
                if (file.exists()) {
                    favFileList.add(0,file);
                }
            } while (cursor.moveToNext());
        }

        return favFileList;
    }

    public static void removeFavourite(String path) {
        mInstance.getWritableDatabase().delete(Utils.TABLE_FAV, COL_PATH + "=?", new String[]{path});
    }

}
