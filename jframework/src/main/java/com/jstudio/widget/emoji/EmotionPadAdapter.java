package com.jstudio.widget.emoji;

import android.content.Context;
import android.net.Uri;

import com.facebook.drawee.view.SimpleDraweeView;
import com.jstudio.R;
import com.jstudio.adapter.list.CommonAdapter;
import com.jstudio.adapter.list.ViewHolder;

import java.util.List;

/**
 * Created by Jason
 */
public class EmotionPadAdapter extends CommonAdapter<Integer> {

    public EmotionPadAdapter(Context context, List<Integer> data) {
        super(context, data);
    }

    @Override
    public void inflateContent(ViewHolder holder, int position, Integer integer) {
        SimpleDraweeView imageView = holder.getView(R.id.emotion);
        imageView.setImageURI(Uri.parse("res:///" + mData.get(position)));
    }

    @Override
    public int setItemLayout(int type) {
        return R.layout.jfw_item_gv_emotion;
    }
}
