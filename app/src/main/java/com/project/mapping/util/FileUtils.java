package com.project.mapping.util;

import android.os.Environment;
import android.util.Log;

import com.google.gson.Gson;
import com.project.mapping.constant.Constant;
import com.project.mapping.tree.model.TreeModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipFile;

/**
 * Created by lin.woo on 2020/4/7.
 */
public class FileUtils {


    public interface SaveFile {
        void saveSuccess(String path);
    }

    public static void saveMap(TreeModel<String> data, String filePath, SaveFile saveFile) {
        File path = new File(Constant.FILEPATH);
        if (!path.exists()) path.mkdirs();

//        saveFile(new File(filePath), data);
        try {
            writeTreeObject(filePath, data);
            FileBlock.saveFile(filePath, 0);
            saveFile.saveSuccess(filePath);
        } catch (IOException e) {
            Log.e("saveMap Err:", e.toString());
        }
    }

    private static void writeTreeObject(String filePath, Object object) throws IOException {
        FileOutputStream fos = new FileOutputStream(filePath);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(object);
        oos.close();
    }

    public static void saveFile(File file, TreeModel<String> data) {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        FileInputStream fileInputStream = null;
        try {
            File saveData = new File(new Gson().toJson(data.getRootNode()));
            fileInputStream = new FileInputStream(saveData);

            byte[] myByte = new byte[0];
            myByte = new byte[fileInputStream.available()];

            //文件长度
            int length = myByte.length;

            //头文件
            while (fileInputStream.read(myByte) != -1) {
                fileOutputStream.write(myByte, 0, length);
            }
            fileOutputStream.flush();
            fileInputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (fileInputStream != null)
                fileInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    //--------------------------------------------
    public static Object readZipFileObject(String filePath, String fileName) throws ClassNotFoundException, InvalidClassException {
        InputStream inputStream = null;
        ZipFile zipFile = null;
        ObjectInputStream objectInputStream = null;
        Object targetObject = null;
        try {
            File file = new File(filePath + fileName);
//            zipFile = new ZipFile(file);
//            ZipEntry zipEntry = new ZipEntry(fileName);
//            inputStream = zipFile.getInputStream(zipEntry);
            inputStream = new FileInputStream(file);
            objectInputStream = new ObjectInputStream(inputStream);
            targetObject = objectInputStream.readObject();
//            zipFile.close();
            inputStream.close();
            objectInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (zipFile != null) {
                try {
                    zipFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (objectInputStream != null) {
                try {
                    objectInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return targetObject;
    }

    /**
     * 删除文件
     *
     * @param filePath 文件地址
     * @return
     */
    public static void deleteFiles(String filePath) {
//        List<File> file = getFile(new File(filePath));
        File file = new File(filePath);
        deleteDirWithFile(file);

    }

    public static void deleteDirWithFile(File dir) {
        if (dir == null || !dir.exists() || !dir.isDirectory()) {
            dir.delete();
        } else {
            for (File file : dir.listFiles()) {
                if (file.isFile()) {
                    file.delete(); // 删除所有文件
                } else if (file.isDirectory()) {
                    deleteDirWithFile(file); // 递规的方式删除文件夹}
                }
            }
            dir.delete();// 删除目录本身
        }

    }

    /**
     * 遍历文件夹下的文件
     *
     * @param file 地址
     */
    public static List<File> getFile(File file) {
        List<File> list = new ArrayList<>();
        File[] fileArray = file.listFiles();
        if (fileArray == null) {
            return null;
        } else {
            for (File f : fileArray) {
                if (f.isFile()) {
                    list.add(0, f);
                } else {
                    getFile(f);
                }
            }
        }
        return list;
    }

    /**
     * 重命名文件
     *
     * @param oldPath 原来的文件地址
     * @param newPath 新的文件地址
     */
    public static void renameFile(String oldPath, String newPath) {
        File oleFile = new File(oldPath);
        File newFile = new File(newPath);
        //执行重命名
        oleFile.renameTo(newFile);
    }


    public static void copy(String source, String target) {
        File file1 = new File(source);
        File file2 = new File(target);
        InputStream fis = null;
        OutputStream fos = null;
        try {
            fis = new FileInputStream(file1);
            fos = new FileOutputStream(file2);
            byte[] buf = new byte[4096];
            int i;
            while ((i = fis.read(buf)) != -1) {
                fos.write(buf, 0, i);
            }
            fis.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 复制单个文件
     *
     * @param oldPath String 原文件路径 如：c:/fqf.txt
     * @param newPath String 复制后路径 如：f:/fqf.txt
     * @return boolean
     */
    public static void copyFile(String oldPath, String newPath) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (!oldfile.exists()) { //文件不存在时
                InputStream inStream = new FileInputStream(oldPath); //读入原文件
                FileOutputStream fs = new FileOutputStream(newPath + File.separator);
                byte[] buffer = new byte[1444];
                int length;
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; //字节数 文件大小
                    System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
            }
        } catch (Exception e) {
            System.out.println("复制单个文件操作出错");
            e.printStackTrace();
        }
    }
}
