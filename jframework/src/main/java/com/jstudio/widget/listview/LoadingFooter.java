package com.jstudio.widget.listview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jstudio.R;

/**
 * Created by Jason
 */
public class LoadingFooter {

    private ProgressBar mProgress;
    private TextView mText;
    private View mFooterView;

    public static final int FOOTER_LOADING = 1;
    public static final int FOOTER_NO_MORE = 2;
    public static final int FOOTER_INVISIBLE = 3;

    private int mCurrentState = 0;

    public LoadingFooter(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mFooterView = inflater.inflate(R.layout.jfw_list_loading_footer, null);
        mProgress = (ProgressBar) mFooterView.findViewById(R.id.loading_progress);
        mText = (TextView) mFooterView.findViewById(R.id.loading_text);
    }

    public View getView() {
        return mFooterView;
    }

    private void show(boolean show) {
        if (mFooterView == null) {
            return;
        }
        if (show) {
            mFooterView.setVisibility(View.VISIBLE);
            mFooterView.setPadding(0, 0, 0, 0);
        } else {
            mFooterView.setVisibility(View.INVISIBLE);
            mFooterView.setPadding(0, -mFooterView.getHeight(), 0, 0);
        }
    }

    public void setFooterState(int state) {
        if (mCurrentState == state) {
            return;
        }
        mCurrentState = state;
        switch (state) {
            case FOOTER_LOADING:
                mProgress.setBackgroundResource(R.drawable.abc_btn_radio_to_on_mtrl_015);
                mText.setText("正在加载中...");
                show(true);
                break;

            case FOOTER_NO_MORE:
                mProgress.setBackgroundResource(0);
                mText.setText("没有更多内容...");
                show(true);
                break;

            case FOOTER_INVISIBLE:
                show(false);
                break;
        }
    }
}
