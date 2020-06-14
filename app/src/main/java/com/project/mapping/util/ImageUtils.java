package com.project.mapping.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.media.MediaScannerConnection;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.project.mapping.MappingApplication;
import com.project.mapping.R;
import com.project.mapping.constant.Constant;
import com.project.mapping.tree.TreeView;
import com.project.mapping.tree.model.NodeModel;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by lin.woo on 2020/4/13.
 */
public class ImageUtils {
    //    private static Bitmap bitmap;
    private static File filePic;
//    private static Context mContext;

    public static void importImage(TreeView fl, Context context, boolean showToast, String filePath) {
        Bitmap bitmap = generateBitmap(fl);
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

    public static Bitmap generateBitmap(TreeView tree) {
        NodeModel<String> rootNode = tree.getTreeModel().getRootNode();

        final float cacheScale = tree.scale;
        final float scale = 2f / DensityUtils.dp2px(tree.getContext(), 1);
        tree.setScale(scale);
        int offset = DensityUtils.dp2px(MappingApplication.mContext, 60);
        int w = (int) (rootNode.boxW + DensityUtils.dp2px(MappingApplication.mContext, 60));
        int h = (int) (rootNode.boxH + DensityUtils.dp2px(MappingApplication.mContext, 60));
        Matrix matrix = new Matrix();


//        tree.setDrawingCacheEnabled(true);
//        //measure()实际测量 自己显示在屏幕上的宽高 2个参数，int widthMeasureSpec 和 int heightMeasureSpec表示具体的测量规则。
//        tree.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
//                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
//        //确定View的大小和位置的,然后将其绘制出来
//        tree.layout(150, 150, tree.getMeasuredWidth(), tree.getMeasuredHeight());
        //调用getDrawingCache方法就可 以获得view的cache图片


//        Bitmap bitmap = Bitmap.createBitmap(tree.getDrawingCache());
        Bitmap bitmap = Bitmap.createBitmap(
                w,
                h,
                Bitmap.Config.RGB_565);

        Point center = new Point(w / 2, w / 2);
        Point bmpCenter = new Point(bitmap.getWidth() / 2, bitmap.getHeight() / 2);
//        matrix.postTranslate(center.x - bmpCenter.x, center.y - bmpCenter.y); // 移动到当前view 的中心

        Canvas c = new Canvas(bitmap);

        c.drawColor(Color.WHITE);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(tree.getContext().getResources().getColor(R.color.color_8666f1));
        paint.setTextSize(25 * scale);
        c.drawText(
                Constant.IMAGE_WATERMARK,
                bmpCenter.x - DensityUtils.dp2px(MappingApplication.mContext, 60),
                bitmap.getHeight() - DensityUtils.dp2px(MappingApplication.mContext, 10),
                paint);

//        c.drawBitmap(bitmap, matrix, paint);
        tree.draw(c);
        c.save();
        tree.setScale(cacheScale);
        return bitmap;
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
