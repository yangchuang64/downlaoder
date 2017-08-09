/*
 * Copyright (C) loongmobile.com, Inc. All Rights Reserved.
 * website: www.loongmobile.com
 */

package com.download.util;

import android.content.Context;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;

public class DownloadFileUtils {
    private static final String TAG = DownloadFileUtils.class.getSimpleName();

    public static boolean createExternalStorageDir(String dirPath) {
        // just create directory
        return createFile(new File(dirPath), false);
    }

    /**
     * Delete a file or directory by path.
     *
     * @param path
     */
    public static void deleteFile(String path) {
        if (path == null || path.equals(""))
            return;

        File file = new File(path);
        deleteFile(file);
    }

    /**
     * Delete a file or directory.
     *
     * @param file
     */
    public static void deleteFile(File file) {
        if (file.exists()) {
            if (file.isFile()) {
                file.delete();
            } else if (file.isDirectory()) {
                File files[] = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    deleteFile(files[i]);
                }
                file.delete();
            }
        }
    }

    public static void notifyUserDownloadStatus(Context context, String url, int status, int taskMode) {
        if (context == null)
            return;

        String msg = null;
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

//    public static String getDirPath() {
//        File storage = Environment.getExternalStorageDirectory();
//        String path = null;
//        if (storage != null) {
//            path = storage.toString() + DownloadConstant.INSTALL_DIR;
//        } else {
//            path = "/sdcard" + DownloadConstant.INSTALL_DIR;
//        }
////        Log.i(TAG, "path:" + path);
//        return path;
//    }

    public static String getFileName(String url) {
//        String mFileName = url.getFile();
        url = URLDecoder.decode(url);
        String mFileName = URLDecoder.decode(url.substring(url.lastIndexOf('/') + 1));
        return URLEncoder.encode(mFileName);
    }

    /**
     * 复制文件
     *
     * @param toFile
     * @param fromFile
     */
    public static void copyFile(File toFile, File fromFile) {
        // 判断目标目录中文件是否存在
        if (toFile.exists()) {
            return;
        } else {
            // 创建文件
            createFile(toFile, true);
        }
        try {
            InputStream is = new FileInputStream(fromFile);// 创建文件输入流
            FileOutputStream fos = new FileOutputStream(toFile);// 文件输出流
            byte[] buffer = new byte[1024];
            int read;
            while ((read = is.read(buffer)) != -1) {
                fos.write(buffer, 0, read);
            }
            is.close();
            fos.close();
        } catch (FileNotFoundException e) {// 捕获文件不存在异常
            e.printStackTrace();
        } catch (IOException e) {// 捕获异常
            e.printStackTrace();
        }
    }

    /**
     * 创建文件
     *
     * @param file
     * @param isFile
     */
    public static boolean createFile(File file, boolean isFile) {
        if (file.exists())
            return true;

        if (!file.getParentFile().exists()) {
            createFile(file.getParentFile(), false);
        }
        if (isFile) {
            try {
                file.createNewFile();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // 创建目录
            if (file.mkdir()) {
                return true;
            }
        }
        return false;
    }
}
