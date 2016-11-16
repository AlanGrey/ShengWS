package com.jumook.syouhui.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.widget.TextView;

import com.jumook.syouhui.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 局部文字变色
 * Created by jumook on 2016/10/28.
 */

public class BrightTextView extends TextView {

    private Drawable drawableLeft;
    private Drawable wrappedDrawable;
    private Drawable drawableTop;
    private Drawable drawableRight;
    private Drawable drawableBottom;

    public BrightTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BrightTextView(Context context) {
        super(context);
        init();
    }

    private void init() {
        Drawable[] compoundDrawables = getCompoundDrawables();
        drawableLeft = compoundDrawables[0];//左
        drawableTop = compoundDrawables[1];//上
        drawableRight = compoundDrawables[2];//右
        drawableBottom = compoundDrawables[3];//下
        /*设置默认着色*/
        if (drawableLeft != null) {
            wrappedDrawable = DrawableCompat.wrap(drawableLeft);
            drawableLeft = wrappedDrawable;
            DrawableCompat.setTint(wrappedDrawable, getResources().getColor(R.color.tint_grey));
            setCompoundDrawables(drawableLeft, drawableTop, drawableRight, drawableBottom);
        }
    }

    /**
     * 默认着色为主题色
     *
     * @param color drawable 颜色值
     */
    public void setLeftIconColor(int color) {
        if (color == 0) {
            DrawableCompat.setTint(wrappedDrawable, getResources().getColor(R.color.tint_theme));
        } else {
            DrawableCompat.setTint(wrappedDrawable, getResources().getColor(color));
        }
        setCompoundDrawables(drawableLeft, drawableTop, drawableRight, drawableBottom);
    }

    public void setBrightTextsColor(String text, String specifiedTexts, int color) {
        List<Integer> sTextsStartList = new ArrayList<>();

        if (specifiedTexts != null && specifiedTexts.length() != 0) {
            int sTextLength = specifiedTexts.length();
            String temp = text;
            int lengthFront = 0;//记录被找出后前面的字段的长度
            int start = -1;
            do {
                start = temp.indexOf(specifiedTexts);
                if (start != -1) {
                    start = start + lengthFront;
                    sTextsStartList.add(start);
                    lengthFront = start + sTextLength;
                    temp = text.substring(lengthFront);
                }
            } while (start != -1);
            SpannableStringBuilder styledText = new SpannableStringBuilder(text);
            for (Integer i : sTextsStartList) {
                styledText.setSpan(new ForegroundColorSpan(color), i, i + sTextLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            setText(styledText);
        }
    }

}
