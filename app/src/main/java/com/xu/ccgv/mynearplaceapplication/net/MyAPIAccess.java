package com.xu.ccgv.mynearplaceapplication.net;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xu.ccgv.mynearplaceapplication.Bean.domain.MyObject;
import com.xu.ccgv.mynearplaceapplication.MyApplication;
import com.xu.ccgv.mynearplaceapplication.R;
import com.xu.ccgv.mynearplaceapplication.Utils.SharedPreferencesUtil;
import com.xu.ccgv.mynearplaceapplication.Utils.UtilsMethod;
import com.xu.ccgv.mynearplaceapplication.configs.ConfigValues;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.xu.ccgv.mynearplaceapplication.configs.ConfigValues.googlePlaceEndpoit;


/**
 * Created by xz on 29/04/2018.
 */

public class MyAPIAccess<T> {
    private static final String TAG = "API";

    //get the nearby locations
    public static MyObject getTheNearByLocations(LatLng latLng, String name, String next_page_token) {
        name = name.replaceAll(" ", "%20");
        int radius = SharedPreferencesUtil.getSharedPreferences(MyApplication.getInstance().getApplicationContext()).getInt(ConfigValues.SEARCH_RADIUS, 500);
        String key = UtilsMethod.getStringMethod(R.string.server_key);
        String url = googlePlaceEndpoit + "location=" + latLng.latitude + "," + latLng.longitude + "&radius=" + radius + "&type=" + name + "&key=" + key;
        if (!next_page_token.equals("")) url += "&pagetoken=" + next_page_token;
        MyObject object = getAPIResult(url, new TypeToken<MyObject>() {
        }.getType());
        //
        return null == object ? null : object;
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            Log.d(TAG, e.getMessage(), e);
            return null;
        }
    }

    private static <T> T getAPIResult(String url, Type type) {
        Gson gson = new Gson();
        HttpURLConnection urlConnection = null;
        try {
            //
            urlConnection = (HttpURLConnection) new URL(url).openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setConnectTimeout(5000);
            urlConnection.connect();

            //
            Log.d(TAG, "response code:" + urlConnection.getResponseCode());
            Log.d(TAG, "response msg:" + urlConnection.getResponseMessage());
            //check if the access token had expired
            if (urlConnection.getResponseCode() != 200) {
                urlConnection.disconnect();
                return null;
            } else {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                T object = gson.fromJson(inputStreamToString(in).toString(), type);

                return object;
            }
        } catch (Exception e) {
            return null;
        } finally {
            if (urlConnection != null) {
                // TODO: this might throw exception itself
                urlConnection.disconnect();
            }
        }
    }

    //
    private static String inputStreamToString(InputStream is) throws IOException {
        String line = "";
        StringBuilder total = new StringBuilder();
        // Wrap a BufferedReader around the InputStream
        BufferedReader rd = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        // Read response until the end
        while ((line = rd.readLine()) != null) {
            total.append(line);
        }
        // Return full string
        return total.toString();
    }
}
