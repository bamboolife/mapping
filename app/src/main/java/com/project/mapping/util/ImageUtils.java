package com.project.mapping.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaScannerConnection;
import android.text.TextUtils;
import android.util.Log;

import com.project.mapping.constant.Constant;
import com.project.mapping.tree.TreeView;
import com.project.mapping.tree.model.NodeModel;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by lin.woo on 2020/4/13.
 */
public class ImageUtils {
    private static Bitmap bitmap;
    private static File filePic;
    private static Context mContext;

    public static void importImage(TreeView fl, Context context, boolean showToast, String filePath) {
        mContext = context;
        generatBitmap(fl);
        String filename[] = filePath.split("/");
        String path = saveBitmap(bitmap, filename[filename.length - 1]);

        if (showToast) {
            if (!TextUtils.isEmpty(path)) {
                MediaScannerConnection.scanFile(context, new String[]{path}, null, null);
                ToastUtil.showToast("导图已保存", context);
            } else {
                ToastUtil.showToast("保存失败", context);
            }
        } else {
            if (!TextUtils.isEmpty(path)) {
                MediaScannerConnection.scanFile(context, new String[]{path}, null, null);
            }
        }
    }

    private static void generatBitmap(TreeView tree) {
        NodeModel<String> rootNode = tree.getTreeModel().getRootNode();

        final float cacheScale = tree.scale;
        final float scale = 2f / DensityUtils.dp2px(tree.getContext(), 1);
        tree.setScale(scale);
        bitmap = Bitmap.createBitmap(
                (int) (rootNode.boxW + DensityUtils.dp2px(mContext, 60)),
                (int) ((rootNode.boxH + DensityUtils.dp2px(mContext, 60))),
                Bitmap.Config.RGB_565);

        Canvas c = new Canvas(bitmap);
        c.drawColor(Color.WHITE);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLUE);
        paint.setTextSize(25 * scale);
        c.drawText(
                Constant.IMAGE_WATERMARK,
                bitmap.getWidth() - DensityUtils.dp2px(mContext, 80 * scale),
                bitmap.getHeight() - DensityUtils.dp2px(mContext, 20 * scale),
                paint);
        tree.draw(c);
        c.save();
        tree.setScale(cacheScale);
    }

    public static final String SD_PATH = "/sdcard/fqdtpic/";

    /**
     * 保存bitmap到本地
     *
     * @param bm
     * @return
     */
    public static String saveBitmap(Bitmap bm, String name) {
        String savePath;
        savePath = SD_PATH;
        try {
            filePic = new File(savePath + name + System.currentTimeMillis() + ".jpg");
            if (!filePic.exists()) {
                filePic.getParentFile().mkdirs();
                filePic.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(filePic);
            bm.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            Log.e("saveBitmap", "" + e);
            return null;
        }
        return filePic.getAbsolutePath();
    }

    public static String getImagePath() {
        if (filePic != null) {
            String absolutePath = filePic.getAbsolutePath();
            return absolutePath;
        } else {
            return null;
        }
    }
}