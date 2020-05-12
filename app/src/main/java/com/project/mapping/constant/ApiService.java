package com.project.mapping.constant;

import com.project.mapping.bean.DataBean;
import com.project.mapping.bean.PayBean;

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface ApiService {
    /**
     * send message
     * get
     */
    @GET(ApiConstant.SEND_MESSAGE)
    Observable<DataBean> getMessage(@Query("phone") String key);

    /**
     * report device
     * put
     */
    @PUT(ApiConstant.REPORT)
    @Headers("Content-Type: application/json;charset=UTF-8")
    Observable<DataBean> putReportDevices(@Body Map<String, String> map);

    /**
     * login or register
     * post
     */
    @POST(ApiConstant.LOGIN_OR_REGISTER)
    @Headers("Content-Type: application/json;charset=UTF-8")
    Observable<DataBean> postLoginOrRegister(@Body Map<String, String> map);

    /**
     * forgot password
     * post
     */
    @POST(ApiConstant.FORGOT_PASSWORD)
    @Headers("Content-Type: application/json;charset=UTF-8")
    Observable<DataBean> postForgetPwd(@Body Map<String, String> map);

    /**
     * 预下单
     * post
     */
    @POST(ApiConstant.ORDER_UNIFIEDORDER)
    @Headers("Content-Type: application/json;charset=UTF-8")
    Observable<PayBean> postUnifiedOrder(@Body Map<String, String> map);

    /**
     * 手机支付完成回调
     * post
     */
    @POST(ApiConstant.ORDER_WXPAYBACK)
    @Headers("Content-Type: application/json;charset=UTF-8")
    Observable<DataBean> postWxPayBack(@Body Map<String, String> map);

    /**
     * 获取权益级别
     * get
     */
    @GET(ApiConstant.USER_GETORDERTYPE)
    Observable<DataBean> getOrderType(@Query("equipmentId") String key);
}
