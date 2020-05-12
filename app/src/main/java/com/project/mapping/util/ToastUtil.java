package com.project.mapping.util;

import android.content.Context;
import android.widget.Toast;

/**
 * 各种效果toast
 *
 * @param
 */
public class ToastUtil {
    private static Toast toast = null;

    //标准toast
    public static void showToast(String text, Context context) {
        if (toast == null) {
            toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        } else {
            toast.setText(text);//如果不为空，则直接改变当前toast的文本
        }
        toast.show();
    }

}
