package com.project.mapping.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.project.mapping.MainActivity;
import com.project.mapping.R;
import com.project.mapping.weight.ZoomImageView;

/**
 * @Copyright (C), 2019-2020
 * @FileName: SnapPreviewFragment
 * @Author: runtime
 * @Date: 2020/6/4 4:13 PM
 * @Description:
 */
public class SnapPreviewFragment extends BaseFragment {
    private MainActivity mainActivity;
    private TextView tvBack;
    private TextView tvExport;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.sanp_preview_layout,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mainActivity= (MainActivity) getActivity();
        initView(view);
    }

    private void initView(View view) {
        tvBack=view.findViewById(R.id.snap_btn_back);
        tvExport=view.findViewById(R.id.snap_btn_export);
        ZoomImageView ivSnap=view.findViewById(R.id.snap_preview_image);
        Glide.with(mainActivity).load(mainActivity.getTreeViewBitmap()).into(ivSnap);
        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.setSaveToSD(View.GONE);
                back();
            }
        });
        tvExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.showSharePop();
                mainActivity.setSaveToSD(View.VISIBLE);
            }
        });
    }
}
