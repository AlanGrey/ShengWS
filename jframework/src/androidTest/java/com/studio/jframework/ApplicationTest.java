package com.studio.jframework;

import android.app.Application;
import android.os.Environment;
import android.test.ApplicationTestCase;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.jstudio.base.BaseAppCompatActivity;
import com.jstudio.thirdparty.QiniuImageTool;
import com.jstudio.utils.JLog;
import com.jstudio.utils.JsonUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }

    public void testGsonNum() {
        String str = "[{'id': '1','code': 'bj','name': '北京','map': '39.90403, 116.40752599999996'}, {'id': '2','code': 'sz','name': '深圳','map': '22.543099, 114.05786799999998'}, {'id': '9','code': 'sh','name': '上海','map': '31.230393,121.473704'}, {'id': '10','code': 'gz','name': '广州','map': '23.129163,113.26443500000005'}]";
        String singleStr = "{'id': '1','code': 'bj','name': '北京','map': '39.90403, 116.40752599999996'}";

        JsonParser parser = new JsonParser();

        JsonArray array = parser.parse(str).getAsJsonArray();
        List<City> citylist = JsonUtils.parseToList(City.class, array);
        for (City city : citylist) {
            System.out.println(city.toString());
        }
        List<City> citylist2 = JsonUtils.parseToList(City.class, str);
        for (City city : citylist2) {
            System.out.println(city.toString());
        }

    }

    public void testExternalStorage() {
        System.out.println("--->" + Environment.getExternalStorageDirectory().getAbsolutePath());
    }

    public static <T> void parse(String str, T t) {
        Gson gson = new Gson();
        List<T> rs;
        Type type = new TypeToken<ArrayList<T>>() {
        }.getType();
        rs = gson.fromJson(str, type);
        for (T o : rs) {
            System.out.println(o.toString());
        }
    }

    public void testLog() {
        JLog.d(BaseAppCompatActivity.TAG, "okok");
    }

    public void testQiniu() {
        String url = "aaaaaaa";
        System.out.println("----->width" + QiniuImageTool.getScaleByWidth(url, 500));
        System.out.println("----->height" + QiniuImageTool.getScaleByHeight(url, 500));
        System.out.println("----->both" + QiniuImageTool.getScaleByBoth(url, 200, 500));
    }
}