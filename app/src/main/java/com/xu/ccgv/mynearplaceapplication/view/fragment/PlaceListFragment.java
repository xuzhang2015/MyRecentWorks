package com.xu.ccgv.mynearplaceapplication.view.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.android.gms.maps.model.Marker;
import com.xu.ccgv.mynearplaceapplication.Bean.LocationEntity;
import com.xu.ccgv.mynearplaceapplication.MyApplication;
import com.xu.ccgv.mynearplaceapplication.R;
import com.xu.ccgv.mynearplaceapplication.Utils.SharedPreferencesUtil;
import com.xu.ccgv.mynearplaceapplication.Utils.UtilsMethod;
import com.xu.ccgv.mynearplaceapplication.adapter.LocationListAdapter;
import com.xu.ccgv.mynearplaceapplication.base.BaseFragment;
import com.xu.ccgv.mynearplaceapplication.base.BaseRecyclerViewAdapter;
import com.xu.ccgv.mynearplaceapplication.configs.ConfigValues;
import com.xu.ccgv.mynearplaceapplication.contract.ILocationListContract;
import com.xu.ccgv.mynearplaceapplication.presenter.impl.LocationListPresenterImpl;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * The fragment is used to display the place list
 */
public class PlaceListFragment extends BaseFragment<ILocationListContract.ILocationView, LocationListPresenterImpl>
        implements ILocationListContract.ILocationView, BaseRecyclerViewAdapter.OnItemClickListener {

    //
    @BindView(R.id.place_list_recycler_view)
    RecyclerView myPlaceListRecyclerView;
    private Unbinder unbinder;
    private LocationListAdapter locationListAdapter;
    private LinearLayoutManager layoutManager;
    private ProgressDialog progress;
    //
    private boolean loading = true;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;

    public PlaceListFragment() {
    }

    // TODO: Rename and change types and number of parameters
    public static PlaceListFragment newInstance() {
        PlaceListFragment fragment = new PlaceListFragment();
        return fragment;
    }

    @Override
    public LocationListPresenterImpl createPresenter() {
        return new LocationListPresenterImpl();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_place_list;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        unbinder = ButterKnife.bind(this, view);
        layoutManager = new LinearLayoutManager(mActivity);
        setupRecyclerView();
    }

    private void setupRecyclerView() {
        locationListAdapter = new LocationListAdapter(mActivity, new ArrayList<LocationEntity>(), R.layout.place_list_item);
        locationListAdapter.setOnItemClickListener(this);
        this.myPlaceListRecyclerView.setLayoutManager(layoutManager);
        this.myPlaceListRecyclerView.setAdapter(locationListAdapter);
        //add the listener for more place search
        this.myPlaceListRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) //check for scroll down
                {
                    visibleItemCount = layoutManager.getChildCount();
                    totalItemCount = layoutManager.getItemCount();
                    pastVisiblesItems = layoutManager.findFirstVisibleItemPosition();
                    //if not in loading status, then load the data
                    if (loading) {
                        //check if reach to the bottom
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            //set the loading is false to avoid load the data frequently
                            loading = false;
                            //find the location by query
                            String query = SharedPreferencesUtil.getSharedPreferences(MyApplication.getInstance().getApplicationContext()).getString(ConfigValues.QUERY, "");
                            mPresenter.findAllLocations(query);
                        }
                    }
                }
            }
        });

        //
        progress = new ProgressDialog(getActivity());
        progress.setMessage(UtilsMethod.getStringMethod(R.string.loading));
    }


    @Override
    protected void initData() {
        EventBus.getDefault().register(this);
        //init the presenter and data
        mPresenter.initPresenter();
        mPresenter.findAllLocations("");
    }

    //update the recycler view
    @Override
    public void updateList(List<LocationEntity> data, boolean isNew) {
        if (isNew) {//if is new target search
            locationListAdapter = new LocationListAdapter(mActivity, data, R.layout.place_list_item);
            locationListAdapter.setOnItemClickListener(this);
            this.myPlaceListRecyclerView.setAdapter(locationListAdapter);
        } else {//the same target search, so search more place results
            locationListAdapter.notifyDataSetChanged();
        }
        EventBus.getDefault().post(data);
        //after update the list, allow new loading event
        loading = true;
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
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        EventBus.getDefault().unregister(this);

    }

    //listen the data from the list
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void OnGetPlaceNameToProgress(final String query) {
        mPresenter.findAllLocations(query);
    }

    //listen the marker select
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void OnGetPlaceFromMarkerProgress(final Marker marker) {
        mPresenter.getTheLocationFromMarker(marker);
    }

    //list item click listener to find the marker in the map
    @Override
    public void onItemClickListener(View v, int position) {
        mPresenter.getTheLocation(position);
    }

    @Override
    public void onClick(View view) {

    }
}