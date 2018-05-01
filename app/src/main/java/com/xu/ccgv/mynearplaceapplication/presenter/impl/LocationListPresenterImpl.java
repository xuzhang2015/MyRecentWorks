package com.xu.ccgv.mynearplaceapplication.presenter.impl;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.xu.ccgv.mynearplaceapplication.Bean.LocationEntity;
import com.xu.ccgv.mynearplaceapplication.Model.impl.LocationListModelImpl;
import com.xu.ccgv.mynearplaceapplication.MyApplication;
import com.xu.ccgv.mynearplaceapplication.R;
import com.xu.ccgv.mynearplaceapplication.Utils.SharedPreferencesUtil;
import com.xu.ccgv.mynearplaceapplication.Utils.UtilsMethod;
import com.xu.ccgv.mynearplaceapplication.configs.ConfigValues;
import com.xu.ccgv.mynearplaceapplication.contract.ILocationListContract;
import com.xu.ccgv.mynearplaceapplication.presenter.BasePresenter;
import com.xu.ccgv.mynearplaceapplication.presenter.IPresenterCallBack;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xz on 28/04/2018.
 */

public class LocationListPresenterImpl extends BasePresenter<ILocationListContract.ILocationView> implements ILocationListContract.ILocationPresenter {
    private ILocationListContract.ILocationView mView;
    private ILocationListContract.ILocationModel mModel;

    //for test only
    public void setTheMockModel(ILocationListContract.ILocationModel mModel) {
        this.mModel = mModel;
    }

    @Override
    public void initPresenter() {
        //using getView method in BasePresenter
        mView = getView();
        //link presenter with model to obtain the data
        mModel = new LocationListModelImpl();
    }

    //get search results
    @Override
    public void getAllLocations(final boolean isNew) {
        mModel.getAllLocations(new IPresenterCallBack<List<LocationEntity>>() {
            @Override
            public void onSuccess(List<LocationEntity> data) {
                mView.updateList(data, isNew);
            }

            @Override
            public void onFailure(String msg) {
                mView.updateList(new ArrayList<LocationEntity>(), false);
            }
        });
    }

    //get the current location
    @Override
    public void getTheLocation(int position) {
        mModel.getTheLocation(position, new IPresenterCallBack<LocationEntity>() {
            @Override
            public void onSuccess(LocationEntity data) {
                EventBus.getDefault().post(data);
            }

            @Override
            public void onFailure(String msg) {
                UtilsMethod.showToast(msg);
            }
        });
    }

    //get the location by marker on the map
    @Override
    public void getTheLocationFromMarker(Marker marker) {
        if (null != marker) {
            mModel.getTheLocationFromMarker(marker, new IPresenterCallBack<List<LocationEntity>>() {
                @Override
                public void onSuccess(List<LocationEntity> data) {
                    mView.updateList(data, false);
                }

                @Override
                public void onFailure(String msg) {
                    UtilsMethod.showToast(msg);
                }
            });
        }
    }

    //find the places by query
    @Override
    public void findAllLocations(final String query) {
        if (null == query || query.length() == 0) {
            UtilsMethod.showToast(R.string.check_your_search_name);
            getAllLocations(true);
        } else {
            mView.showLoading();
            float lat = SharedPreferencesUtil.getSharedPreferences(MyApplication.getInstance().getApplicationContext()).getCurrentLat();
            float lng = SharedPreferencesUtil.getSharedPreferences(MyApplication.getInstance().getApplicationContext()).getCurrentLng();
            if (Math.abs(lat) == 100 && Math.abs(lng) == 200) {
                UtilsMethod.showToast(R.string.inner_error_try_again);
            } else {
                LatLng latLng = new LatLng(lat, lng);
                final String next_page_token = SharedPreferencesUtil.getSharedPreferences(MyApplication.getInstance().getApplicationContext()).getString(ConfigValues.next_page_token, "");

                mModel.findAllLocations(latLng, query, next_page_token, new IPresenterCallBack<List<LocationEntity>>() {
                    @Override
                    public void onSuccess(List<LocationEntity> data) {
                        if (next_page_token.equals(""))
                            mView.updateList(data, true);
                        else
                            mView.updateList(data, false);
                        SharedPreferencesUtil.getSharedPreferences(MyApplication.getInstance().getApplicationContext()).putString(ConfigValues.QUERY, query);
                        mView.hideLoading();
                    }

                    @Override
                    public void onFailure(String msg) {
                        mView.hideLoading();
                        UtilsMethod.showToast(msg);
                    }
                });
            }
        }
    }
}
