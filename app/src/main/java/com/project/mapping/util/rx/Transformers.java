package com.project.mapping.util.rx;

import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.android.FragmentEvent;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.trello.rxlifecycle2.components.support.RxFragment;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class Transformers {

    /**
     * Thread change : io ---> mainTread
     *
     * @param <T>
     * @return
     */
    public static <T> ObservableTransformer<T, T> applySchedulers() {
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(Observable<T> upstream) {
                return upstream.
                        subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

    /**
     * Thread change : io ---> mainTread
     * @param <T>
     * @param rxActivity
     * @param activityEvent
     * @return
     */
    public static <T> ObservableTransformer<T, T> applySchedulers(final RxAppCompatActivity rxActivity, final ActivityEvent activityEvent) {
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(Observable<T> upstream) {
                return upstream.compose(rxActivity.<T>bindUntilEvent(activityEvent)).
                        subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
            }
        };
    }
    /**
     * Thread change : io ---> mainTread
     * @param rxFragment
     * @param fragmentEvent
     * @param <T>
     * @return
     */
    public static <T> ObservableTransformer<T, T> applySchedulers(final RxFragment rxFragment, final FragmentEvent fragmentEvent) {
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(Observable<T> upstream) {
                return upstream.compose(rxFragment.<T>bindUntilEvent(fragmentEvent)).
                        subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
            }
        };
    }
}

