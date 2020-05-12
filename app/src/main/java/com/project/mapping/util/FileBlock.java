package com.project.mapping.util;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.project.mapping.bean.FileListBean;
import com.project.mapping.bean.FileTypeBean;
import com.project.mapping.constant.Constant;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by lin.woo on 2020/4/8.
 */
public class FileBlock {
    public static final String SP_FILE_LIST = "sp_file_list";
    private static String name;

    public static void saveFile(String path, int folderType) {
        String name = "番茄思维导图";
        ArrayList<FileListBean> list = readFile();
        if (!list.isEmpty()) {
            name += list.size();
        }
        if (folderType == 0) {
            list.add(new FileListBean(name, path, "文件"));
        } else {
            list.add(new FileListBean(name, path, "文件夹"));
        }
        SPUtil.put(SP_FILE_LIST, new Gson().toJson(list));
    }

    public static List<FileTypeBean> getAllFiles(String navPath) {
        ArrayList<FileTypeBean> fileTypeList = new ArrayList<>();
        navPath = Constant.FILEPATH + (null == navPath ? "" : File.separator + navPath);
        File[] fs = new File(navPath).listFiles();
        if (fs == null) {
            return null;
        }
        for (File f : fs) {
            // ignore hidden file
            if (f.getName().startsWith(".")) continue;

            if ((f.getName() != null) && (f.getName().length() > 0) && !f.isDirectory()) {
                int dot = f.getName().lastIndexOf('.');
                if ((dot > -1) && (dot < (f.length()))) {
                    name = f.getName().substring(0, dot);
                }
            } else {
                name = f.getName();
            }
            fileTypeList.add(new FileTypeBean(name, f.isDirectory() ? f.getPath() : f.getParent(), f.isDirectory(),
                    new SimpleDateFormat("MM月dd日 HH:mm")
                            .format(new Date(f.lastModified()))));
        }
        return fileTypeList;
    }


    public static ArrayList<FileListBean> readFile() {
        ArrayList<FileListBean> list = new ArrayList<>();
        String str = SPUtil.getString(SP_FILE_LIST, null);
        if (!TextUtils.isEmpty(str)) {
            list = new Gson().fromJson(str, new TypeToken<ArrayList<FileListBean>>() {
            }.getType());
        }
        return list;
    }
}
