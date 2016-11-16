package com.jstudio.widget.photoselector;

import android.graphics.Bitmap;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;

import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.DataSource;
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber;
import com.facebook.imagepipeline.image.CloseableImage;
import com.jstudio.R;
import com.jstudio.base.BaseSupportFragment;
import com.jstudio.utils.FrescoUtils;
import com.jstudio.utils.SizeUtils;
import com.jstudio.widget.progressbar.MaterialProgressBar;

import uk.co.senab.photoview.PhotoViewAttacher;

public class ImagePreviewFragment extends BaseSupportFragment {

    private Uri mImageUrl;
    private ImageView mImageView;
    private MaterialProgressBar mProgressBar;

    private PhotoViewAttacher mAttacher;

    @Override
    protected int setLayout() {
        return R.layout.jfw_fgm_photo_view;
    }

    @Override
    protected void findViews(View view) {
        mImageView = (ImageView) view.findViewById(R.id.image);
        mProgressBar = (MaterialProgressBar) view.findViewById(R.id.loading);
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    protected void initialization() {
        mImageUrl = getArguments().getParcelable(LocalPicPreviewActivity.URL_KEY);
        mAttacher = new PhotoViewAttacher(mImageView);

        FrescoUtils.getImageBitmap(mContext, mImageUrl, SizeUtils.getScreenWidth(mContext),
                SizeUtils.getScreenHeight(mContext), null, new BaseBitmapDataSubscriber() {
                    @Override
                    protected void onNewResultImpl(final Bitmap bitmap) {
                        mImageView.post(new Runnable() {
                            @Override
                            public void run() {
                                mImageView.setImageBitmap(bitmap);
                                mAttacher.update();
                            }
                        });
                    }

                    @Override
                    protected void onFailureImpl(DataSource<CloseableReference<CloseableImage>> dataSource) {

                    }
                });
    }

    @Override
    protected void bindEvent() {
        mAttacher.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {

            @Override
            public void onPhotoTap(View arg0, float arg1, float arg2) {
                getActivity().finish();
            }
        });
    }

    @Override
    protected void onCreateView() {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mImageView.setImageBitmap(null);
        System.gc();
    }
}