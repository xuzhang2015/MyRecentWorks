package com.xu.ccgv.mynearplaceapplication.contract;


import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.xu.ccgv.mynearplaceapplication.Bean.LocationEntity;
import com.xu.ccgv.mynearplaceapplication.Model.BaseModel;
import com.xu.ccgv.mynearplaceapplication.presenter.IPresenterCallBack;
import com.xu.ccgv.mynearplaceapplication.view.BaseView;

import java.util.List;

/**
 * Created by xz on 28/04/2018.
 */

public interface IMapContract {
    interface IMapModel extends BaseModel {
        void findLocation(IPresenterCallBack<Location> presenterCallBack);

        void getAllLocations(IPresenterCallBack<List<LocationEntity>> presenterCallBack);
    }

    interface IMapView extends BaseView {
        void showLoading();

        void hideLoading();

        void moveCameraTo(LatLng place);

        void updateMarker(List<LocationEntity> data);

        void showThePoint(Location location);

        void updateSearchView(String query);
    }

    interface IMapPresenter {
        void initPresenter();

        void findLocation();

        void getAllLocations();
    }
}
