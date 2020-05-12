package com.project.mapping.bean;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.Serializable;

/**
 * Created by lin.woo on 2020/4/8.
 */
public class FileTypeBean implements Serializable {
    public String name;
    public String path;
    public boolean isFolder;//文件夹
    public String data;

    public FileTypeBean(String name, String path, boolean folder, String date) {
        this.name = name;
        this.path = path;
        this.isFolder = folder;
        this.data = date;
    }

    public String fileName() {
        return isFolder ? "" : name + ".mapping";
    }

    public String fullPath() {
        return path + File.separator + fileName();
    }
    @NonNull
    @Override
    public String toString() {
        return "name:" + name + ",path:" + path + ",isFolder:" + isFolder + ",data:" + data;
    }
}
