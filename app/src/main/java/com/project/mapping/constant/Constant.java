package com.project.mapping.constant;

import android.os.Environment;

public interface Constant {

    String DATA_SUCCESS = "Successfully!";
    String BIZ_SUCCESS = "BIZ_SUCCESS";
    String LOGIN = "login";
    String NUMBER_LAST_4 = "number_last_4";
    String PAY_TYPE = "pay_type";
    /**
     * 渠道来源
     */
    String CHANNEL_SOURCES = "channelSources";
    /**
     * 设备id
     */
    String EQUIPMENT_ID = "equipmentId";
    /**
     * 设备型号
     */
    String EQUIPMENT_MODEL = "equipmentModel";
    /**
     * 手机号码
     */
    String USERPHONE = "userPhone";
    /**
     * 密码
     */
    String PASSWORD = "password";
    /**
     * 订单号
     */
    String ORDERTYPE = "orderType";
    /**
     * 验证码
     */
    String VERIFICATION_CODE = "verificationCode";
    String WEHCHAT_APPID = "wx3922c854984be505";
    String IMAGE_WATERMARK = "Powered by 番茄思维导图";
    /**
     * 导图存放根目录
     */
    String FILEPATH = Environment.getExternalStorageDirectory() + "/mapping/";
    String PAY_SUCCESSFUL = "pay_successful";
    String PAY_FAIL = "pay_fail";
    String PAY_CANCEL = "pay_cancel";
    String CHANNEL_ID = "10086";
}
