package com.xu.ccgv.mynearplaceapplication.view.activity;

import android.content.Intent;
import android.view.MenuItem;

import com.xu.ccgv.mynearplaceapplication.BuildConfig;
import com.xu.ccgv.mynearplaceapplication.R;
import com.xu.ccgv.mynearplaceapplication.database.DatabaseManager;
import com.xu.ccgv.mynearplaceapplication.service.GPSService;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.fakes.RoboMenuItem;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.shadows.ShadowToast;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.robolectric.Shadows.shadowOf;

/**
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 18, manifest = "AndroidManifest.xml")
public class MainActivityTest {
    private MainActivity activity;
    private DatabaseManager databaseHelper;

    public static void assertIntent(Intent expected, Intent actual) {
        assertEquals(expected.toString(), actual.toString());
    }

    @Before
    public void setUp() throws Exception {
        activity = Robolectric.buildActivity(MainActivity.class).create().start().resume().get();
        databaseHelper = DatabaseManager.getInstance(activity);
    }

    @After
    public void tearDown() throws Exception {
        activity = null;
        databaseHelper.close();
    }

    @Test
    public void testLifecycle() throws Exception {
        assertNotNull(activity);
    }

    @Test
    public void testFunction() {

        //click current location btn to start a service
        activity.findViewById(R.id.btn_current_location).performClick();
        Intent expectedIntent = new Intent(activity, GPSService.class);
        assertIntent(expectedIntent, shadowOf(activity).getNextStartedService());
        assertEquals("Permission is granted", ShadowToast.getTextOfLatestToast());
        //
    }

    @Test
    public void testMenu() {
        MenuItem menuItem = new RoboMenuItem(R.id.action_search);
        activity.onOptionsItemSelected(menuItem);
        ShadowActivity shadowActivity = Shadows.shadowOf(activity);
        shadowActivity.clickMenuItem(R.id.action_search);
        //
        shadowActivity.clickMenuItem(R.id.search_500_meters);
        assertEquals("Search range is set to 500 meters", ShadowToast.getTextOfLatestToast());
        shadowActivity.clickMenuItem(R.id.search_1000_meters);
        assertEquals("Search range is set to 1000 meters", ShadowToast.getTextOfLatestToast());
        shadowActivity.clickMenuItem(R.id.search_1500_meters);
        assertEquals("Search range is set to 1500 meters", ShadowToast.getTextOfLatestToast());
    }
}