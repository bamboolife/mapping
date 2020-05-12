package com.project.mapping.wxapi;

import android.content.Intent;
import android.os.Bundle;

import com.jeremyliao.liveeventbus.LiveEventBus;
import com.project.mapping.constant.Constant;
import com.project.mapping.util.ToastUtil;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

public class WXPayEntryActivity extends RxAppCompatActivity implements IWXAPIEventHandler {
    private IWXAPI iwxapi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        iwxapi = WXAPIFactory.createWXAPI(this, Constant.WEHCHAT_APPID);
        iwxapi.handleIntent(getIntent(), this);
    }

    @Override
    public void onReq(BaseReq baseReq) {
    }

    //处理支付回调
    @Override
    public void onResp(BaseResp baseResp) {
        if (baseResp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            if (baseResp.errCode == 0) {
                ToastUtil.showToast("支付成功", this);
                LiveEventBus.get(Constant.PAY_SUCCESSFUL).post(baseResp.errCode);
            } else if (baseResp.errCode == -1) {
                ToastUtil.showToast("支付失败，请联系客服！", this);
//                LiveEventBus.get(Constant.PAY_FAIL).post(baseResp.errCode);
            } else {
                ToastUtil.showToast("支付取消！", this);
//                LiveEventBus.get(Constant.PAY_CANCEL).post(baseResp.errCode);
            }
            finish();
        }
    }

    public interface paySuccessfulCallBack {
        void onPaySuccessful();

        void onPayCancel();

        void onPayFail();
    }

    private paySuccessfulCallBack paySuccessfulCallBack;

    public void setOnPaySuccessful(paySuccessfulCallBack paySuccessfulCallBack) {
        this.paySuccessfulCallBack = paySuccessfulCallBack;
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        iwxapi.handleIntent(intent, this);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
    }
}