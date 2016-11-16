package com.jstudio.widget.listview;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * A list view which can detect when scroll to the bottom.
 */
public class LoadMoreListView extends ListView {

    private boolean mIsFastScrollEnabled = false;
    private IndexScroller mScroller = null;
    private GestureDetector mGestureDetector = null;

    protected int mCurrentFirstVisibleItem;
    protected int mCurrentVisibleItemCount;
    protected int mCurrentScrollState;
    protected int mTotalItemCounts;
    protected boolean mIsLoading = false;
    private OnLoadMoreListener mListener;
    private boolean mEnableLoadMore = true;

    protected OnScrollListener mOnScrollListener = new OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            mCurrentScrollState = scrollState;
            isScrollCompleted();
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            mCurrentFirstVisibleItem = firstVisibleItem;
            mCurrentVisibleItemCount = visibleItemCount;
            mTotalItemCounts = totalItemCount;
        }
    };

    public LoadMoreListView(Context context) {
        super(context);
        init();
    }

    public LoadMoreListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LoadMoreListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    @Override
    public boolean isFastScrollEnabled() {
        return mIsFastScrollEnabled;
    }

    private void isScrollCompleted() {
        if (mCurrentVisibleItemCount > 0 &&
                mCurrentScrollState == OnScrollListener.SCROLL_STATE_IDLE &&
                (mCurrentFirstVisibleItem + mCurrentVisibleItemCount) >= mTotalItemCounts - 1) {
            if (!mIsLoading) {
                mIsLoading = true;
                onLoadMore();
            }
        }
    }

    @Override
    public void setFastScrollEnabled(boolean enabled) {
        mIsFastScrollEnabled = enabled;
        if (mIsFastScrollEnabled) {
            if (mScroller == null)
                mScroller = new IndexScroller(getContext(), this);
        } else {
            if (mScroller != null) {
                mScroller.hide();
                mScroller = null;
            }
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        // Overlay index bar
        if (mScroller != null)
            mScroller.draw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        // Intercept ListView's touch event
        if (mScroller != null && mScroller.onTouchEvent(ev))
            return true;

        if (mGestureDetector == null) {
            mGestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {

                @Override
                public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                    // If fling happens, index bar shows
                    if (mScroller != null)
                        mScroller.show();
                    return super.onFling(e1, e2, velocityX, velocityY);
                }
            });
        }
        mGestureDetector.onTouchEvent(ev);
        return super.onTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (mScroller != null && mScroller.contains(ev.getX(), ev.getY())) {
            return true;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(adapter);
        if (mScroller != null)
            mScroller.setAdapter(adapter);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (mScroller != null)
            mScroller.onSizeChanged(w, h, oldw, oldh);
    }

    private void init() {
        setOnScrollListener(mOnScrollListener);
    }

    public interface OnLoadMoreListener {
        void loadMore();
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.mListener = onLoadMoreListener;
    }

    public void enableLoadMore(boolean enable) {
        mEnableLoadMore = enable;
    }

    public void onLoadMore() {
        if (mListener != null && mEnableLoadMore) {
            mListener.loadMore();
        }
        mIsLoading = false;
    }
}
