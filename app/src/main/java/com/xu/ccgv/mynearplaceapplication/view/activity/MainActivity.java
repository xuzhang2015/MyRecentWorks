package com.xu.ccgv.mynearplaceapplication.view.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.xu.ccgv.mynearplaceapplication.Bean.LocationEntity;
import com.xu.ccgv.mynearplaceapplication.MyApplication;
import com.xu.ccgv.mynearplaceapplication.R;
import com.xu.ccgv.mynearplaceapplication.Utils.SharedPreferencesUtil;
import com.xu.ccgv.mynearplaceapplication.Utils.UtilsMethod;
import com.xu.ccgv.mynearplaceapplication.base.BaseActivity;
import com.xu.ccgv.mynearplaceapplication.configs.ConfigValues;
import com.xu.ccgv.mynearplaceapplication.contract.IMapContract;
import com.xu.ccgv.mynearplaceapplication.presenter.impl.MapPresenterImpl;
import com.xu.ccgv.mynearplaceapplication.service.GPSService;
import com.xu.ccgv.mynearplaceapplication.tool.MyOfflineTileProvider;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import kr.co.namee.permissiongen.PermissionFail;
import kr.co.namee.permissiongen.PermissionSuccess;

/**
 * The activity is used to display the map and markers
 */
public class MainActivity extends BaseActivity<IMapContract.IMapView, MapPresenterImpl>
        implements IMapContract.IMapView,
        OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener {

    //
    @BindView(R.id.btn_current_location)
    FloatingActionButton fab;
    @BindView(R.id.content_bottom_sheet_list)
    View bottomSheet;
    private Unbinder unbinder;
    private BottomSheetBehavior mBottomSheetBehavior;
    private SearchView searchView;

    private GoogleMap mMap;
    private ProgressDialog progress;
    private boolean isMapReady = false;
    private Intent intent;
    //for save the Map and used to offline case
    private MyOfflineTileProvider mOfflineTileProvider;
    private TileOverlay mTileOverlay;
    //
    private List<Marker> markerList = new ArrayList<>();
    private Marker targetMaker;
    private List<Circle> circleList = new ArrayList<>();
    //


    @Override
    public MapPresenterImpl createPresenter() {
        return new MapPresenterImpl();
    }

    //setup the content
    @Override
    protected int getContentViewId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initData() {
        mPresenter.initPresenter();
    }

    @Override
    protected void initView() {
        unbinder = ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        intent = new Intent(this, GPSService.class);
        //start the service to get the location data
        startService(intent);
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        //init map
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.my_content_map);
        mapFragment.getMapAsync(this);
        //
        progress = new ProgressDialog(this);
        progress.setMessage(UtilsMethod.getStringMethod(R.string.loading));
        showLoading();
        //fab is used to find the user location
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if the service has been killed, restart the service
                if (!UtilsMethod.isMyServiceRunning(GPSService.class)) {
                    showLoading();
                    intent = new Intent(MainActivity.this, GPSService.class);
                    startService(intent);
                } else {
                    mPresenter.findLocation();
                }
            }
        });
        //in case the BitmapDescriptorFactory is not initialized
        MapsInitializer.initialize(getApplicationContext());
    }

    @Override
    public void showLoading() {
        if (!progress.isShowing()) progress.show();
    }

    @Override
    public void hideLoading() {
        if (progress.isShowing()) progress.dismiss();
    }

    @Override
    public void moveCameraTo(LatLng place) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place, 20));
    }

    //update the markers in the map
    @Override
    public void updateMarker(List<LocationEntity> data) {
        if (null != data && data.size() > 0) {
            removeAllMarkers();
            for (int i = 0; i < data.size(); i++) {
                Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(data.get(i).getLocation().getLatitude(), data.get(i).getLocation().getLongitude()))
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                        .title(data.get(i).getName())
                        .snippet(data.get(i).getVicinity()));
//                marker.hideInfoWindow();
                markerList.add(marker);
            }
        }
    }

    //draw the user's location and search range pin
    @Override
    public void showThePoint(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        moveCameraTo(latLng);
        int range = SharedPreferencesUtil.getSharedPreferences(this).getInt(ConfigValues.SEARCH_RADIUS, 500);
        drawPinOntheTargetLocation(latLng, range);
    }

    //save the search view test history
    @Override
    public void updateSearchView(String query) {
        if (searchView != null)
            searchView.setQuery(query, false);
    }

    //get the data from the Service
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void OnGetCurrentPositionEventProgress(final Location location) {
        //save the location into preference as user profile
        SharedPreferencesUtil.getSharedPreferences(this).putFloat(ConfigValues.LAT, (float) location.getLatitude());
        SharedPreferencesUtil.getSharedPreferences(this).putFloat(ConfigValues.LNG, (float) location.getLongitude());
        //use accuracy as the radius of the point
        SharedPreferencesUtil.getSharedPreferences(this).putFloat(ConfigValues.ACCURACY, (float) location.getAccuracy());
        //get the search range
        int range = SharedPreferencesUtil.getSharedPreferences(this).getInt(ConfigValues.SEARCH_RADIUS, 500);
        hideLoading();
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        moveCameraTo(latLng);
        drawPinOntheTargetLocation(latLng, range);
    }

    //listen the data from the list
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void OnGetAllLocationUpdateMapToProgress(final List<LocationEntity> data) {
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        updateMarker(data);
    }


    //listen the data from the list
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void OnGetMoveCameraToProgress(final LocationEntity entity) {
        LatLng latLng = new LatLng(entity.getLocation().getLatitude(), entity.getLocation().getLongitude());
        moveCameraTo(latLng);
        findTargetMarkerShowInfoWindow(latLng);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }


    private void findTargetMarkerShowInfoWindow(LatLng latLng) {
        for (int i = 0; i < markerList.size(); i++) {
            if (markerList.get(i).getPosition() == latLng) {
                markerList.get(i).showInfoWindow();
                break;
            }
        }
    }


    //map is ready and close the dialog
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        isMapReady = true;
        //check if online or offline
        if (!UtilsMethod.checkInternetConnection(this)) {
            goOffline();
        } else {
            goOnline();
            LatLng sydney = new LatLng(-34, 151);
            mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
            moveCameraTo(sydney);
        }
        mMap.setOnMarkerClickListener(this);
        hideLoading();
        mPresenter.getAllLocations();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        //setup the searchview in toolbar
        searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setQueryHint(UtilsMethod.getStringMethod(R.string.enter_search_place));
        searchView.setIconified(true);
        SearchView.SearchAutoComplete et = (SearchView.SearchAutoComplete) searchView.findViewById(R.id.search_src_text);
//        et.setHint(UtilsMethod.getStringMethod(R.string.enter_place_name_or_type));
//        et.setHintTextColor(Color.WHITE);
        et.setTextColor(Color.WHITE);
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                UtilsMethod.showToast("You have enter " + query);
                //reset the next page token
                SharedPreferencesUtil.getSharedPreferences(MyApplication.getInstance().getApplicationContext()).putString(ConfigValues.next_page_token, "");
                EventBus.getDefault().post(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }


        });
        //setup the radiogroup in menu
        MenuItem menu2 = menu.findItem(R.id.action_search_range);
        SubMenu subMenu2 = menu2.getSubMenu();
        int value = SharedPreferencesUtil.getSharedPreferences(this).getInt(ConfigValues.SEARCH_RADIUS, 500);
        if (500 == value) {
            subMenu2.getItem(0).setChecked(true);
        } else if (1000 == value) {
            subMenu2.getItem(1).setChecked(true);
        } else {
            subMenu2.getItem(2).setChecked(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        float lat = SharedPreferencesUtil.getSharedPreferences(this).getCurrentLat();
        float lng = SharedPreferencesUtil.getSharedPreferences(this).getCurrentLng();
        LatLng latLng = new LatLng(lat, lng);
        switch (item.getItemId()) {
            case R.id.search_500_meters:
                SharedPreferencesUtil.getSharedPreferences(this).putInt(ConfigValues.SEARCH_RADIUS, 500);
                drawPinOntheTargetLocation(latLng, 500);
                UtilsMethod.showToast("Search range is set to 500 meters");
                item.setChecked(true);
                return true;
            case R.id.search_1000_meters:
                SharedPreferencesUtil.getSharedPreferences(this).putInt(ConfigValues.SEARCH_RADIUS, 1000);
                drawPinOntheTargetLocation(latLng, 1000);
                UtilsMethod.showToast("Search range is set to 1000 meters");
                item.setChecked(true);
                return true;
            case R.id.search_1500_meters:
                SharedPreferencesUtil.getSharedPreferences(this).putInt(ConfigValues.SEARCH_RADIUS, 1500);
                drawPinOntheTargetLocation(latLng, 1500);
                UtilsMethod.showToast("Search range is set to 1500 meters");
                item.setChecked(true);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        EventBus.getDefault().unregister(this);
        if (null != intent) stopService(intent);
    }

    private void removeAllMarkers() {
        for (int i = 0; i < markerList.size(); i++) {
            markerList.get(i).remove();
        }
    }

    private void removeAllCircles() {
        for (int i = 0; i < circleList.size(); i++) {
            circleList.get(i).remove();
        }
    }

    //handle the event when use click a marker, the detail of the place will show at first item in the list
    @Override
    public boolean onMarkerClick(Marker marker) {
        marker.showInfoWindow();
        EventBus.getDefault().post(marker);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        return false;
    }

    private void drawPinOntheTargetLocation(LatLng point, int radius) {
        if (Math.abs(point.latitude) == 100 && Math.abs(point.longitude) == 200) {
            //
        } else {
            removeAllCircles();
            CircleOptions circleOptions1 = new CircleOptions();
            circleOptions1.center(point);
            circleOptions1.radius(radius);
            circleOptions1.strokeColor(Color.BLUE);
            circleOptions1.fillColor(0x5500ff00);
            circleOptions1.strokeWidth(1);

            //
            if (targetMaker != null) targetMaker.remove();
            if (isMapReady) {
                targetMaker = mMap.addMarker(new MarkerOptions()
                        .position(point)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                        .title("Me").snippet(""));
                //
                circleList.add(mMap.addCircle(circleOptions1));
            }
        }
    }

    private void goOffline() {
        mOfflineTileProvider = new MyOfflineTileProvider();
        mTileOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mOfflineTileProvider));
//        mMap.setMapType(GoogleMap.MAP_TYPE_NONE);
    }

    private void goOnline() {
        if (mTileOverlay != null) {
            mTileOverlay.remove();
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }
    }


    //check the permission
    @PermissionSuccess(requestCode = ConfigValues.GPS_PERMISSION)
    public void doSomethingPick() {
        UtilsMethod.showToast(R.string.permission_is_granted);
        initData();
        initView();
    }

    @PermissionFail(requestCode = ConfigValues.GPS_PERMISSION)
    public void doFailSomethingPick() {
        UtilsMethod.showToast(R.string.permission_is_not_granted);
        finish();
    }
}
