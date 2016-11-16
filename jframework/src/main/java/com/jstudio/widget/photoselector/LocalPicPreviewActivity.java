package com.jstudio.widget.photoselector;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.jstudio.R;
import com.jstudio.base.BaseAppCompatActivity;
import com.jstudio.ui.WindowHelper;
import com.jstudio.widget.pager.HackyViewPager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jason
 */
public class LocalPicPreviewActivity extends BaseAppCompatActivity {

    public static final String URL_KEY = "url";

    private HackyViewPager mViewPager;

    @Override
    protected boolean onRestoreState(Bundle paramSavedState) {
        return false;
    }

    @Override
    protected void setActivityTheme() {
    }

    @Override
    protected void setContentView() {
        setContentView(R.layout.jfw_aty_photo_review);
        WindowHelper.setFullScreen(this, true);
    }

    @Override
    protected void findViews() {
        mViewPager = (HackyViewPager) findViewById(R.id.photo_view_pager);
    }

    @Override
    protected void initialization() {
        Intent intent = getIntent();
        ArrayList<Uri> mImages = intent.getParcelableArrayListExtra(AlbumFragment.SELECTED_PIC);
        if (mImages == null) {
            return;
        }
        int mPosition = intent.getIntExtra(AlbumFragment.PHOTO_INDEX, 0);
        List<Fragment> mFragmentList = new ArrayList<>();

        for (int i = 0; i < mImages.size(); i++) {
            Fragment fragment = new ImagePreviewFragment();
            Bundle args = new Bundle();
            args.putParcelable(URL_KEY, mImages.get(i));
            fragment.setArguments(args);
            mFragmentList.add(fragment);
        }
        mViewPager.setAdapter(new PhotoPagerAdapter(getSupportFragmentManager(), mFragmentList));
        mViewPager.setCurrentItem(mPosition);
    }

    @Override
    protected void bindEvent() {
    }

    @Override
    protected void doMoreInOnCreate() {
    }

    private class PhotoPagerAdapter extends FragmentStatePagerAdapter {

        private List<Fragment> mFragments;

        public PhotoPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
            super(fm);
            mFragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments == null ? 0 : mFragments.size();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        System.gc();
    }
}
