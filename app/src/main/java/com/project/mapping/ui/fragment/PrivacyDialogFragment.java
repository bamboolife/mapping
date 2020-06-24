package com.project.mapping.ui.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.text.style.BackgroundColorSpan;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.project.mapping.MainActivity;
import com.project.mapping.R;
import com.project.mapping.ui.activity.PrivacyActivity;
import com.project.mapping.util.SPUtil;
import com.project.mapping.util.ToastUtil;

/**
 * @Copyright (C), 2019-2020, 仙豆
 * @FileName: PrivacyDialogFragment
 * @Author: Yl
 * @Date: 2020/6/6 23:49
 * @Description: s
 */
public class PrivacyDialogFragment extends DialogFragment {

    private MainActivity mainActivity;

    public PrivacyDialogFragment(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().setCanceledOnTouchOutside(false);
        return inflater.inflate(R.layout.layout_user_privacy, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView clickPrivacy = view.findViewById(R.id.privacy_detail);
        SpannableString spannableString = new SpannableString("请仔细阅读完整版服务协议和隐私政策");
        ForegroundColorSpan foregroundColorSpan1 = new ForegroundColorSpan(getResources().getColor(R.color.color_8666f1));
        ForegroundColorSpan foregroundColorSpan2 = new ForegroundColorSpan(getResources().getColor(R.color.color_8666f1));
        spannableString.setSpan(foregroundColorSpan1, 8, 12, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        spannableString.setSpan(foregroundColorSpan2, 13, 17, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        spannableString.setSpan(new UnderlineSpan(), 8, 12, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        spannableString.setSpan(new UnderlineSpan(), 13, 16, Spanned.SPAN_INCLUSIVE_INCLUSIVE);

        Intent intent = new Intent(mainActivity, PrivacyActivity.class);
        spannableString.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                intent.putExtra("tab",0);
                startActivity(intent);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(getResources().getColor(R.color.color_8666f1));
            }
        }, 8, 12, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        spannableString.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                intent.putExtra("tab",1);
                startActivity(intent);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(getResources().getColor(R.color.color_8666f1));
            }
        }, 13, 17, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        clickPrivacy.setMovementMethod(LinkMovementMethod.getInstance());
        clickPrivacy.setText(spannableString);

        view.findViewById(R.id.privacy_disagree).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SPUtil.put("user_privacy", 2);
                dismiss();
                mainActivity.finish();
            }
        });
        view.findViewById(R.id.privacy_agree).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SPUtil.put("user_privacy", 1);
                dismiss();
                mainActivity.applyPermission();
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
