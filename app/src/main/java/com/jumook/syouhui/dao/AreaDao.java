package com.jumook.syouhui.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class AreaDao {
    //public static final String PATH = "/data/data/com.jumook.syouhui/local_databases/area.db";
    public static final String TABLE = "area";
    public static final String NAME = "name";
    public static final String ID = "id";
    public static final String PARENT_ID = "parent_id";

    private static SQLiteDatabase db;

    public AreaDao(Context context) {
        if (db == null) {
            AssetsDatabaseManager.initManager(context.getApplicationContext());
            AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
            db = mg.getDatabase("area.db");
        }
    }

    public String getAreaFullName(int id) {
        Cursor c = db.query(TABLE, new String[]{PARENT_ID}, ID + "=" + id, null, null, null, null);
        int parent_id = 0;
        if (c.moveToFirst()) {
            parent_id = c.getInt(0);
        }
        c.close();
        String ret = getArea(parent_id) + "." + getArea(id);
        return ret;
    }

    public String getArea(int id) {
        Cursor c = db.query(TABLE, new String[]{NAME}, ID + "=" + id, null, null, null, null);
        String name = null;
        if (c.moveToFirst()) {
            name = c.getString(0);
        }
        c.close();
        return name;
    }

    public String[] getAreas() {
        Cursor c = db.query(TABLE, new String[]{NAME}, null, null, null, null, null);
        int length = c.getCount();
        String[] s = new String[length];
        for (int i = 0; c.moveToNext(); i++) {
            s[i] = c.getString(0);
        }
        c.close();
        return s;
    }

    public int getAreasId(String area) {
        Cursor cursor = db.query(TABLE, new String[]{ID}, NAME + "=" + "'" + area + "'", null, null, null, null);
        int length = cursor.getCount();
        int[] ids = new int[length];
        for (int i = 0; cursor.moveToNext(); i++) {
            ids[i] = cursor.getInt(0);
        }
        cursor.close();
        return ids[0];
    }

    public String[] getAreasProvince() {
        Cursor c = db.query(TABLE, new String[]{NAME}, PARENT_ID + " is null", null, null, null, null);
        int length = c.getCount();
        String[] s = new String[length];
        for (int i = 0; c.moveToNext(); i++) {
            s[i] = c.getString(0);
        }
        c.close();
        return s;
    }

    public String[] getAreasCity(String province) {
        Cursor cursor = db.query(TABLE, new String[]{ID}, NAME + "=" + "'" + province + "'", null, null, null, null);
        int length = cursor.getCount();
        int[] ids = new int[length];
        for (int i = 0; cursor.moveToNext(); i++) {
            ids[i] = cursor.getInt(0);
        }
        cursor.close();
        if (ids.length == 0)
            return null;
        Cursor c = db.query(TABLE, new String[]{NAME}, PARENT_ID + "=" + ids[0], null, null, null, null);
        length = c.getCount();
        String[] s = new String[length];
        for (int i = 0; c.moveToNext(); i++) {
            s[i] = c.getString(0);
        }
        c.close();
        return s;
    }

//
//    public List<String> getAreasCityList(String province) {
//        List<String> mCityDate = new ArrayList<>();
//        Cursor cursor = db.query(TABLE, new String[]{ID}, NAME + "=" + "'" + province + "'", null, null, null, null);
//        int length = cursor.getCount();
//        int[] ids = new int[length];
//        for (int i = 0; cursor.moveToNext(); i++) {
//            ids[i] = cursor.getInt(0);
//        }
//        cursor.close();
//        if (ids.length == 0)
//            return null;
//        Cursor c = db.query(TABLE, new String[]{NAME}, PARENT_ID + "=" + ids[0], null, null, null, null);
//        length = c.getCount();
//        String[] s = new String[length];
//        for (int i = 0; c.moveToNext(); i++) {
//            s[i] = c.getString(0);
//        }
//
//        for (int i = 0; i < s.length; i++){
//
//            mCityDate.add(i, s[i]);
//        }
//        c.close();
//        return mCityDate;
//    }


    public String[] getAreasTown(String city) {
        Cursor cursor = db.query(TABLE, new String[]{ID}, NAME + "=" + "'" + city + "'", null, null, null, null);
        int length = cursor.getCount();
        int[] ids = new int[length];
        for (int i = 0; cursor.moveToNext(); i++) {
            ids[i] = cursor.getInt(0);
        }
        cursor.close();
        if (ids.length == 0)
            return null;
        Cursor c = db.query(TABLE, new String[]{NAME}, PARENT_ID + "=" + ids[0], null, null, null, null);
        length = c.getCount();
        String[] s = new String[length];
        for (int i = 0; c.moveToNext(); i++) {
            s[i] = c.getString(0);
        }
        c.close();
        return s;
    }

    public SQLiteDatabase getDB() {
        return db;
    }

    public void close() {
        if (db != null && db.isOpen()) {
            db.close();
            db = null;
        }
    }
}
