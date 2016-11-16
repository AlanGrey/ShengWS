package com.jstudio.widget.progressbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ProgressBar;

import com.jstudio.R;
import com.jstudio.utils.SizeUtils;

/**
 * Created by Jason
 */
public class TextProgressBar extends ProgressBar {

    private static final int DEFAULT_TEXT_SIZE = 10;//sp
    private static final int DEFAULT_TEXT_COLOR = 0xFFC00D1;
    private static final int DEFAULT_BEFORE_COLOR = DEFAULT_TEXT_COLOR;
    private static final int DEFAULT_AFTER_COLOR = 0xFFD3D6DA;
    private static final int DEFAULT_BEFORE_HEIGHT = 2;//dp
    private static final int DEFAULT_AFTER_HEIGHT = 2;//dp
    private static final int DEFAULT_TEXT_OFFSET = 10;//dp

    private int mTextSize = SizeUtils.convertSp2Px(getContext(), DEFAULT_TEXT_SIZE);
    private int mTextColor = DEFAULT_TEXT_COLOR;
    private int mBeforeColor = DEFAULT_BEFORE_COLOR;
    private int mBeforeHeight = SizeUtils.convertDp2Px(getContext(), DEFAULT_BEFORE_HEIGHT);
    private int mAfterColor = DEFAULT_AFTER_COLOR;
    private int mAfterHeight = SizeUtils.convertDp2Px(getContext(), DEFAULT_AFTER_HEIGHT);
    private int mTextOffset = SizeUtils.convertDp2Px(getContext(), DEFAULT_TEXT_OFFSET);

    private Paint mPaint = new Paint();
    private int mRealWidth;

    public TextProgressBar(Context context) {
        this(context, null);
    }

    public TextProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TextProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.TextProgressBar);
        mTextSize = (int) typedArray.getDimension(R.styleable.TextProgressBar_text_size, mTextSize);
        mTextColor = typedArray.getColor(R.styleable.TextProgressBar_text_color, mTextColor);
        mTextOffset = (int) typedArray.getDimension(R.styleable.TextProgressBar_text_offset, mTextOffset);
        mAfterColor = typedArray.getColor(R.styleable.TextProgressBar_after_color, mAfterColor);
        mBeforeColor = typedArray.getColor(R.styleable.TextProgressBar_before_color, mBeforeColor);
        mAfterHeight = (int) typedArray.getDimension(R.styleable.TextProgressBar_after_height, mAfterHeight);
        mBeforeHeight = (int) typedArray.getDimension(R.styleable.TextProgressBar_before_height, mBeforeHeight);

        typedArray.recycle();
        mPaint.setTextSize(mTextSize);
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int WidthValue = MeasureSpec.getSize(widthMeasureSpec);
        int height = measureHeight(heightMeasureSpec);
        setMeasuredDimension(WidthValue, height);

        mRealWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        canvas.save();
        canvas.translate(getPaddingLeft(), getHeight() / 2);
        boolean noNeedAfter = false;

        String text = getProgress() + "%";
        int textWidth = (int) mPaint.measureText(text);
        float radio = getProgress() * 1.0f / getMax();
        float progressX = radio * mRealWidth;
        if (progressX + textWidth > mRealWidth) {
            progressX = mRealWidth - textWidth;
            noNeedAfter = true;
        }

        float endX = -mTextOffset / 2;
        if (endX > 0) {
            mPaint.setColor(mBeforeColor);
            mPaint.setStrokeWidth(mBeforeHeight);
            canvas.drawLine(0, 0, endX, 0, mPaint);
        }

        mPaint.setColor(mTextColor);
        int y = (int) -(mPaint.descent() - mPaint.ascent() / 2);
        canvas.drawText(text, progressX, y, mPaint);

        if (!noNeedAfter) {
            float start = progressX + mTextOffset / 2 + textWidth;
            mPaint.setColor(mAfterColor);
            mPaint.setStrokeWidth(mAfterHeight);
            canvas.drawLine(start, 0, mRealWidth, 0, mPaint);
        }
    }

    private int measureHeight(int heightMeasureSpec) {
        int result = 0;
        int mode = MeasureSpec.getMode(heightMeasureSpec);
        int size = MeasureSpec.getSize(heightMeasureSpec);
        if (mode == MeasureSpec.EXACTLY) {
            result = size;
        } else {
            int textHeight = (int) (mPaint.descent() - mPaint.ascent());
            result = getPaddingTop() + getPaddingBottom() + Math.max(Math.max(mBeforeHeight, mAfterHeight), Math.abs(textHeight));
            if (mode == MeasureSpec.AT_MOST) {
                result = Math.min(result, size);
            }
        }
        return result;
    }
}
