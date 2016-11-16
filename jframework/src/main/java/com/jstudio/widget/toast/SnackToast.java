package com.jstudio.widget.toast;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jstudio.R;

public class SnackToast {

    private Toast mToast;
    private TextView mTextView;
    private RelativeLayout mRelativeLayout;

    public SnackToast(Context context) {
        mToast = new Toast(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.jfw_view_toast_layout, null);
        mTextView = (TextView) view.findViewById(R.id.text);
        mRelativeLayout = (RelativeLayout) view.findViewById(R.id.toast_container);
        mToast.setView(view);
    }

    public Toast getToast() {
        return this.mToast;
    }

    public SnackToast setTextColor(int color) {
        this.mTextView.setTextColor(color);
        return this;
    }

    public SnackToast setBackgroundColor(int color) {
        this.mRelativeLayout.setBackgroundColor(color);
        return this;
    }

    public SnackToast setText(String text) {
        this.mTextView.setText(text);
        return this;
    }

    public SnackToast setTextSize(float size) {
        this.mTextView.setTextSize(size);
        return this;
    }

    public SnackToast setDrawableLeft(int resId) {
        Drawable drawable = mTextView.getContext().getResources().getDrawable(resId);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        this.mTextView.setCompoundDrawables(drawable, null, null, null);
        return this;
    }

    public SnackToast setDuration(int duration) {
        this.mToast.setDuration(duration);
        return this;
    }


    public SnackToast setText(int resId) {
        this.mTextView.setText(resId);
        return this;
    }

    /**
     * Set the gravity of the SnackToast
     *
     * @param gravity One of Gravity.TOP or Gravity.BOTTOM
     * @return The instance of this class
     */
    public SnackToast setGravity(int gravity) {
        int position = Gravity.FILL_HORIZONTAL | gravity;
        this.mToast.setGravity(position, 0, 0);
        return this;
    }

    public void show() {
        getToast().show();
    }

    /**
     * Construct a full width toast
     *
     * @param context  Context
     * @param text     The string text you want to show
     * @param duration Length to show the view
     * @param drawable The drawable icon, nullable
     * @return The origin toast object
     */
    public static Toast makeToast(Context context, String text, int duration, @Nullable Drawable drawable) {
        Toast toast = new Toast(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View toast_view = inflater.inflate(R.layout.jfw_view_toast_layout, null);
        TextView textView = (TextView) toast_view.findViewById(R.id.text);
        textView.setText(text);
        if (drawable != null) {
            ImageView imageView = (ImageView) toast_view.findViewById(R.id.icon);
            imageView.setImageDrawable(drawable);
        }
        toast.setView(toast_view);
        toast.setDuration(duration);
        toast.setGravity(Gravity.BOTTOM | Gravity.FILL_HORIZONTAL, 0, 0);
        return toast;
    }

    /**
     * Construct a full width toast
     *
     * @param context  Context
     * @param resId    The resource string you want to show
     * @param duration Length to show the view
     * @param drawable The drawable icon, nullable
     * @return The origin toast object
     */
    public static Toast makeToast(Context context, int resId, int duration, @Nullable Drawable drawable) {
        Toast toast = new Toast(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View toast_view = inflater.inflate(R.layout.jfw_view_toast_layout, null);
        TextView textView = (TextView) toast_view.findViewById(R.id.text);
        textView.setText(resId);
        if (drawable != null) {
            ImageView imageView = (ImageView) toast_view.findViewById(R.id.icon);
            imageView.setImageDrawable(drawable);
        }
        toast.setView(toast_view);
        toast.setDuration(duration);
        toast.setGravity(Gravity.BOTTOM | Gravity.FILL_HORIZONTAL, 0, 0);
        return toast;
    }
}
