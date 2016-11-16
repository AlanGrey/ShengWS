package com.jumook.syouhui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.support.v7.widget.AppCompatSpinner;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.jstudio.widget.dialog.DialogCreator;
import com.jumook.syouhui.R;
import com.jumook.syouhui.dao.AreaDao;

/**
 * 地区选择弹出框
 * Created by jumook on 2016/11/14.
 */

abstract public class AreaSelectDialog {

    public static final String TAG = "AreaSelectDialog";

    private Activity activity;
    private Dialog dialog;
    private AppCompatSpinner areaProvince;
    private AppCompatSpinner areaCity;

    private String[] provinces = null;
    private int provincePosition = -1, cityPosition = -1;
    private AreaDao mAreaDao;

    public AreaSelectDialog(Activity activity) {
        this.activity = activity;
    }

    abstract public void selectAreaCallBack(String province, String city);

    public void init() {
        View areaView = LayoutInflater.from(activity).inflate(R.layout.dialog_area_select, null);
        areaProvince = (AppCompatSpinner) areaView.findViewById(R.id.area_province);
        areaCity = (AppCompatSpinner) areaView.findViewById(R.id.area_city);
        Button addressCancel = (Button) areaView.findViewById(R.id.address_cancel);
        Button addressConfirm = (Button) areaView.findViewById(R.id.address_confirm);
        dialog = DialogCreator.createNormalDialog(activity, areaView, DialogCreator.Position.BOTTOM);

        mAreaDao = new AreaDao(activity);
        initProvinceSpinner();
        String province = provinces[areaProvince.getSelectedItemPosition()];
        initCitySpinners(province);

        addressCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissDialog();
            }
        });
        addressConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectAreaCallBack(areaProvince.getSelectedItem().toString(), areaCity.getSelectedItem().toString());
                dismissDialog();
            }
        });

        areaProvince.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != provincePosition) {
                    provincePosition = position;
                    String province = provinces[provincePosition];
                    initCitySpinners(province);
                    cityPosition = areaCity.getSelectedItemPosition();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        areaCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                cityPosition = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void showDialog() {
        dialog.show();
    }

    public void dismissDialog() {
        dialog.dismiss();
    }


    private void initProvinceSpinner() {
        provinces = mAreaDao.getAreasProvince();//加载省份数据
        if (provinces == null) {
            return;
        }
        ArrayAdapter<String> provinceAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_item, provinces);
        provinceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        areaProvince.setAdapter(provinceAdapter);
    }

    private void initCitySpinners(String province) {
        String[] cities = mAreaDao.getAreasCity(province);
        if (cities == null) {
            return;
        }
        ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_item, cities);
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        areaCity.setAdapter(cityAdapter);
    }

}
