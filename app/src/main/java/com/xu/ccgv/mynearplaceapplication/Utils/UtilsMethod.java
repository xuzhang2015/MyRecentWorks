package com.xu.ccgv.mynearplaceapplication.Utils;

import android.app.ActivityManager;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.MediaStore;
import android.support.annotation.StringRes;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xu.ccgv.mynearplaceapplication.Bean.LocationEntity;
import com.xu.ccgv.mynearplaceapplication.Bean.domain.MyObject;
import com.xu.ccgv.mynearplaceapplication.MyApplication;
import com.xu.ccgv.mynearplaceapplication.R;
import com.xu.ccgv.mynearplaceapplication.configs.ConfigValues;
import com.xu.ccgv.mynearplaceapplication.database.DatabaseHelper;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by xz on 29/04/2018.
 */

public class UtilsMethod {

    private static final String TAG = "Utils";
    //
    public static Type locationType = new TypeToken<Location>() {
    }.getType();
    private static DecimalFormat df1 = new DecimalFormat(".#");

    public static String distanceFormat(double d) {
        return df1.format(d);
    }

    public static String getStringMethod(@StringRes int stringId) {
        return MyApplication.getInstance().getApplicationContext().getResources().getString(stringId);
    }

    //simple toast method
    public static void showToast(@StringRes int stringId) {
        Toast.makeText(MyApplication.getInstance().getApplicationContext(), getStringMethod(stringId), Toast.LENGTH_SHORT).show();
    }

    //simple toast method
    public static void showToast(String msg) {
        Toast.makeText(MyApplication.getInstance().getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    public static boolean checkInternetConnection(Context context) {
        NetworkInfo info = null;
        try {
            info = (NetworkInfo) ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        } catch (Exception e) {
            Log.d(TAG, e.getMessage(), e);
            return false;
        }
        if (info == null) return false;
        else if (info.isConnected()) return true;
        else return false;
    }

    public static String getImageUri(Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(MyApplication.getInstance().getApplicationContext().getContentResolver(), inImage, "Title", null);
        return path;
    }

    public static Bitmap covertBlobToBitmap(byte[] blob) {
        Bitmap bmp = null;
        try {
            bmp = BitmapFactory.decodeByteArray(blob, 0, blob.length);
        } catch (Exception e) {
            Log.d(TAG, e.getMessage(), e);
        }
        return bmp;
    }

    public static byte[] covertBitmapToBlob(Bitmap bmp) {
        byte[] blob = null;
        try {
            final ByteArrayOutputStream os = new ByteArrayOutputStream();
            // Bitmap compress to png
            bmp.compress(Bitmap.CompressFormat.PNG, 100, os);
            blob = os.toByteArray();
        } catch (Exception e) {
            Log.d(TAG, e.getMessage(), e);
        }
        return blob;
    }

    public static void GetTheLocationList(List<LocationEntity> mData) {
        Gson gson = new Gson();
        Cursor cur = DatabaseHelper.getAll(MyApplication.getInstance().getApplicationContext(), ConfigValues.PLACE_RESULT_TABLE);
        if (cur.moveToFirst()) {
            do {
                LocationEntity object = new LocationEntity();
                Location location = gson.fromJson(cur.getString(1), locationType);
                object.setLocation(location);
                object.setIcon_uri(cur.getString(2));
                object.setIcon_bm(UtilsMethod.covertBlobToBitmap(cur.getBlob(3)));
                object.setName(cur.getString(4));
                object.setVicinity(cur.getString(5));
                object.setType(cur.getString(6));
                object.setDirection(cur.getString(7));
                object.setDistance(cur.getDouble(8));
                //
                mData.add(object);
            }
            while (cur.moveToNext());
        }
        cur.moveToFirst();
        cur.close();
        //sort the data by distance
        Collections.sort(mData, new compareTheDataByDistance());

    }

    public static ContentValues BuildPlaceContentValues(LocationEntity data) {
        ContentValues cv = new ContentValues();
        Gson gson = new Gson();
        String[] contents = MyApplication.getInstance().getApplicationContext().getResources().getStringArray(R.array.place_database_column);
        String location = gson.toJson(data.getLocation());
        cv.put(contents[0], location);
        cv.put(contents[1], data.getIcon_uri());
        cv.put(contents[2], UtilsMethod.covertBitmapToBlob(data.getIcon_bm()));
        cv.put(contents[3], data.getName());
        cv.put(contents[4], data.getVicinity());
        cv.put(contents[5], data.getType());
        cv.put(contents[6], data.getDirection());
        cv.put(contents[7], data.getDistance());
        //
        return cv;
    }

    public static boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) MyApplication.getInstance().getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static List<LocationEntity> covertDomainObjectToData(MyObject myObject) {
        List<LocationEntity> data = new ArrayList<>();
        for (int i = 0; i < myObject.getResults().size(); i++) {
            LocationEntity entity = new LocationEntity();
            entity.setIcon_uri(myObject.getResults().get(i).getIcon());
            entity.setName(myObject.getResults().get(i).getName());
            entity.setVicinity(myObject.getResults().get(i).getVicinity());
            Location location = new Location("");
            location.setLatitude(myObject.getResults().get(i).getGeometry().getLocation().getLat());
            location.setLongitude(myObject.getResults().get(i).getGeometry().getLocation().getLng());
            entity.setLocation(location);
            data.add(entity);
        }
        SharedPreferencesUtil.getSharedPreferences(MyApplication.getInstance().getApplicationContext()).putString(ConfigValues.next_page_token, myObject.getNext_page_token());


        return data;
    }

    //v is 0 to 360, 0 is N, 90 is E, 180 is S, 270 is W
    public static String getTheDirection(float v) {
        if (v < 22.5 || v >= 337.5) return "N";
        if (v >= 22.5 && v < 67.5) return "NE";
        if (v >= 67.5 && v < 112.5) return "E";
        if (v >= 112.5 && v < 157.5) return "SE";
        if (v >= 157.5 && v < 202.5) return "S";
        if (v >= 202.5 && v < 247.5) return "SW";
        if (v >= 247.5 && v < 292.5) return "W";
        if (v >= 292.5 && v < 337.5) return "NW";
        return "N";
    }

    public static class compareTheDataByDistance implements Comparator<LocationEntity> {
        @Override
        public int compare(LocationEntity o1, LocationEntity o2) {
            return Double.compare(o1.getDistance(), o2.getDistance());
        }
    }
}
