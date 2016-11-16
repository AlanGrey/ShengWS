package com.jumook.syouhui.ui.other;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.jstudio.adapter.pager.SimpleViewPagerAdapter;
import com.jumook.syouhui.R;
import com.jumook.syouhui.base.AppBaseActivity;
import com.jumook.syouhui.ui.MainActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * 引导页
 * Created by jumook on 2016/10/25.
 */

public class GuidePageActivity extends AppBaseActivity {

    public static final String TAG = "GuidePageActivity";

    @Bind(R.id.app_guide_viewpager)
    ViewPager mGuidePager;
    @Bind(R.id.app_guide_start)
    Button mGuideStart;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_guide_page);
    }

    @Override
    protected void initialization() {
        List<Integer> pagerImageRes = new ArrayList<>();
        pagerImageRes.add(R.drawable.guide_page_a);
        pagerImageRes.add(R.drawable.guide_page_b);
        pagerImageRes.add(R.drawable.guide_page_c);
        SimpleViewPagerAdapter<Integer> mPagerAdapter = new SimpleViewPagerAdapter<Integer>(this, pagerImageRes) {
            @Override
            public List<View> inflateContent(LayoutInflater inflater, List<Integer> data) {
                List<View> views = new ArrayList<>();
                for (int i = 0; i < data.size(); i++) {
                    View pageView = inflater.inflate(R.layout.item_pager_guide, null, false);
                    ImageView imageView = (ImageView) pageView.findViewById(R.id.guide_item_image);
                    imageView.setImageResource(data.get(i));
                    views.add(pageView);
                }
                return views;
            }
        };
        mGuidePager.setAdapter(mPagerAdapter);
    }

    @Override
    protected void bindEvent() {
        mGuidePager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 2) {
                    mGuideStart.setVisibility(View.VISIBLE);
                } else {
                    mGuideStart.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        mGuideStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toHomeActivity = new Intent(GuidePageActivity.this, MainActivity.class);
                startActivity(toHomeActivity);
                finish();
            }
        });
    }

    @Override
    protected void doMoreInOnCreate() {
    }

}
