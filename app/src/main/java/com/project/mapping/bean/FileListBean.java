package com.project.mapping.bean;

import java.io.Serializable;

/**
 * Created by lin.woo on 2020/4/8.
 */
public class FileListBean implements Serializable {
    public String name;
    public String path;
    public String folder;//文件夹

    public FileListBean(String name, String path, String folder) {
        this.name = name;
        this.path = path;
        this.folder = folder;
    }
}
