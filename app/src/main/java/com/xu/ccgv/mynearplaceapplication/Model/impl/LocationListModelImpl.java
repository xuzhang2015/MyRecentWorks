package com.xu.ccgv.mynearplaceapplication.Model.impl;

import android.graphics.Bitmap;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.xu.ccgv.mynearplaceapplication.Bean.LocationEntity;
import com.xu.ccgv.mynearplaceapplication.Bean.domain.MyObject;
import com.xu.ccgv.mynearplaceapplication.MyApplication;
import com.xu.ccgv.mynearplaceapplication.R;
import com.xu.ccgv.mynearplaceapplication.Utils.UtilsMethod;
import com.xu.ccgv.mynearplaceapplication.configs.ConfigValues;
import com.xu.ccgv.mynearplaceapplication.contract.ILocationListContract;
import com.xu.ccgv.mynearplaceapplication.database.DatabaseHelper;
import com.xu.ccgv.mynearplaceapplication.net.MyAPIAccess;
import com.xu.ccgv.mynearplaceapplication.presenter.IPresenterCallBack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by xz on 28/04/2018.
 */

public class LocationListModelImpl implements ILocationListContract.ILocationModel {

    private final String TAG = "LocationListModelImpl";
    private List<LocationEntity> mData = new ArrayList<>();


    @Override
    public void getAllLocations(IPresenterCallBack<List<LocationEntity>> presenterCallBack) {
        mData = new ArrayList<>();
        UtilsMethod.GetTheLocationList(mData);
        presenterCallBack.onSuccess(mData);
    }

    @Override
    public void getTheLocation(int position, IPresenterCallBack<LocationEntity> presenterCallBack) {
        LocationEntity entity = null;
        try {
            entity = mData.get(position);
        } catch (Exception e) {
            Log.d(TAG, e.getMessage(), e);
        }
        if (null == entity)
            presenterCallBack.onFailure(UtilsMethod.getStringMethod(R.string.inner_error_try_again));
        else presenterCallBack.onSuccess(entity);
    }

    @Override
    public void getTheLocationFromMarker(Marker marker, IPresenterCallBack<List<LocationEntity>> presenterCallBack) {
        int index = -1;
        if (mData.size() == 0) UtilsMethod.GetTheLocationList(mData);
        for (int i = 0; i < mData.size(); i++) {
            if (marker.getTitle().equals(mData.get(i).getName()) && marker.getSnippet().equals(mData.get(i).getVicinity())) {
                index = i;
            }
        }
        if (index >= 0) {
            Collections.swap(mData, 0, index);
            presenterCallBack.onSuccess(mData);
        } else {
            presenterCallBack.onFailure(UtilsMethod.getStringMethod(R.string.cannot_find_the_place_detail));
        }

    }

    @Override
    public void findAllLocations(LatLng location, String query, String next_page_toke, IPresenterCallBack<List<LocationEntity>> presenterCallBack) {
        if (UtilsMethod.checkInternetConnection(MyApplication.getInstance().getApplicationContext())) {
            new findTheLocationsByType(location, query, next_page_toke, presenterCallBack).execute();
        } else {
            presenterCallBack.onFailure(UtilsMethod.getStringMethod(R.string.please_check_internet_connection));
        }
    }


    //Async to call API
    private class findTheLocationsByType extends AsyncTask<Void, Void, Void> {

        private IPresenterCallBack<List<LocationEntity>> presenterCallBack;
        private LatLng location;
        private String query;
        private String next_page_token;
        private List<LocationEntity> data;
        private Boolean isNew = false;


        public findTheLocationsByType(LatLng location, String query, String next_page_token, IPresenterCallBack<List<LocationEntity>> presenterCallBack) {
            this.location = location;
            this.query = query;
            this.next_page_token = next_page_token;
            this.presenterCallBack = presenterCallBack;
        }

        @Override
        protected Void doInBackground(Void... params) {
            MyObject myObject = MyAPIAccess.<MyObject>getTheNearByLocations(location, query, next_page_token);
            if (null != myObject) {
                if (next_page_token.equals("")) isNew = true;
                data = UtilsMethod.covertDomainObjectToData(myObject);
                //update database
                if (data.size() > 0) {
                    //
                    Location currentLocation = new Location("");
                    currentLocation.setLatitude(location.latitude);
                    currentLocation.setLongitude(location.longitude);
                    if (isNew) {
                        //delete the previous records
                        DatabaseHelper.deleteAll(MyApplication.getInstance().getApplicationContext(), ConfigValues.PLACE_RESULT_TABLE);
                    }
                    //add new records
                    for (int i = 0; i < data.size(); i++) {
                        //get bitmap
                        data.get(i).setIcon_bm(MyAPIAccess.<Bitmap>getBitmapFromURL(data.get(i).getIcon_uri()));
                        //get distance
                        data.get(i).setDistance(currentLocation.distanceTo(data.get(i).getLocation()));
                        //get the direction
                        data.get(i).setDirection(UtilsMethod.getTheDirection(currentLocation.bearingTo(data.get(i).getLocation())));

                        DatabaseHelper.add(MyApplication.getInstance().getApplicationContext(),
                                ConfigValues.PLACE_RESULT_TABLE,
                                UtilsMethod.BuildPlaceContentValues(data.get(i)));

                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (null != data) {
                if (isNew) {
                    mData = new ArrayList<>();
                    mData = data;
                } else {
                    mData.addAll(data);
                }
                Collections.sort(mData, new UtilsMethod.compareTheDataByDistance());
                presenterCallBack.onSuccess(mData);
            } else {
                presenterCallBack.onFailure(UtilsMethod.getStringMethod(R.string.cannot_find_places));
            }
        }
    }
}
