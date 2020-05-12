package com.project.mapping.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.project.mapping.R;
import com.trello.rxlifecycle2.components.support.RxFragment;

public class BaseFragment extends RxFragment implements View.OnTouchListener {

    private Toolbar mTbHead;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.setOnTouchListener(this);
        mTbHead = (Toolbar) getActivity().findViewById(R.id.tb_head);
        mTbHead.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                back();
            }
        });
    }

    /**
     * setTitle
     *
     * @param title head title
     */
    public void setTitle(String title) {
        mTbHead.setTitle(title);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return true;
    }

    public void back(){
        int backStackEntryCount = getActivity().getSupportFragmentManager().getBackStackEntryCount();
        if (backStackEntryCount >= 1) {
            getActivity().getSupportFragmentManager().popBackStackImmediate();
        }
    }
}
