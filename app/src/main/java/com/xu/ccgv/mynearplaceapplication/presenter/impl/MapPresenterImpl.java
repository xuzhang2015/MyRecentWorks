package com.xu.ccgv.mynearplaceapplication.presenter.impl;


import android.location.Location;

import com.xu.ccgv.mynearplaceapplication.Bean.LocationEntity;
import com.xu.ccgv.mynearplaceapplication.Model.impl.MapModelImpl;
import com.xu.ccgv.mynearplaceapplication.MyApplication;
import com.xu.ccgv.mynearplaceapplication.Utils.SharedPreferencesUtil;
import com.xu.ccgv.mynearplaceapplication.Utils.UtilsMethod;
import com.xu.ccgv.mynearplaceapplication.configs.ConfigValues;
import com.xu.ccgv.mynearplaceapplication.contract.IMapContract;
import com.xu.ccgv.mynearplaceapplication.presenter.BasePresenter;
import com.xu.ccgv.mynearplaceapplication.presenter.IPresenterCallBack;

import java.util.List;

/**
 * Created by xz on 28/04/2018.
 */

public class MapPresenterImpl extends BasePresenter<IMapContract.IMapView> implements IMapContract.IMapPresenter {

    private IMapContract.IMapView mView;
    private IMapContract.IMapModel mModel;

    //for test only
    public void setTheMockModel(IMapContract.IMapModel mModel) {
        this.mModel = mModel;
    }

    @Override
    public void initPresenter() {
        //using getView method in BasePresenter
        mView = getView();
        //link presenter with model to obtain the data
        mModel = new MapModelImpl();
    }

    //find the user's current location
    @Override
    public void findLocation() {
        mModel.findLocation(new IPresenterCallBack<Location>() {

            @Override
            public void onSuccess(Location data) {
                mView.showThePoint(data);
            }

            @Override
            public void onFailure(String msg) {
                UtilsMethod.showToast(msg);
            }
        });
    }

    //get the search result
    @Override
    public void getAllLocations() {
        mModel.getAllLocations(new IPresenterCallBack<List<LocationEntity>>() {
            @Override
            public void onSuccess(List<LocationEntity> data) {
                String query = SharedPreferencesUtil.getSharedPreferences(MyApplication.getInstance().getApplicationContext()).getString(ConfigValues.QUERY, "");
                mView.updateMarker(data);
                if (!query.equals("")) mView.updateSearchView(query);
            }

            @Override
            public void onFailure(String msg) {
                UtilsMethod.showToast(msg);
            }
        });
    }
}
