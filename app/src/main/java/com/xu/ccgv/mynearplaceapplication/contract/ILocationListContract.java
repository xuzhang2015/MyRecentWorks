package com.xu.ccgv.mynearplaceapplication.contract;


import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.xu.ccgv.mynearplaceapplication.Bean.LocationEntity;
import com.xu.ccgv.mynearplaceapplication.Model.BaseModel;
import com.xu.ccgv.mynearplaceapplication.presenter.IPresenterCallBack;
import com.xu.ccgv.mynearplaceapplication.view.BaseView;

import java.util.List;

/**
 * Created by xz on 28/04/2018.
 */

public interface ILocationListContract {
    interface ILocationModel extends BaseModel {
        void getAllLocations(IPresenterCallBack<List<LocationEntity>> presenterCallBack);

        void getTheLocation(int position, IPresenterCallBack<LocationEntity> presenterCallBack);

        void getTheLocationFromMarker(Marker marker, IPresenterCallBack<List<LocationEntity>> presenterCallBack);

        void findAllLocations(LatLng location, String query, String next_page_token, IPresenterCallBack<List<LocationEntity>> presenterCallBack);
    }

    interface ILocationView extends BaseView {
        void updateList(List<LocationEntity> data, boolean isNew);

        void showLoading();

        void hideLoading();
    }

    interface ILocationPresenter {
        void initPresenter();

        void getAllLocations(boolean isNew);

        void getTheLocation(int position);

        void getTheLocationFromMarker(Marker marker);

        void findAllLocations(String query);

    }
}
