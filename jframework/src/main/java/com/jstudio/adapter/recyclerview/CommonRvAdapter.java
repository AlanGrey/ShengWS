package com.jstudio.adapter.recyclerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jstudio.R;

import java.util.List;

/**
 * Created by Jason
 * 使用方法见CommonAdapter
 */
public abstract class CommonRvAdapter<T> extends RecyclerView.Adapter<RvViewHolder> implements View.OnClickListener {

    public static String TAG = null;

    protected Context mContext;
    protected List<T> mData;
    protected int mBackground;
    protected final TypedValue mTypedValue = new TypedValue();
    private OnRecyclerViewItemClickListener mOnItemClickListener;

    public CommonRvAdapter(Context context, List<T> data) {
        TAG = this.getClass().getSimpleName();
        this.mContext = context;
        this.mData = data;
        this.mContext.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
        this.mBackground = mTypedValue.resourceId;
    }

    public void setData(List<T> data) {
        if (data != null) {
            this.mData = data;
        }
        this.notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public List<T> getData() {
        return mData;
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    public T getItem(int position) {
        return mData.get(position);
    }

//    @Override
//    public int getItemViewType(int position) {
//        if (position % 5 == 1) {
//            return 0;
//        } else {
//            return 1;
//        }
//    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public RvViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(setItemLayout(viewType), parent, false);
        itemView.setBackgroundResource(mBackground);
        itemView.setOnClickListener(this);
        return new RvViewHolder(itemView);
    }

    public abstract int setItemLayout(int type);

    public abstract void inflateContent(RvViewHolder holder, int position, T t);

    @Override
    public void onBindViewHolder(RvViewHolder holder, int position) {
        inflateContent(holder, position, getItem(position));
        holder.getRootView().setTag(position);
    }

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int position, long id);
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            int position = (int) v.getTag();
            mOnItemClickListener.onItemClick(v, position, getItemId(position));
        }
    }
}
