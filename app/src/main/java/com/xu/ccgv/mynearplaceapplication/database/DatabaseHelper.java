package com.xu.ccgv.mynearplaceapplication.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by xz on 29/04/2018.
 */

public class DatabaseHelper {
    public static Cursor getAll(Context context, String table_name) {
        SQLiteDatabase db = DatabaseManager.getInstance(context.getApplicationContext()).getReadableDatabase();
        if (db == null) return null;
        //
        Cursor cur = db.query(table_name, null, null, null, null, null, null);
        return cur;
    }

    public static void deleteAll(Context context, String table_name) {
        SQLiteDatabase db = DatabaseManager.getInstance(context.getApplicationContext()).getWritableDatabase();
        if (db == null) {
            return;
        }
        db.delete(table_name, null, null);
        db.close();
    }

    public static long add(Context context, String table_name, ContentValues row) {
        SQLiteDatabase db = DatabaseManager.getInstance(context.getApplicationContext()).getWritableDatabase();
        if (db == null) {
            return -1;
        }
        long id = db.insert(table_name, null, row);
        db.close();
        return id;
    }

    public static void delete(Context context, String table_name, long id) {
        SQLiteDatabase db = DatabaseManager.getInstance(context.getApplicationContext()).getWritableDatabase();
        if (db == null) {
            return;
        }
        db.delete(table_name, "_id=" + id, null);
        db.close();
    }
}
