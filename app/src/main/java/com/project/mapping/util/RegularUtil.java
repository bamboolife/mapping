package com.project.mapping.util;

import android.text.TextUtils;

/**
 * Created by Administrator
 * Time  2017/3/24 16:29
 */

public class RegularUtil {
    /**
     * 验证手机格式
     * ^(?![0-9]+$)(?![^0-9]+$)(?![a-zA-Z]+$)(?![^a-zA-Z]+$)(?!^[^a-zA-Z0-9]+$)^.{6,16}$
     */
    public static boolean isMobile(String mobiles) {
        /*
         * 移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
         * 联通：130、131、132、152、155、156、185、186 电信：133、153、180、189、（1349卫通）
         * 总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
         */
        String telRegex = "[1][3456789]\\d{9}";// "[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        if (TextUtils.isEmpty(mobiles)) {
            return false;
        } else {
            return mobiles.matches(telRegex);
        }

    }

    /**
     * ^(?![0-9]+$)(?![^0-9]+$)(?![a-zA-Z]+$)(?![^a-zA-Z]+$)(?!^[^a-zA-Z0-9]+$)^.{6,16}$
     *
     * @param pwd
     * @return 密码6到10位
     */
    public static boolean isPWD(String pwd) {

//        String telRegex = "^(?![0-9]+$)(?![^0-9]+$)(?![a-zA-Z]+$)(?![^a-zA-Z]+$)(?!^[^a-zA-Z0-9]+$)^.{6,10}$";
//        if (TextUtils.isEmpty(pwd)) {
//            return false;
//        } else {
//            return pwd.matches(telRegex);
//        }
        if (pwd.length() < 6 || pwd.length() > 10) {
            return false;
        } else {
            return true;
        }

    }
}
