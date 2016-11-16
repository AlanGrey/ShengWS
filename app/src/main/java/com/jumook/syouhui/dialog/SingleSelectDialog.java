package com.jumook.syouhui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.jstudio.widget.dialog.DialogCreator;
import com.jumook.syouhui.R;
import com.jumook.syouhui.adapter.OptionListAdapter;
import com.jumook.syouhui.bean.OptionItem;

import java.util.ArrayList;
import java.util.List;

/**
 * （单选）选择弹出框
 * Created by jumook on 2016/11/14.
 */

abstract public class SingleSelectDialog {


    private Activity activity;

    private Dialog dialog;
    private ListView singleList;

    private List<OptionItem> list;
    private OptionListAdapter adapter;

    public SingleSelectDialog(Activity activity) {
        this.activity = activity;
    }

    abstract public void singleSelectCallBack(int position, OptionItem value);

    public void init(String title) {
        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_single_select, null);
        TextView singleTitle = (TextView) view.findViewById(R.id.single_title);
        singleList = (ListView) view.findViewById(R.id.single_list);
        Button singleCancel = (Button) view.findViewById(R.id.single_cancel);
        dialog = DialogCreator.createNormalDialog(activity, view, DialogCreator.Position.CENTER);
        singleTitle.setText(title);

        list = new ArrayList<>();
        adapter = new OptionListAdapter(activity, list);
        singleList.setAdapter(adapter);

        singleCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismissDialog();
            }
        });

        singleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                singleSelectCallBack(i, list.get(i));
                dismissDialog();
            }
        });
    }

    public void showDialog(List<OptionItem> list) {
        this.list = list;
        adapter.setData(list);
        dialog.show();
    }

    public void dismissDialog() {
        dialog.dismiss();
    }


}
