package com.project.mapping.constant;

public interface ApiConstant {
    String BASE_URL = "http://39.98.202.91:8089/mindmap/";
    //  上报设备id
    String REPORT ="user/report";
    //登录/注册
    String LOGIN_OR_REGISTER ="user/loginOrRegister";
    //找回密码
    String FORGOT_PASSWORD ="user/forgotPassword";
    //发送短信
    String SEND_MESSAGE ="user/sendMsg";
    //预下单
    String ORDER_UNIFIEDORDER ="order/unifiedOrder";
    //手机支付完成回调
    String ORDER_WXPAYBACK ="order/wxPayBack";
    /**
     * 获取权益级别
     */
    String USER_GETORDERTYPE ="user/getOrderType";

}
