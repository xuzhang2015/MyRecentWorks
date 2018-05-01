package com.xu.ccgv.mynearplaceapplication.presenter.impl;

import android.location.Location;

import com.xu.ccgv.mynearplaceapplication.Bean.LocationEntity;
import com.xu.ccgv.mynearplaceapplication.Model.impl.MapModelImpl;
import com.xu.ccgv.mynearplaceapplication.presenter.IPresenterCallBack;
import com.xu.ccgv.mynearplaceapplication.view.activity.MainActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class MapPresenterImplTest {
    @Mock
    private MainActivity mView;

    @Mock
    private MapModelImpl mModel;


    private MapPresenterImpl presenter;


    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mView = mock(MainActivity.class);
        mModel = mock(MapModelImpl.class);
        //
        presenter = new MapPresenterImpl();
        presenter.attachView(mView);
        presenter.initPresenter();
        presenter.setTheMockModel(mModel);
    }

    @After
    public void tearDown() throws Exception {
        presenter = null;
    }


    @Test
    public void testLocations() {
        MapPresenterImpl presenter_spy = spy(presenter);
        //check presenter
        presenter_spy.findLocation();
        presenter_spy.getAllLocations();
        //check the model
        verify(mModel).findLocation((IPresenterCallBack<Location>) any());
        verify(mModel).getAllLocations((IPresenterCallBack<List<LocationEntity>>) any());
        ;
        //

    }
}

