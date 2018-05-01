package com.xu.ccgv.mynearplaceapplication.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.xu.ccgv.mynearplaceapplication.configs.ConfigValues;


/**
 * Created by xz on 29/04/2018.
 */

public class DatabaseManager extends SQLiteOpenHelper {
    //
//    private String icon_uri;
//    private Bitmap icon_bm;
//    private String name;
//    private String vicinity;
//    private String type;
//   private String direction;
//    private double distance;
    private static final String PLACE_RESULT_TABLE = "  create table place_result_table (" +
            "_id integer primary key autoincrement, " + //0
            "location                           TEXT," +//1
            "icon_uri                           TEXT," +//2
            "icon_bmp                           BLOB," +//3
            "name                               TEXT," +//4
            "vicinity                           TEXT," +//5
            "type                               TEXT," +//6
            "direction                          TEXT," +//7
            "distance                           INTEGER);";//8
    private static DatabaseManager mInstance = null;
    private final String TAG = "DataBaseManager";

    public DatabaseManager(Context context) {
        super(context, ConfigValues.DATABASE_NAME, null, ConfigValues.DATABASE_VERSION);
    }

    public static DatabaseManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new DatabaseManager(context.getApplicationContext());
        }
        return mInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(PLACE_RESULT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}

