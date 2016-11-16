/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jstudio.widget.gridview;

import android.content.Context;
import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.WrapperListAdapter;

import java.util.ArrayList;

public class HeaderGridView extends GridView {

    protected int currentFirstVisibleItem;
    protected int currentVisibleItemCount;
    protected int currentScrollState;
    protected int totalItemCounts;
    protected boolean isLoading = false;
    private OnLoadMoreListener onLoadMoreListener;

    private static class FixedViewInfo {
        public View view;
        public ViewGroup viewContainer;
        public Object data;
        public boolean isSelectable;
    }

    protected OnScrollListener loadMoreOnScrollListener = new OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            currentScrollState = scrollState;
            isScrollCompleted();
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            currentFirstVisibleItem = firstVisibleItem;
            currentVisibleItemCount = visibleItemCount;
            totalItemCounts = totalItemCount;
        }
    };

    private ArrayList<FixedViewInfo> mHeaderViewInfos = new ArrayList<FixedViewInfo>();

    private void initHeaderGridView() {
        super.setClipChildren(false);
        setOnScrollListener(loadMoreOnScrollListener);
    }

    public HeaderGridView(Context context) {
        super(context);
        initHeaderGridView();
    }

    public HeaderGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initHeaderGridView();
    }

    public HeaderGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initHeaderGridView();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        ListAdapter adapter = getAdapter();
        if (adapter != null && adapter instanceof HeaderViewGridAdapter) {
            ((HeaderViewGridAdapter) adapter).setNumColumns(getNumColumns());
        }
    }

    private void isScrollCompleted() {
        if (this.currentVisibleItemCount > 0 &&
                this.currentScrollState == OnScrollListener.SCROLL_STATE_IDLE &&
                (currentFirstVisibleItem + currentVisibleItemCount) >= totalItemCounts - 1) {
            if (!isLoading) {
                isLoading = true;
                onLoadMore();
            }
        }
    }

    public interface OnLoadMoreListener {
        void loadMore();
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public void onLoadMore() {
        if (onLoadMoreListener != null) {
            onLoadMoreListener.loadMore();
        }
        isLoading = false;
    }

    @Override
    public void setClipChildren(boolean clipChildren) {
    }

    public void addHeaderView(View v, Object data, boolean isSelectable) {
        ListAdapter adapter = getAdapter();
        if (adapter != null && !(adapter instanceof HeaderViewGridAdapter)) {
            throw new IllegalStateException(
                    "Cannot add header view to grid -- setAdapter has already been called.");
        }
        FixedViewInfo info = new FixedViewInfo();
        FrameLayout fl = new FullWidthFixedViewLayout(getContext());
        fl.addView(v);
        info.view = v;
        info.viewContainer = fl;
        info.data = data;
        info.isSelectable = isSelectable;
        mHeaderViewInfos.add(info);
        // in the case of re-adding a header view, or adding one later on,
        // we need to notify the observer
        if (adapter != null) {
            ((HeaderViewGridAdapter) adapter).notifyDataSetChanged();
        }
    }

    public void addHeaderView(View v) {
        addHeaderView(v, null, true);
    }

    public int getHeaderViewCount() {
        return mHeaderViewInfos.size();
    }

    public boolean removeHeaderView(View v) {
        if (mHeaderViewInfos.size() > 0) {
            boolean result = false;
            ListAdapter adapter = getAdapter();
            if (adapter != null && ((HeaderViewGridAdapter) adapter).removeHeader(v)) {
                result = true;
            }
            removeFixedViewInfo(v, mHeaderViewInfos);
            return result;
        }
        return false;
    }

    private void removeFixedViewInfo(View v, ArrayList<FixedViewInfo> where) {
        int len = where.size();
        for (int i = 0; i < len; ++i) {
            FixedViewInfo info = where.get(i);
            if (info.view == v) {
                where.remove(i);
                break;
            }
        }
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        if (mHeaderViewInfos.size() > 0) {
            HeaderViewGridAdapter hadapter = new HeaderViewGridAdapter(mHeaderViewInfos, adapter);
            int numColumns = getNumColumns();
            if (numColumns > 1) {
                hadapter.setNumColumns(numColumns);
            }
            super.setAdapter(hadapter);
        } else {
            super.setAdapter(adapter);
        }
    }

    private class FullWidthFixedViewLayout extends FrameLayout {
        public FullWidthFixedViewLayout(Context context) {
            super(context);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int targetWidth = HeaderGridView.this.getMeasuredWidth()
                    - HeaderGridView.this.getPaddingLeft()
                    - HeaderGridView.this.getPaddingRight();
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(targetWidth,
                    MeasureSpec.getMode(widthMeasureSpec));
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    private static class HeaderViewGridAdapter implements WrapperListAdapter, Filterable {
        private final DataSetObservable mDataSetObservable = new DataSetObservable();
        private final ListAdapter mAdapter;
        private int mNumColumns = 1;
        ArrayList<FixedViewInfo> mHeaderViewInfos;
        boolean mAreAllFixedViewsSelectable;
        private final boolean mIsFilterable;

        public HeaderViewGridAdapter(ArrayList<FixedViewInfo> headerViewInfos, ListAdapter adapter) {
            mAdapter = adapter;
            mIsFilterable = adapter instanceof Filterable;
            if (headerViewInfos == null) {
                throw new IllegalArgumentException("headerViewInfos cannot be null");
            }
            mHeaderViewInfos = headerViewInfos;
            mAreAllFixedViewsSelectable = areAllListInfosSelectable(mHeaderViewInfos);
        }

        public int getHeadersCount() {
            return mHeaderViewInfos.size();
        }

        @Override
        public boolean isEmpty() {
            return (mAdapter == null || mAdapter.isEmpty()) && getHeadersCount() == 0;
        }

        public void setNumColumns(int numColumns) {
            if (numColumns < 1) {
                throw new IllegalArgumentException("Number of columns must be 1 or more");
            }
            if (mNumColumns != numColumns) {
                mNumColumns = numColumns;
                notifyDataSetChanged();
            }
        }

        private boolean areAllListInfosSelectable(ArrayList<FixedViewInfo> infos) {
            if (infos != null) {
                for (FixedViewInfo info : infos) {
                    if (!info.isSelectable) {
                        return false;
                    }
                }
            }
            return true;
        }

        public boolean removeHeader(View v) {
            for (int i = 0; i < mHeaderViewInfos.size(); i++) {
                FixedViewInfo info = mHeaderViewInfos.get(i);
                if (info.view == v) {
                    mHeaderViewInfos.remove(i);
                    mAreAllFixedViewsSelectable = areAllListInfosSelectable(mHeaderViewInfos);
                    mDataSetObservable.notifyChanged();
                    return true;
                }
            }
            return false;
        }

        @Override
        public int getCount() {
            if (mAdapter != null) {
                return getHeadersCount() * mNumColumns + mAdapter.getCount();
            } else {
                return getHeadersCount() * mNumColumns;
            }
        }

        @Override
        public boolean areAllItemsEnabled() {
            if (mAdapter != null) {
                return mAreAllFixedViewsSelectable && mAdapter.areAllItemsEnabled();
            } else {
                return true;
            }
        }

        @Override
        public boolean isEnabled(int position) {
            int numHeadersAndPlaceholders = getHeadersCount() * mNumColumns;
            if (position < numHeadersAndPlaceholders) {
                return (position % mNumColumns == 0)
                        && mHeaderViewInfos.get(position / mNumColumns).isSelectable;
            }
            final int adjPosition = position - numHeadersAndPlaceholders;
            int adapterCount = 0;
            if (mAdapter != null) {
                adapterCount = mAdapter.getCount();
                if (adjPosition < adapterCount) {
                    return mAdapter.isEnabled(adjPosition);
                }
            }
            throw new ArrayIndexOutOfBoundsException(position);
        }

        @Override
        public Object getItem(int position) {
            int numHeadersAndPlaceholders = getHeadersCount() * mNumColumns;
            if (position < numHeadersAndPlaceholders) {
                if (position % mNumColumns == 0) {
                    return mHeaderViewInfos.get(position / mNumColumns).data;
                }
                return null;
            }
            final int adjPosition = position - numHeadersAndPlaceholders;
            int adapterCount = 0;
            if (mAdapter != null) {
                adapterCount = mAdapter.getCount();
                if (adjPosition < adapterCount) {
                    return mAdapter.getItem(adjPosition);
                }
            }
            throw new ArrayIndexOutOfBoundsException(position);
        }

        @Override
        public long getItemId(int position) {
            int numHeadersAndPlaceholders = getHeadersCount() * mNumColumns;
            if (mAdapter != null && position >= numHeadersAndPlaceholders) {
                int adjPosition = position - numHeadersAndPlaceholders;
                int adapterCount = mAdapter.getCount();
                if (adjPosition < adapterCount) {
                    return mAdapter.getItemId(adjPosition);
                }
            }
            return -1;
        }

        @Override
        public boolean hasStableIds() {
            if (mAdapter != null) {
                return mAdapter.hasStableIds();
            }
            return false;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            int numHeadersAndPlaceholders = getHeadersCount() * mNumColumns;
            if (position < numHeadersAndPlaceholders) {
                View headerViewContainer = mHeaderViewInfos
                        .get(position / mNumColumns).viewContainer;
                if (position % mNumColumns == 0) {
                    return headerViewContainer;
                } else {
                    if (convertView == null) {
                        convertView = new View(parent.getContext());
                    }
                    convertView.setVisibility(View.INVISIBLE);
                    convertView.setMinimumHeight(headerViewContainer.getHeight());
                    return convertView;
                }
            }
            final int adjPosition = position - numHeadersAndPlaceholders;
            int adapterCount = 0;
            if (mAdapter != null) {
                adapterCount = mAdapter.getCount();
                if (adjPosition < adapterCount) {
                    return mAdapter.getView(adjPosition, convertView, parent);
                }
            }
            throw new ArrayIndexOutOfBoundsException(position);
        }

        @Override
        public int getItemViewType(int position) {
            int numHeadersAndPlaceholders = getHeadersCount() * mNumColumns;
            if (position < numHeadersAndPlaceholders && (position % mNumColumns != 0)) {
                return mAdapter != null ? mAdapter.getViewTypeCount() : 1;
            }
            if (mAdapter != null && position >= numHeadersAndPlaceholders) {
                int adjPosition = position - numHeadersAndPlaceholders;
                int adapterCount = mAdapter.getCount();
                if (adjPosition < adapterCount) {
                    return mAdapter.getItemViewType(adjPosition);
                }
            }
            return AdapterView.ITEM_VIEW_TYPE_HEADER_OR_FOOTER;
        }

        @Override
        public int getViewTypeCount() {
            if (mAdapter != null) {
                return mAdapter.getViewTypeCount() + 1;
            }
            return 2;
        }

        @Override
        public void registerDataSetObserver(DataSetObserver observer) {
            mDataSetObservable.registerObserver(observer);
            if (mAdapter != null) {
                mAdapter.registerDataSetObserver(observer);
            }
        }

        @Override
        public void unregisterDataSetObserver(DataSetObserver observer) {
            mDataSetObservable.unregisterObserver(observer);
            if (mAdapter != null) {
                mAdapter.unregisterDataSetObserver(observer);
            }
        }

        @Override
        public Filter getFilter() {
            if (mIsFilterable) {
                return ((Filterable) mAdapter).getFilter();
            }
            return null;
        }

        @Override
        public ListAdapter getWrappedAdapter() {
            return mAdapter;
        }

        public void notifyDataSetChanged() {
            mDataSetObservable.notifyChanged();
        }
    }
}