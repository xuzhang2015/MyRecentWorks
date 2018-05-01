package com.xu.ccgv.mynearplaceapplication.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.xu.ccgv.mynearplaceapplication.configs.ConfigValues;


/**
 * Created by xz on 29/04/2018.
 */

public class SharedPreferencesUtil {

    private static SharedPreferencesUtil spUtil;
    private SharedPreferences sp;

    private SharedPreferencesUtil(Context context) {
        sp = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static SharedPreferencesUtil getSharedPreferences(Context context) {
        if (spUtil == null) {
            spUtil = new SharedPreferencesUtil(context);
        }
        return spUtil;
    }

    public void remove(String key) {
        if (sp != null) {
            sp.edit().remove(key).commit();
        }
    }

    public void putBoolean(String key, boolean value) {
        if (sp != null) {
            sp.edit().putBoolean(key, value).commit();
        }
    }

    public void putFloat(String key, float value) {
        if (sp != null) {
            sp.edit().putFloat(key, value).commit();
        }
    }

    public void putInt(String key, int value) {
        if (sp != null) {
            sp.edit().putInt(key, value).commit();
        }
    }

    public void putLong(String key, long value) {
        if (sp != null) {
            sp.edit().putLong(key, value).commit();
        }
    }

    public void putString(String key, String value) {
        if (sp != null) {
            sp.edit().putString(key, value).commit();
        }
    }

    public String getString(String key, String defValue) {
        String value = null;
        if (sp != null) {
            value = sp.getString(key, defValue);
        }
        return value;
    }

    public boolean getBoolean(String key, boolean defValue) {
        boolean value = false;
        if (sp != null) {
            value = sp.getBoolean(key, defValue);
        }
        return value;
    }

    public int getInt(String key, int defValue) {
        int value = 0;
        if (sp != null) {
            value = sp.getInt(key, defValue);
        }
        return value;
    }

    public long getLong(String key, long defValue) {
        long value = 0;
        if (sp != null) {
            value = sp.getLong(key, defValue);
        }
        return value;
    }

    public float getFloat(String key, float defValue) {
        float value = 0;
        if (sp != null) {
            value = sp.getFloat(key, defValue);
        }
        return value;
    }


    public float getCurrentLat() {
        //lat abs[0 90]
        float value = 100;
        if (sp != null) {
            value = sp.getFloat(ConfigValues.LAT, value);
        }
        return value;
    }

    public float getCurrentLng() {
        //lng abs[0 180]
        float value = 200;
        if (sp != null) {
            value = sp.getFloat(ConfigValues.LNG, value);
        }
        return value;
    }

    public float getAccuracy() {
        //default as 20 meter
        float value = 20;
        if (sp != null) {
            value = sp.getFloat(ConfigValues.ACCURACY, value);
        }
        return value;
    }

    public void clearAll() {
        if (sp != null) {
            sp.edit().clear().commit();
        }
    }
}
