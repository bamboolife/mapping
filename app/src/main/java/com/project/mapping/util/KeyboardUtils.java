package com.project.mapping.util;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * Created by lin.woo on 2020/3/14.
 */
public class KeyboardUtils {
    /**
     * 显示键盘
     */
    public static void showInput(Activity act, EditText et) {
        et.requestFocus();
        InputMethodManager imm = (InputMethodManager) act.getSystemService(INPUT_METHOD_SERVICE);
        imm.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT);
    }

    /**
     * 隐藏键盘
     */
    public static void hideInput(Activity act) {
        InputMethodManager imm = (InputMethodManager) act.getSystemService(INPUT_METHOD_SERVICE);
        View v = act.getWindow().peekDecorView();
        if (null != v) {
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }
}
