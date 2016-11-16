package com.jstudio.widget.listview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Animatable;
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
public class UltimateListView extends FrameLayout {

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private LoadMoreListView mListView;
    private LoadingFooter mFooter;

    private OnItemClickListener mListener;

    private View mEmptyView;
    private ImageView mEmptyImage;
    private TextView mEmptyText;

    public UltimateListView(Context context) {
        this(context, null);
    }

    public UltimateListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UltimateListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs, defStyleAttr);
    }

    private void initView(Context context, AttributeSet attrs, int defStyleAttr) {
        int dividerHeight = 1;
        int dividerDrawable = 0;
        boolean hasLoadingFooter = false;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.UltimateListView, defStyleAttr, 0);
        int n = a.getIndexCount();

        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);
            if (attr == R.styleable.UltimateListView_dividerHeight) {
                dividerHeight = a.getDimensionPixelSize(attr,
                        (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, context.getResources().getDisplayMetrics()));
            }
            if (attr == R.styleable.UltimateListView_dividerDrawable) {
                dividerDrawable = a.getResourceId(R.styleable.UltimateListView_dividerDrawable, R.drawable.divider_drawable);
            }
            if (attr == R.styleable.UltimateListView_hasLoadingFooter) {
                hasLoadingFooter = a.getBoolean(R.styleable.UltimateListView_hasLoadingFooter, false);
            }
        }
        //array should be release
        a.recycle();

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.jfw_listview_layout, this);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.jfw_swipe_layout);
        mListView = (LoadMoreListView) view.findViewById(R.id.ultimate_list_view);
        mSwipeRefreshLayout.setEnabled(false);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.holo_green_dark, R.color.holo_orange_dark, R.color.holo_red_light);

        mEmptyView = view.findViewById(R.id.empty_view);

        mEmptyImage = (ImageView) view.findViewById(R.id.empty_image);
        mEmptyText = (TextView) view.findViewById(R.id.empty_text);
        if (dividerDrawable != 0) {
            mListView.setDivider(getResources().getDrawable(dividerDrawable));
        }
        mListView.setDividerHeight(dividerHeight);
        if (hasLoadingFooter) {
            mFooter = new LoadingFooter(context);
            mListView.addFooterView(mFooter.getView());
            setFooterState(LoadingFooter.FOOTER_INVISIBLE);
        }
    }

    /**
     * 设置是否允许下拉刷新
     *
     * @param enable true为允许
     */
    public void enableRefreshing(boolean enable) {
        mSwipeRefreshLayout.setEnabled(enable);
    }

    /**
     * 设置是否允许到底加载更多
     *
     * @param enable true为允许
     */
    public void enableLoadMore(boolean enable) {
        mListView.enableLoadMore(enable);
    }

    /**
     * 获取当前ListView的SwipeRefreshLayout对象
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
     * 获取真实的ListView
     *
     * @return ListView对象
     */
    public LoadMoreListView getRealListView() {
        return mListView;
    }

    /**
     * 为ListView设置Adapter
     *
     * @param adapter ListView的Adapter
     */
    public void setAdapter(ListAdapter adapter) {
        mListView.setAdapter(adapter);
    }

    /**
     * 下拉刷新的监听
     *
     * @param listener SwipeRefreshLayout.OnRefreshListener对象
     */
    public void setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener listener) {
        mSwipeRefreshLayout.setEnabled(true);
        mSwipeRefreshLayout.setOnRefreshListener(listener);
        setFooterState(LoadingFooter.FOOTER_INVISIBLE);
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
     * @param listener 传入一个UltimateListView.OnItemClickListener对象
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
        final int headerViewCount = mListView.getHeaderViewsCount();

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                int size = mListView.getAdapter().getCount();
                if (position < headerViewCount || position >= size + headerViewCount) {
                    return;
                } else {
                    mListener.onItemClick(adapterView, view, position - headerViewCount, l);
                }
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
     * 设置ListView到底监听
     *
     * @param listener LoadMoreListView.OnLoadMoreListener
     */
    public void setOnLoadMoreListener(LoadMoreListView.OnLoadMoreListener listener) {
        mListView.setOnLoadMoreListener(listener);
    }

    /**
     * 获取ListView的Adapter
     *
     * @return ListView的Adapter
     */
    public ListAdapter getAdapter() {
        return mListView.getAdapter();
    }

    /**
     * 为ListView设置Header
     *
     * @param view Header的View
     */
    public void addHeader(View view) {
        mListView.addHeaderView(view);
    }

    /**
     * 为ListView设置Header
     *
     * @param view       Header的View
     * @param selectable 是否可以点击，也就是会不会出发OnItemClick事件
     */
    public void addHeader(View view, boolean selectable) {
        mListView.addHeaderView(view, null, selectable);
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
        if (drawable instanceof Animatable) {
            ((Animatable) drawable).start();
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
        if (drawable != null && drawable instanceof Animatable) {
            ((Animatable) drawable).stop();
        }
        mEmptyImage.setImageResource(0);
    }

    /**
     * 设置LoadingFooter的状态
     *
     * @param state FOOTER_INVISIBLE，FOOTER_LOADING，FOOTER_NO_MORE三者之一
     */
    public void setFooterState(int state) {
        if (mListView.getFooterViewsCount() == 0) {
            return;
        }
        mFooter.setFooterState(state);
    }

    public interface OnItemClickListener {
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id);
    }

}
