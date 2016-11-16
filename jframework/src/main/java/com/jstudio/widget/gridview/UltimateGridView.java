package com.jstudio.widget.gridview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.jstudio.R;


/**
 * Created by Jason
 */
public class UltimateGridView extends FrameLayout {

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private HeaderGridView mGridView;

    private OnItemClickListener mListener;

    private View mEmptyView;
    private ImageView mEmptyImage;
    private TextView mEmptyText;

    public UltimateGridView(Context context) {
        this(context, null);
    }

    public UltimateGridView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UltimateGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs, defStyleAttr);
    }

    private void initView(Context context, AttributeSet attrs, int defStyleAttr) {
        int hSpacing = 0;
        int vSpacing = 0;
        int columnWidth = 0;
        int columnNum = 1;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.UltimateGridView, defStyleAttr, 0);
        int n = a.getIndexCount();

        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);
            if (attr == R.styleable.UltimateGridView_horizontalSpacing) {
                hSpacing = a.getDimensionPixelSize(attr, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, context.getResources().getDisplayMetrics()));
            }
            if (attr == R.styleable.UltimateGridView_verticalSpacing) {
                vSpacing = a.getDimensionPixelSize(attr, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, context.getResources().getDisplayMetrics()));
            }
            if (attr == R.styleable.UltimateGridView_columnWidth) {
                columnWidth = a.getDimensionPixelSize(attr, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, context.getResources().getDisplayMetrics()));
            }
            if (attr == R.styleable.UltimateGridView_columnNum) {
                columnNum = a.getInteger(R.styleable.UltimateGridView_columnNum, 1);
            }
        }
        //array should be release
        a.recycle();

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.jfw_gridview_layout, this);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.jfw_swipe_layout);
        mGridView = (HeaderGridView) view.findViewById(R.id.jfw_ultimate_grid_view);
        mSwipeRefreshLayout.setEnabled(false);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.holo_green_dark, R.color.holo_orange_dark, R.color.holo_red_light);

        mEmptyView = view.findViewById(R.id.empty_view);

        mEmptyImage = (ImageView) view.findViewById(R.id.empty_image);
        mEmptyText = (TextView) view.findViewById(R.id.empty_text);

        mGridView.setHorizontalSpacing(hSpacing);
        mGridView.setVerticalSpacing(vSpacing);
        mGridView.setColumnWidth(columnWidth);
        mGridView.setNumColumns(columnNum);
    }

    /**
     * 设置是否允许下拉刷新
     *
     * @param enable true为允许
     */
    public void setEnableRefreshing(boolean enable) {
        mSwipeRefreshLayout.setEnabled(enable);
    }

    /**
     * 获取当前GridView的SwipeRefreshLayout对象
     *
     * @return SwipeRefreshLayout
     */
    public SwipeRefreshLayout getRefreshLayout() {
        return mSwipeRefreshLayout;
    }

    /**
     * 相当于调用SwipeRefreshLayout的post方法
     *
     * @param runnable Runnable对象
     */
    public void postRefresh(Runnable runnable) {
        mSwipeRefreshLayout.post(runnable);
    }

    /**
     * 获取真实的GridView
     *
     * @return GridView对象
     */
    public HeaderGridView getRealGridView() {
        return mGridView;
    }

    /**
     * 为GridView设置Adapter
     *
     * @param adapter ListView的Adapter
     */
    public void setAdapter(ListAdapter adapter) {
        mGridView.setAdapter(adapter);
    }

    /**
     * 下拉刷新的监听
     *
     * @param listener SwipeRefreshLayout.OnRefreshListener对象
     */
    public void setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener listener) {
        mSwipeRefreshLayout.setEnabled(true);
        mSwipeRefreshLayout.setOnRefreshListener(listener);
    }

    /**
     * 设置加载圆圈是否显示
     *
     * @param isRefreshing true为正在刷新
     */
    public void setRefreshing(boolean isRefreshing) {
        mSwipeRefreshLayout.setRefreshing(isRefreshing);
    }

    /**
     * 设置Item点击事件
     *
     * @param listener 传入一个UltimateGridView.OnItemClickListener对象
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
        if (mListener == null) {
            return;
        }

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                mListener.onItemClick(adapterView, view, position, l);
            }
        });
    }

    /**
     * 为空白页面设置点击事件
     *
     * @param listener OnClickListener
     */
    public void setEmptyViewClickListener(OnClickListener listener) {
        mEmptyView.setOnClickListener(listener);
    }

    /**
     * 设置GridView到底监听
     *
     * @param listener HeaderGridView.OnLoadMoreListener
     */
    public void setOnLoadMoreListener(HeaderGridView.OnLoadMoreListener listener) {
        mGridView.setOnLoadMoreListener(listener);
    }

    /**
     * 获取ListView的Adapter
     *
     * @return ListView的Adapter
     */
    public ListAdapter getAdapter() {
        return mGridView.getAdapter();
    }

    /**
     * 为GridView设置Header
     *
     * @param view Header的View
     */
    public void addHeader(View view) {
        mGridView.addHeaderView(view);
    }

    /**
     * 为GridView设置Header
     *
     * @param view       Header的View
     * @param selectable 是否可以点击，也就是会不会出发OnItemClick事件
     */
    public void addHeader(View view, boolean selectable) {
        mGridView.addHeaderView(view, null, selectable);
    }

    /**
     * 显示空白页面内容
     *
     * @param imageResId 图片的资源id, 传0不显示图片
     * @param text       提示文字的内容
     */
    public void showEmptyView(int imageResId, String text) {
        mSwipeRefreshLayout.setVisibility(GONE);
        mEmptyView.setVisibility(VISIBLE);
        mEmptyImage.setVisibility(VISIBLE);
        mEmptyImage.setImageResource(imageResId);
        Drawable drawable = mEmptyImage.getDrawable();
        if (drawable instanceof AnimationDrawable) {
            ((AnimationDrawable) drawable).start();
        }
        mEmptyText.setText(text);
    }

    /**
     * 去除空白页，显示ListView
     */
    public void removeEmptyView() {
        mSwipeRefreshLayout.setVisibility(VISIBLE);
        mEmptyView.setVisibility(INVISIBLE);
        Drawable drawable = mEmptyImage.getDrawable();
        if (drawable != null && drawable instanceof AnimationDrawable) {
            ((AnimationDrawable) drawable).stop();
        }
        mEmptyImage.setImageResource(0);
    }

    public interface OnItemClickListener {
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id);
    }

}
