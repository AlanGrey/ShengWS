package com.jumook.syouhui.ui.step;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.widget.Button;
import android.widget.TextView;

import com.jstudio.utils.JLog;
import com.jumook.syouhui.R;
import com.jumook.syouhui.base.AppBaseActivity;

import butterknife.Bind;
import butterknife.OnClick;


/**
 * 应用程序的用户界面，
 * 主要功能就是按照XML布局文件的内容显示界面，
 * 并与用户进行交互
 * 负责前台界面展示
 * 在android中Activity负责前台界面展示，service负责后台的需要长期运行的任务。
 * Activity和Service之间的通信主要由Intent负责
 */
public class StepCounterActivity extends AppBaseActivity {

    private static final int MSG_GET_POSITION = 0;
    @Bind(R.id.step_count)
    TextView stepCount;
    @Bind(R.id.stay)
    Button stay;

    private int total_step = 0;   //走的总步数

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_GET_POSITION:
                    countStep(); //调用步数方法
                    handler.sendEmptyMessageDelayed(MSG_GET_POSITION, 1000);
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_step_counter);
    }

    @Override
    protected void initialization() {
        stepCount.setText("步数：0");
    }

    @Override
    protected void bindEvent() {

    }

    @OnClick(R.id.stay)
    public void onClick() {
        if (StepCounterService.FLAG || StepDetector.CURRENT_STEP > 0) {
            handler.sendEmptyMessage(MSG_GET_POSITION);
        } else {
            Intent service = new Intent(this, StepCounterService.class);
            startService(service);
            StepDetector.CURRENT_STEP = 0;
            handler.sendEmptyMessage(MSG_GET_POSITION);
        }
    }

    @Override
    protected void onDestroy() {
        handler.removeMessages(MSG_GET_POSITION);
        super.onDestroy();
    }

    /**
     * 实际的步数
     */
    private void countStep() {
        stepCount.setText(String.format("步数：%s", StepDetector.CURRENT_STEP));
        JLog.d("tag", "步数：" + StepDetector.CURRENT_STEP);
//        total_step = StepDetector.CURRENT_STEP;
    }


}
