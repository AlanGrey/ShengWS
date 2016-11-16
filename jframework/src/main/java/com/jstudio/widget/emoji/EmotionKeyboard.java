package com.jstudio.widget.emoji;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.jstudio.R;

/**
 * Created by Jason on 2/20/16.
 */
public class EmotionKeyboard {

    private View mPopupView;
    private PopupWindow mPopupWindow;

    private LinearLayout mParentLayout;
    private LinearLayout mEmotionCover;

    private int mKeyboardHeight;
    private int mPreviousHeightDiff = 0;
    private boolean mIsKeyboardVisible;

    private OnItemClickListener mItemClickListener;
    private OnStateChangedListener mStateListener;

    public EmotionKeyboard(Activity activity, LinearLayout parentLayout, LinearLayout emotionCover) {
        mParentLayout = parentLayout;
        mEmotionCover = emotionCover;
        init(activity);
    }

    private void init(final Activity activity) {
        final float popupHeight = activity.getResources().getDimension(R.dimen.keyboard_height);
        changeKeyboardHeight((int) popupHeight);

        mPopupView = activity.getLayoutInflater().inflate(R.layout.jfw_view_emotion_popup, null);
        GridView gridView = (GridView) mPopupView.findViewById(R.id.emotion_keypad_grid_view);
        gridView.setAdapter(new EmotionPadAdapter(activity, EmotionParser.getAllDrawableID()));
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mItemClickListener != null) {
                    String emotionName = EmotionParser.getEmotionPhrase(position);
                    int emotionID = EmotionParser.getDrawableID(position);
                    SpannableString spannableString = new SpannableString(emotionName);
                    Bitmap bitmap = BitmapFactory.decodeResource(activity.getResources(), emotionID);
                    ImageSpan imageSpan = new ImageSpan(activity, bitmap);
                    spannableString.setSpan(imageSpan, 0, emotionName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    mItemClickListener.onClick(spannableString);
                }
            }
        });
        mPopupWindow = new PopupWindow(mPopupView, ViewGroup.LayoutParams.MATCH_PARENT, mKeyboardHeight, false);
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                mEmotionCover.setVisibility(LinearLayout.GONE);
                if (mStateListener != null) {
                    mStateListener.onStateChanged(View.INVISIBLE);
                }
            }
        });
        checkKeyboardHeight();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mItemClickListener = listener;
    }

    public void setOnStateChangedListener(OnStateChangedListener listener) {
        this.mStateListener = listener;
    }

    /**
     * 显示表情键盘
     */
    public void show() {
        if (!isEmotionShowing()) {
            mPopupWindow.setHeight(mKeyboardHeight);
            if (mIsKeyboardVisible) {
                mEmotionCover.setVisibility(LinearLayout.GONE);
            } else {
                mEmotionCover.setVisibility(LinearLayout.VISIBLE);
            }
            mPopupWindow.showAtLocation(mParentLayout, Gravity.BOTTOM, 0, 0);
            if (mStateListener != null) {
                mStateListener.onStateChanged(View.VISIBLE);
            }
        }
    }

    /**
     * 收起表情键盘
     */
    public void dismiss() {
        if (mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
            if (mStateListener != null) {
                mStateListener.onStateChanged(View.INVISIBLE);
            }
        }
    }

    /**
     * 表情键盘是否正在显示
     *
     * @return true表示正在显示否则返回false
     */
    public boolean isEmotionShowing() {
        return mPopupWindow.isShowing();
    }

    private void checkKeyboardHeight() {
        mParentLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                mParentLayout.getWindowVisibleDisplayFrame(r);
                int screenHeight = mParentLayout.getRootView().getHeight();
                int heightDifference = screenHeight - (r.bottom);
                if (mPreviousHeightDiff - heightDifference > 50) {
                    mPopupWindow.dismiss();
                }
                mPreviousHeightDiff = heightDifference;
                if (heightDifference > 100) {
                    mIsKeyboardVisible = true;
                    changeKeyboardHeight(heightDifference);
                } else {
                    mIsKeyboardVisible = false;
                }
            }
        });
    }

    private void changeKeyboardHeight(int height) {
        if (height > 100) {
            mKeyboardHeight = height;
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mKeyboardHeight);
            mEmotionCover.setLayoutParams(params);
        }
    }

    public interface OnItemClickListener {
        void onClick(Spanned spanned);
    }

    public interface OnStateChangedListener {
        void onStateChanged(int state);
    }

}
