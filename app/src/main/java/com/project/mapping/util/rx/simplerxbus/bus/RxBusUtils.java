package com.project.mapping.util.rx.simplerxbus.bus;


import androidx.fragment.app.FragmentActivity;

import com.project.mapping.util.rx.simplerxbus.lifecycle.HolderLifeFragment;
import com.trello.rxlifecycle2.components.support.RxFragment;

import io.reactivex.disposables.Disposable;

public class RxBusUtils {
    /**
     * @param tag
     * @param event
     * 发送普通事件
     */
    public static void post(String tag, Object event) {
        RxSimpleBus.getBus().sendMessage(new RxBusMessage(tag, event));
    }

    /**
     *
     * @param filter
     * @param receiver
     * @return
     */
    public static Disposable receive(String filter, RxBusReceiver<Object> receiver) {
        return RxSimpleBus.getBus().receiveMessageFrom(filter, receiver);
    }

    /**
     * 发送粘性事件
     * @param tag
     * @param event
     */
    public static void postSticky(String tag, Object event) {
        RxSimpleBus.getBus().sendStickyMessage(new RxBusMessage(tag, event));
    }

    /**
     *
     * @param filter
     * @param receiver
     * @return
     */
    public static Disposable receiveSticky(String filter, RxBusReceiver<Object> receiver) {
        receiver.setCurFilter(filter);
        return RxSimpleBus.getBus().receiveStickyMessage(filter, receiver);
    }

    /**
     * 接受普通事件
     * 自动取消注册
     * @param fragment
     * @param filter
     * @param receiver
     */
    public static void receive(RxFragment fragment, String filter, RxBusReceiver<Object> receiver) {
        Disposable disposable = receive(filter, receiver);
        HolderLifeFragment fragment1 = HolderLifeFragment.holderFragmentFor(fragment);
        setDispose(fragment1, disposable);
    }
    /**
     * 接受普通事件
     * 自动取消注册
     * @param activity
     * @param filter
     * @param receiver
     */
    public static void receive(FragmentActivity activity, String filter, RxBusReceiver<Object> receiver) {
        Disposable disposable = receive(filter, receiver);
        HolderLifeFragment fragment1 = HolderLifeFragment.holderFragmentFor(activity);
        setDispose(fragment1, disposable);
    }

    /**
     * 接受粘性事件
     * 自动取消注册
     * @param fragment
     * @param filter
     * @param receiver
     */
    public static void receiveSticky(RxFragment fragment, String filter, RxBusReceiver<Object> receiver) {
        Disposable disposable = receiveSticky(filter, receiver);
        HolderLifeFragment fragment1 = HolderLifeFragment.holderFragmentFor(fragment);
        setDispose(fragment1, disposable);
    }

    /**
     *
     * @param activity
     * @param filter
     * @param receiver
     */
    public static void receiveSticky(FragmentActivity activity, String filter, RxBusReceiver<Object> receiver) {
        Disposable disposable = receiveSticky(filter, receiver);
        HolderLifeFragment fragment1 = HolderLifeFragment.holderFragmentFor(activity);
        setDispose(fragment1, disposable);
    }

    private static void setDispose(HolderLifeFragment fragment, Disposable disposable) {
        if (fragment != null) {
            fragment.setDispose(disposable);
        }
    }
}
