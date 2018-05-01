package com.xu.ccgv.mynearplaceapplication.view.fragment;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;

import com.xu.ccgv.mynearplaceapplication.BuildConfig;
import com.xu.ccgv.mynearplaceapplication.R;
import com.xu.ccgv.mynearplaceapplication.base.BaseFragment;
import com.xu.ccgv.mynearplaceapplication.database.DatabaseManager;
import com.xu.ccgv.mynearplaceapplication.view.activity.MainActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertNotNull;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 18, manifest = "AndroidManifest.xml")
public class PlaceListFragmentTest {

    private PlaceListFragment fragment;
    private DatabaseManager databaseHelper;

    public static void startFragment(BaseFragment fragment) {
        MainActivity activity = Robolectric.buildActivity(MainActivity.class)
                .create()
                .start()
                .resume()
                .get();

        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(fragment, null);
        fragmentTransaction.commit();
    }

    @Before
    public void setUp() throws Exception {
        fragment = PlaceListFragment.newInstance();
        startFragment(fragment);
        databaseHelper = DatabaseManager.getInstance(fragment.getActivity());


    }

    @After
    public void tearDown() throws Exception {
        databaseHelper.close();
        fragment = null;
    }

    @Test
    public void shouldNotBeNull() throws Exception {
        assertNotNull(fragment);
    }

    @Test
    public void testRecyclerView() throws Exception {
        RecyclerView recyclerView = fragment.getActivity().findViewById(R.id.place_list_recycler_view);
        recyclerView.measure(0, 0);
        recyclerView.layout(0, 0, 100, 1000);
    }
}