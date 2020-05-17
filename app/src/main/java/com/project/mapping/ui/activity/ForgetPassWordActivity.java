package com.project.mapping.ui.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.project.mapping.R;
import com.project.mapping.bean.DataBean;
import com.project.mapping.constant.Constant;
import com.project.mapping.util.DeviceUtil;
import com.project.mapping.util.RegularUtil;
import com.project.mapping.util.RetrofitManager;
import com.project.mapping.util.SPUtil;
import com.project.mapping.util.ToastUtil;
import com.project.mapping.util.rx.Transformers;
import com.project.mapping.weight.CountDownButton;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import java.util.HashMap;
import java.util.Map;

public class ForgetPassWordActivity extends RxAppCompatActivity implements View.OnClickListener {

    private EditText mEtPhone, mEtCode, mEtPwd;
    private CountDownButton mBtCode;
    private Button mBtForget;
    private String mPhoneCode;
    private String mPhoneNumber;
    private String mPwd;
    private Toolbar mTbHead;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        initView();
    }

    private void initView() {
        mEtPhone = (EditText) findViewById(R.id.et_phone);
        mEtCode = (EditText) findViewById(R.id.et_code);
        mEtPwd = (EditText) findViewById(R.id.et_pwd);
        mBtCode = (CountDownButton) findViewById(R.id.bt_code);
        mBtForget = (Button) findViewById(R.id.bt_forget);
        mTbHead = (Toolbar) findViewById(R.id.tb_head);
        initHead();
        initListener();
    }

    private void initHead() {
        mTbHead.setTitle("找回密码");
    }

    private void initListener() {
        mBtForget.setOnClickListener(this);
        mBtCode.setOnClickListener(this);
        mTbHead.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public void onClick(View view) {
        mPhoneNumber = mEtPhone.getText().toString().trim();
        mPhoneCode = mEtCode.getText().toString().trim();
        mPwd = mEtPwd.getText().toString().trim();
        switch (view.getId()) {
            case R.id.bt_code:
                if (!RegularUtil.isMobile(mPhoneNumber)) {
                    ToastUtil.showToast("请输入有效的手机号码！", this);
                    return;
                }
                mBtCode.onStart();
                getMessage();

                break;
            case R.id.bt_forget:
                if (TextUtils.isEmpty(mPhoneCode) || TextUtils.isEmpty(mPhoneNumber) || TextUtils.isEmpty(mPwd)) {
                    ToastUtil.showToast("手机号,验证码,密码不能为空", this);
                    return;
                } else if (!RegularUtil.isMobile(mPhoneNumber)) {
                    ToastUtil.showToast("请输入有效的手机号码！", this);
                    return;
                } else if (!RegularUtil.isPWD(mPwd)) {
                    ToastUtil.showToast("密码长度6~10位！", this);
                } else {
                    Map<String, String> map = new HashMap<>();
                    map.put(Constant.CHANNEL_SOURCES, "10086");
                    map.put(Constant.EQUIPMENT_ID, DeviceUtil.getDeviceId(this));
                    map.put(Constant.EQUIPMENT_MODEL, DeviceUtil.getDeviceModel());
                    map.put(Constant.USERPHONE, mPhoneNumber);
                    map.put(Constant.VERIFICATION_CODE, mPhoneCode);
                    map.put(Constant.PASSWORD, mPwd);
                    RetrofitManager.getInstance().getService().postForgetPwd(map)
                            .compose(Transformers.<DataBean>applySchedulers(this, ActivityEvent.DESTROY))
                            .subscribe(dataBean -> {
                                Log.d("===postForgetPwd===", dataBean.toString());
                                if (dataBean.getStatus().equals(Constant.BIZ_SUCCESS)) {
                                    SPUtil.put(Constant.LOGIN, false);
                                    ToastUtil.showToast("密码修改成功，请重新登录", ForgetPassWordActivity.this);
                                    finish();
                                } else {
                                    ToastUtil.showToast(dataBean.getMessage(), ForgetPassWordActivity.this);
                                }
                            });
                }
                break;
            default:
                break;
        }
    }

    private void getMessage() {
        RetrofitManager.getInstance().getService().getMessage(mPhoneNumber).
                compose(Transformers.<DataBean>applySchedulers(this, ActivityEvent.DESTROY)).
                subscribe(dataBean -> {
                    Log.d("===getMessage===", dataBean.toString());
                    if (dataBean.getStatus().equals(Constant.BIZ_SUCCESS)) {
                        ToastUtil.showToast("验证码发送成功，请查看手机", ForgetPassWordActivity.this);
                    } else {
                        ToastUtil.showToast(dataBean.getMessage(), ForgetPassWordActivity.this);
                    }
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mBtCode != null) {
            mBtCode.onCancel();
            mBtCode = null;
        }
    }
}
