package com.project.mapping.ui.fragment;

import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

import com.jeremyliao.liveeventbus.LiveEventBus;
import com.project.mapping.MainActivity;
import com.project.mapping.R;
import com.project.mapping.bean.PayBean;
import com.project.mapping.constant.Constant;
import com.project.mapping.util.DeviceUtil;
import com.project.mapping.util.RetrofitManager;
import com.project.mapping.util.SPUtil;
import com.project.mapping.util.rx.Transformers;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.trello.rxlifecycle2.android.FragmentEvent;

import java.util.HashMap;
import java.util.Map;

public class PayFragment extends BaseFragment {

    private IWXAPI iwxapi;
    private boolean isLogin;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pay, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setTitle("权益");
        TextView tvPayContent = (TextView) view.findViewById(R.id.tv_pay_content);
        String content = "<font color=\"#000000\">爱用思维导图的人，运气会更好^^\"</font><br><br>" +
                "<font color=\"#8666f1\">市面上的思维导图软件，我几乎都用过</font><br>" +
                "<font color=\"#000000\">有的操作简单，但看起来不好看</font><br>" +
                "<font color=\"#000000\">有的好看，但操作不简单</font><br>" +
                "<font color=\"#000000\">于是，我自己做了一个：</font><br>" +
                "<font color=\"#000000\">只求：</font><br>" +
                "<font color=\"#8666f1\">1、操作特别简单</font><br>" +
                "<font color=\"#8666f1\">2、看起来好看点</font><br>" +
                "<font color=\"#8666f1\">3、导出特别方便</font><br>" +
                "<font color=\"#8666f1\">4、别让我注册，不好用注册它干啥</font><br><br>" +
                "<font color=\"#000000\">就酱紫。这款软件就诞生了。</font><br><br>" +
                "<font color=\"#8666f1\">您可以不用注册。免费用，免费导出。</font><br>" +
                "<font color=\"#000000\">因为技术小哥哥维护和开发都花钱，所以只能免费4个。</font><br>" +
                "<font color=\"#8666f1\">如果觉得好用，平时用的多，可以付点小钱，想怎么用怎么用。</font><br>" +
                "<font color=\"#000000\">也许我们能把它做成最好用的思维导图软件~</font><br>" +
                "<font>(联系方式 wx: hdfenda)\"</font>";
        tvPayContent.setMovementMethod(ScrollingMovementMethod.getInstance());
        tvPayContent.setText(Html.fromHtml(content));
        initView(view);
        LiveEventBus.get(Constant.PAY_SUCCESSFUL).observe(getActivity(), new Observer<Object>() {
            @Override
            public void onChanged(Object o) {
                Log.d("==s==", "ok");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(500);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (isLogin) {
                                        back();
                                    } else {
                                        MainActivity activity = (MainActivity) getActivity();
                                        activity.replaceFragment(new LoginFragment());
                                    }
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                back();
            }
        });
    }


    private void startWx(String appid, String partnerid, String prepayid, String packageX, String noncestr, String timestamp, String sign) {
        iwxapi = WXAPIFactory.createWXAPI(getActivity(), Constant.WEHCHAT_APPID);
        PayReq request = new PayReq();
        request.appId = appid;
        request.partnerId = partnerid;
        request.prepayId = prepayid;
        request.packageValue = packageX;
        request.nonceStr = noncestr;
        request.timeStamp = timestamp;
        request.sign = sign;
        iwxapi.sendReq(request);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void initView(View view) {
        view.findViewById(R.id.btn_year).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isLogin = SPUtil.getBoolean(Constant.LOGIN, false);
                Map<String, String> map = new HashMap<>();
                map.put(Constant.ORDERTYPE, "1");
                map.put(Constant.EQUIPMENT_ID, DeviceUtil.getDeviceId(getActivity()));
                RetrofitManager.getInstance().getService().postUnifiedOrder(map).
                        compose(Transformers.applySchedulers(PayFragment.this, FragmentEvent.DESTROY))
                        .subscribe(payBean -> {
                            Log.d("===pay=1==", payBean.toString());
                            if (payBean.getStatus().equals(Constant.BIZ_SUCCESS)) {
                                PayBean.DataBean payBeanData = payBean.getData();
                                startWx(payBeanData.getAppid(),
                                        payBeanData.getPartnerid(),
                                        payBeanData.getPrepayid(),
                                        payBeanData.getPackageX(),
                                        payBeanData.getNoncestr(),
                                        payBeanData.getTimestamp(),
                                        payBeanData.getSign());
                            }
                        });
            }
        });

        view.findViewById(R.id.btn_forever).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isLogin = SPUtil.getBoolean(Constant.LOGIN, false);
                Map<String, String> map = new HashMap<>();
                map.put(Constant.ORDERTYPE, "2");
                map.put(Constant.EQUIPMENT_ID, DeviceUtil.getDeviceId(getActivity()));
                RetrofitManager.getInstance().getService().postUnifiedOrder(map).
                        compose(Transformers.applySchedulers(PayFragment.this, FragmentEvent.DESTROY)).
                        subscribe(payBean -> {
                            Log.d("===pay=2==", payBean.toString());
                            if (payBean.getStatus().equals(Constant.BIZ_SUCCESS)) {
                                PayBean.DataBean payBeanData = payBean.getData();
                                startWx(payBeanData.getAppid(),
                                        payBeanData.getPartnerid(),
                                        payBeanData.getPrepayid(),
                                        payBeanData.getPackageX(),
                                        payBeanData.getNoncestr(),
                                        payBeanData.getTimestamp(),
                                        payBeanData.getSign());
                            }
                        });
            }
        });
    }
}
