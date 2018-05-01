package com.xu.ccgv.mynearplaceapplication.presenter;

/**
 * callback to connect presenter and model
 */

public interface IPresenterCallBack<T> {
    void onSuccess(T data);

    void onFailure(String msg);
}
