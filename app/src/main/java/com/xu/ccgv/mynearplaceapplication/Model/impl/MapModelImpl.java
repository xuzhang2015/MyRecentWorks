package com.xu.ccgv.mynearplaceapplication.Model.impl;

import android.location.Location;

import com.xu.ccgv.mynearplaceapplication.Bean.LocationEntity;
import com.xu.ccgv.mynearplaceapplication.MyApplication;
import com.xu.ccgv.mynearplaceapplication.R;
import com.xu.ccgv.mynearplaceapplication.Utils.SharedPreferencesUtil;
import com.xu.ccgv.mynearplaceapplication.Utils.UtilsMethod;
import com.xu.ccgv.mynearplaceapplication.contract.IMapContract;
import com.xu.ccgv.mynearplaceapplication.presenter.IPresenterCallBack;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xz on 28/04/2018.
 */

public class MapModelImpl implements IMapContract.IMapModel {

    private List<LocationEntity> mData = new ArrayList<>();


    @Override
    public void findLocation(IPresenterCallBack<Location> presenterCallBack) {
        float lat = SharedPreferencesUtil.getSharedPreferences(MyApplication.getInstance().getApplicationContext()).getCurrentLat();
        float lng = SharedPreferencesUtil.getSharedPreferences(MyApplication.getInstance().getApplicationContext()).getCurrentLng();
        float acc = SharedPreferencesUtil.getSharedPreferences(MyApplication.getInstance().getApplicationContext()).getAccuracy();
        //
        if (Math.abs(lat) != 100 && Math.abs(lng) != 200) {
            Location location = new Location("dummyprovider");
            location.setAccuracy(acc);
            location.setLatitude(lat);
            location.setLongitude(lng);
            presenterCallBack.onSuccess(location);
        } else {
            presenterCallBack.onFailure(UtilsMethod.getStringMethod(R.string.inner_error_try_again));
        }
    }

    @Override
    public void getAllLocations(IPresenterCallBack<List<LocationEntity>> presenterCallBack) {
        mData = new ArrayList<>();
        UtilsMethod.GetTheLocationList(mData);
        presenterCallBack.onSuccess(mData);
    }
}
