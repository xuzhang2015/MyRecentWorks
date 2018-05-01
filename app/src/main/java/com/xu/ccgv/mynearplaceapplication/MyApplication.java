package com.xu.ccgv.mynearplaceapplication;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;

public class MyApplication extends Application {
    private static MyApplication instance;

    public static MyApplication getInstance() {
        return instance;
    }

    private void setInstance(MyApplication instance) {
        MyApplication.instance = instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setInstance(this);

//        initLeakCanary();
    }


    //test any memory leak appear
    private void initLeakCanary() {
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        LeakCanary.install(this);
    }

}
