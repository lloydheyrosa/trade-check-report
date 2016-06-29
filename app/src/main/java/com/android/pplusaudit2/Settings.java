package com.android.pplusaudit2;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by ULTRABOOK on 10/9/2015.
 */
public class Settings {

    public static File captureFolder;
    public static File signatureFolder;
    public static File imgFolder;
    public static File postingFolder;
    public static File PjpFolder;
    private static final File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), General.QUESTION_IMAGE_CAPTURE);

    public static Uri uriSignatureTempPath;

    public static void LoadSettings() {
        if(!mediaStorageDir.exists())
            mediaStorageDir.mkdirs();
    }

    public static Uri GetUriQuestionImagePath(String filename) {

        LoadSettings();
        return Uri.fromFile(new File(mediaStorageDir.getPath() + File.separator + filename.trim().toUpperCase() + ".jpg"));
    }

    public static void InitTransactionFolders(Context mContext) {
        File dlpath =  new File(new File(mContext.getExternalFilesDir(null),""), "Downloads");
        Settings.captureFolder.delete();
        Settings.signatureFolder.delete();

        for (String files : General.DOWNLOAD_FILES) {
            File fDelete = new File(dlpath, files);
            if(!fDelete.delete()) Log.e("Deleting file", "Can't delete " + files);
        }
    }

    public static void CopyFile(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dst);

        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }
}
