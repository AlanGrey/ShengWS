package com.jumook.syouhui.bean;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 选择对象
 * Created by zbb on 2016/6/14.
 */
public class OptionItem {

    public int id;
    public String name;
    public boolean isChecked;

    public OptionItem() {
    }

    public OptionItem(int id, String name, boolean isChecked) {
        this.id = id;
        this.name = name;
        this.isChecked = isChecked;
    }

    @Override
    public String toString() {
        return "OptionItem{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", isChecked=" + isChecked +
                '}';
    }

    /**
     * 获取病情种类
     *
     * @param array json数据
     * @return list
     */
    public static List<OptionItem> getDiagnoseList(JSONArray array) {
        List<OptionItem> list = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            JSONObject object = array.optJSONObject(i);
            list.add(new OptionItem(object.optInt("ill_id"), object.optString("ill_name"), false));
        }
        return list;
    }

}
