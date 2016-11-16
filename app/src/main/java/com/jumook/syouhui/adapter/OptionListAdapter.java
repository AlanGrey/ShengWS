package com.jumook.syouhui.adapter;

import android.content.Context;
import android.widget.CheckBox;

import com.jstudio.adapter.list.CommonAdapter;
import com.jstudio.adapter.list.ViewHolder;
import com.jumook.syouhui.R;
import com.jumook.syouhui.bean.OptionItem;

import java.util.List;

/**
 * 选择Dialog 适配器
 * Created by Administrator on 2015-09-24.
 */
public class OptionListAdapter extends CommonAdapter<OptionItem> {

    public OptionListAdapter(Context context, List<OptionItem> data) {
        super(context, data);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void inflateContent(ViewHolder holder, int position, OptionItem item) {
        holder.setTextByString(R.id.item_name, item.name);
        CheckBox radioButton = holder.getView(R.id.item_checkbox);
        radioButton.setChecked(item.isChecked);
    }

    @Override
    public int setItemLayout(int type) {
        return R.layout.item_lv_option_select;
    }
}
