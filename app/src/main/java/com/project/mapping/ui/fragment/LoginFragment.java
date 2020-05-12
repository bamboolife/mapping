package com.project.mapping.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.project.mapping.R;
import com.project.mapping.bean.DataBean;
import com.project.mapping.constant.Constant;
import com.project.mapping.ui.activity.ForgetPassWordActivity;
import com.project.mapping.util.DeviceUtil;
import com.project.mapping.util.RegularUtil;
import com.project.mapping.util.RetrofitManager;
import com.project.mapping.util.SPUtil;
import com.project.mapping.util.ToastUtil;
import com.project.mapping.util.rx.Transformers;
import com.trello.rxlifecycle2.android.FragmentEvent;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.functions.Consumer;

public class LoginFragment extends BaseFragment implements View.OnClickListener {

    private EditText mEtPwd, mEtPhone;
    private Button mBtLogin;
    private TextView mTvPwd;
    private String mPhoneNumber, mPwd;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setTitle("登录/注册");
        initView(view);
    }

    private void initView(View view) {
        mEtPwd = (EditText) view.findViewById(R.id.et_pwd);
        mEtPhone = (EditText) view.findViewById(R.id.et_phone);
        mTvPwd = (TextView) view.findViewById(R.id.tv_pwd);
        mBtLogin = (Button) view.findViewById(R.id.bt_login);
        initListener();
    }

    private void initListener() {
        mTvPwd.setOnClickListener(this);
        mBtLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.tv_pwd:
                Intent intent = new Intent();
                intent.setClass(getActivity(), ForgetPassWordActivity.class);
                getActivity().startActivity(intent);
                break;
            case R.id.bt_login:
                mPhoneNumber = mEtPhone.getText().toString().trim();
                mPwd = mEtPwd.getText().toString().trim();
                loginOrRegister(mPhoneNumber, mPwd);
                break;
            default:
                break;
        }
    }

    private void loginOrRegister(final String phoneNumber, String pwd) {
        if (TextUtils.isEmpty(phoneNumber) || TextUtils.isEmpty(pwd)) {
            ToastUtil.showToast("请输入手机号码或密码", getActivity());
            return;
        } else if (!RegularUtil.isMobile(phoneNumber)) {
            ToastUtil.showToast("请输入有效的手机号码！", getActivity());
            return;
        } else if (!RegularUtil.isPWD(this.mPwd)) {
            ToastUtil.showToast("密码长度6~10位！", getActivity());
            return;
        } else {
            Map<String, String> map = new HashMap<>();
            map.put(Constant.CHANNEL_SOURCES, "10086");
            map.put(Constant.EQUIPMENT_ID, DeviceUtil.getDeviceId(getActivity()));
            map.put(Constant.EQUIPMENT_MODEL, DeviceUtil.getDeviceModel());
            map.put(Constant.USERPHONE, phoneNumber);
            map.put(Constant.PASSWORD, pwd);
            RetrofitManager.getInstance().getService().postLoginOrRegister(map).
                    compose(Transformers.<DataBean>applySchedulers(this, FragmentEvent.DESTROY))
                    .subscribe(new Consumer<DataBean>() {
                        @SuppressLint("CheckResult")
                        @Override
                        public void accept(DataBean dataBean) throws Exception {
                            Log.d("===postLoginOrRegister===", dataBean.toString());
                            if (dataBean.getStatus().equals(Constant.BIZ_SUCCESS)) {
                                ToastUtil.showToast("登录成功", getActivity());
                                SPUtil.put(Constant.NUMBER_LAST_4, phoneNumber.substring(7, 11));
                                SPUtil.put(Constant.PAY_TYPE, dataBean.getData());
                                SPUtil.put(Constant.LOGIN, true);
                                int backStackEntryCount = getActivity().getSupportFragmentManager().getBackStackEntryCount();
                                if (backStackEntryCount >= 1) {
                                    getActivity().getSupportFragmentManager().popBackStackImmediate();
                                }
                            } else {
                                ToastUtil.showToast(dataBean.getMessage(), getActivity());
                            }
                        }
                    });
        }
    }
}
