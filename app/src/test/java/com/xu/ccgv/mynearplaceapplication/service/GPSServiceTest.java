package com.xu.ccgv.mynearplaceapplication.service;

import android.content.Intent;
import android.test.ServiceTestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class GPSServiceTest extends ServiceTestCase<GPSService> {

    private GPSService mGPSService;

    public GPSServiceTest() {
        super(GPSService.class);
    }

    @Before
    public void setUp() throws Exception {
        startGPSservice();
    }

    @After
    public void tearDown() throws Exception {
        mGPSService.stopSelf();
    }


    @Test
    public void TestService() throws Exception {
        assertNotNull(mGPSService);
    }


    private void startGPSservice() {
        Intent intent = new Intent(mContext, GPSService.class);
        startService(intent);
        mGPSService = getService();
    }
}