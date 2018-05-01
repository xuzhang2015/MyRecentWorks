package com.xu.ccgv.mynearplaceapplication.presenter;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

/**
 * detach/attach the view
 */

public abstract class BasePresenter<T> {
    private Reference<T> mViewRef;

    //Provide View and Presenter binding operations, making Presenter weak references to View to avoid memory leaks
    public void attachView(T view) {
        mViewRef = new WeakReference<T>(view);
    }

    protected T getView() {
        return mViewRef.get();
    }

    public boolean isViewAttached() {
        return mViewRef != null && mViewRef.get() != null;
    }

    public void detachView() {
        if (mViewRef != null) {
            mViewRef.clear();
            mViewRef = null;
        }
    }
}
