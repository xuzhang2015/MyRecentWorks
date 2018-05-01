package com.xu.ccgv.mynearplaceapplication.presenter.impl;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.xu.ccgv.mynearplaceapplication.Bean.LocationEntity;
import com.xu.ccgv.mynearplaceapplication.Model.impl.LocationListModelImpl;
import com.xu.ccgv.mynearplaceapplication.Utils.SharedPreferencesUtil;
import com.xu.ccgv.mynearplaceapplication.Utils.UtilsMethod;
import com.xu.ccgv.mynearplaceapplication.presenter.IPresenterCallBack;
import com.xu.ccgv.mynearplaceapplication.view.fragment.PlaceListFragment;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class LocationListPresenterImplTest {
    @Mock
    private PlaceListFragment mView;

    @Mock
    private LocationListModelImpl mModel;

    private LocationListPresenterImpl presenter;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mView = mock(PlaceListFragment.class);
        mModel = mock(LocationListModelImpl.class);
        mock(SharedPreferencesUtil.class);
        mock(UtilsMethod.class);
        //
        presenter = new LocationListPresenterImpl();
        presenter.attachView(mView);
        presenter.initPresenter();
        presenter.setTheMockModel(mModel);
    }


    @Test
    public void testLocations() {
        LocationListPresenterImpl presenter_spy = spy(presenter);
        presenter_spy.getAllLocations(anyBoolean());
        presenter_spy.getTheLocation(anyInt());
        presenter_spy.getTheLocationFromMarker((Marker) any());
        //due to the static method and marker cannot be mock, these method are not successfully test.
        //I need to change the the some method structures to achieve the mock and test, I will modify them in future work
//        presenter_spy.findAllLocations(" ");
//        presenter_spy.findAllLocations(anyString());
//        verify(mModel).getTheLocationFromMarker((Marker) anyObject(),(IPresenterCallBack<List<LocationEntity>>)any());
        verify(mModel).getAllLocations((IPresenterCallBack<List<LocationEntity>>) any());
        verify(mModel).getTheLocation(anyInt(), (IPresenterCallBack<LocationEntity>) any());
        verify(mModel).findAllLocations((LatLng) any(), anyString(), anyString(), (IPresenterCallBack<List<LocationEntity>>) any());
        //

    }
}