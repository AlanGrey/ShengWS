package com.jumook.syouhui.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.jumook.syouhui.R;


/**
 * 左侧Item自动着色为主题色
 * Created by jumook on 2016/10/27.
 */

public class TintEditText extends AppCompatEditText implements View.OnFocusChangeListener, TextWatcher {


    private Drawable drawableLeft;
    private Drawable wrappedDrawable;
    private Drawable drawableTop;
    private Drawable drawableRight;
    private Drawable drawableBottom;
    private OnFocusChangeListener onFocusChangeListener;
    private OnTextChangeListener onTextChangeListener;

    private Drawable mClearDrawable;    //右边删除图片的Drawable资源
    private boolean hasFocus;            //是否获得焦点

    public TintEditText(Context context) {
        super(context);
        init();
    }

    public TintEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TintEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        /*我们都知道，TextView的子类都是可以设置Drawable的，当前咯，不此TextView,不知道自己去查资料*/
        setOnFocusChangeListener(this);
        Drawable[] compoundDrawables = getCompoundDrawables();
        drawableLeft = compoundDrawables[0];//左
        drawableTop = compoundDrawables[1];//上
        drawableRight = compoundDrawables[2];//右
        drawableBottom = compoundDrawables[3];//下
        //初始化清空按钮
        mClearDrawable = getResources().getDrawable(R.drawable.ic_edittext_clear);
        mClearDrawable.setBounds(0, 0, mClearDrawable.getIntrinsicWidth(), mClearDrawable.getIntrinsicHeight());
        //默认设置隐藏图标
        setClearIconVisible(false);
        //设置焦点改变的监听
        setOnFocusChangeListener(this);
        //设置输入框里面内容发生改变的监听
        addTextChangedListener(this);
        /*设置默认着色*/
        if (drawableLeft != null) {
            wrappedDrawable = DrawableCompat.wrap(drawableLeft);
            drawableLeft = wrappedDrawable;
            DrawableCompat.setTint(wrappedDrawable, getResources().getColor(R.color.tint_grey));
            setCompoundDrawables(drawableLeft, drawableTop, drawableRight, drawableBottom);
        }
    }

    /**
     * 因为我们不能直接给EditText设置点击事件，所以我们用记住我们按下的位置来模拟点击事件
     * 当我们按下的位置 在  EditText的宽度 - 图标到控件右边的间距 - 图标的宽度  和
     * EditText的宽度 - 图标到控件右边的间距之间我们就算点击了图标，竖直方向就没有考虑
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (getCompoundDrawables()[2] != null) {
                boolean touchable = event.getX() > (getWidth() - getTotalPaddingRight())
                        && (event.getX() < ((getWidth() - getPaddingRight())));
                if (touchable) {
                    this.setText("");
                }
            }
        }
        return super.onTouchEvent(event);
    }

    /**
     * 当ClearEditText焦点发生变化的时候，判断里面字符串长度设置清除图标的显示与隐藏
     */
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        //设置Left图标颜色变化
        if (hasFocus) {
            /*如果有焦点，就设置成当前文本的颜色，这里的颜色可以自己去修改，也可以自己自定义属性在布局里设置*/
            DrawableCompat.setTint(wrappedDrawable, getResources().getColor(R.color.tint_theme));
        } else {
            /*如果没有焦点，就设置成当前提示文本颜色*/
            DrawableCompat.setTint(wrappedDrawable, getResources().getColor(R.color.tint_grey));
        }
        setCompoundDrawables(drawableLeft, drawableTop, drawableRight, drawableBottom);
        onFocusChangeListener.onFocusChange(v, hasFocus);
        //设置Right清空状态
        this.hasFocus = hasFocus;
        if (hasFocus) {
            setClearIconVisible(getText().length() > 0);
        } else {
            setClearIconVisible(false);
        }
    }

    /**
     * 当输入框里面内容发生变化的时候回调的方法
     */
    @Override
    public void onTextChanged(CharSequence s, int start, int count, int after) {
        if (hasFocus) {
            setClearIconVisible(s.length() > 0);
            onTextChangeListener.onTextChanged(s);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    /**
     * 设置清除图标的显示与隐藏，调用setCompoundDrawables为EditText绘制上去
     *
     * @param visible boolean
     */
    protected void setClearIconVisible(boolean visible) {
        Drawable right = visible ? mClearDrawable : null;
        setCompoundDrawables(getCompoundDrawables()[0],
                getCompoundDrawables()[1], right, getCompoundDrawables()[3]);
    }

    public interface OnFocusChangeListener {
        void onFocusChange(View v, boolean hasFocus);
    }

    public void setOnFocusChangeListener(OnFocusChangeListener onFocusChangeListener) {
        this.onFocusChangeListener = onFocusChangeListener;
    }

    public void setOnTextChangeListener(OnTextChangeListener onTextChangeListener) {
        this.onTextChangeListener = onTextChangeListener;
    }

    public interface OnTextChangeListener {
        void onTextChanged(CharSequence s);
    }
}