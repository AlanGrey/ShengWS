package com.jstudio.widget.photoselector;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.SparseArray;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.jstudio.R;
import com.jstudio.adapter.list.CommonAdapter;
import com.jstudio.adapter.list.ViewHolder;
import com.jstudio.base.BaseSupportFragment;
import com.jstudio.utils.FrescoUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jason
 */
public class AlbumFragment extends BaseSupportFragment {

    public static final String INTENT_MAX_NUM = "intent_max_num";
    public static final String SELECTED_PIC = "intent_selected_pic_list";
    public static final String PHOTO_INDEX = "photo_index";

    /**
     * 允许选择图片的数量，通过setArgument传进来
     */
    private int mEnableCount;

    private GridView mGridView;
    private PhotoSelectorAdapter mAdapter;
    private TextView mSelectedText;
    private TextView mPreview;

    private TextView mConfirm;
    private ArrayList<Uri> mSelectedPic = new ArrayList<>();
    private List<Uri> mPictures;

    @Override
    protected int setLayout() {
        return R.layout.jfw_fgm_photo_selector;
    }

    @Override
    protected void findViews(View view) {
        mGridView = (GridView) view.findViewById(R.id.photo_selector_grid_view);
        mSelectedText = (TextView) view.findViewById(R.id.selected);
        mPreview = (TextView) view.findViewById(R.id.preview);
        mConfirm = (TextView) view.findViewById(R.id.confirm);
    }

    @Override
    protected void initialization() {
        Bundle bundle = getArguments();
        mEnableCount = bundle.getInt(INTENT_MAX_NUM);
        mPictures = getThumb();

        mSelectedText.setText(getString(R.string.selected) + "0");
        mPreview.setEnabled(false);
        mConfirm.setEnabled(false);
    }

    @Override
    protected void bindEvent() {
        mPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(mContext, LocalPicPreviewActivity.class);
                intent.putExtra(SELECTED_PIC, mSelectedPic);
                intent.putExtra(PHOTO_INDEX, 0);
                mContext.startActivity(intent);
            }
        });
        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(SELECTED_PIC, mSelectedPic);
                getActivity().setResult(Activity.RESULT_OK, intent);
                getActivity().finish();
            }
        });
    }

    @Override
    protected void onCreateView() {
        mAdapter = new PhotoSelectorAdapter(mContext, mPictures);
        mGridView.setAdapter(mAdapter);
    }

    private List<Uri> getThumb() {
        List<Uri> uris = new ArrayList<>();
        Cursor cursor = MediaStore.Images.Media.query(mContext.getContentResolver(),
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Images.ImageColumns._ID}, null, MediaStore.Images.Media.DATE_TAKEN);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToLast();
            while (cursor.moveToPrevious()) {
                uris.add(Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cursor.getString(0)));
            }
            cursor.close();
        }
        return uris;
    }

    public class PhotoSelectorAdapter extends CommonAdapter<Uri> {

        SparseArray<Uri> mState = new SparseArray<>();

        public PhotoSelectorAdapter(Context context, List<Uri> data) {
            super(context, data);
        }

        @Override
        public void inflateContent(ViewHolder holder, final int position, Uri uri) {
            SimpleDraweeView photo = holder.getView(R.id.photo);
            FrescoUtils.loadImage(photo, uri, 160, 160, null, null);
            photo.setAspectRatio(1.0f);

            final CheckBox checkBox = holder.getView(R.id.photo_check_box);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        if (mSelectedPic.size() >= mEnableCount) {
                            if (mState.get(position) == null) {
                                checkBox.setChecked(false);
                            }
                            return;
                        }
                        mState.put(position, mPictures.get(position));
                        if (!mSelectedPic.contains(mPictures.get(position))) {
                            mSelectedPic.add(mPictures.get(position));
                        }
                    } else {
                        mState.remove(position);
                        mSelectedPic.remove(mPictures.get(position));
                    }

                    if (mSelectedPic.size() == 0) {
                        mSelectedText.setText(getString(R.string.selected) + "0");
                        mPreview.setEnabled(false);
                        mConfirm.setEnabled(false);
                    } else {
                        mSelectedText.setText(getString(R.string.selected) + mSelectedPic.size());
                        mPreview.setEnabled(true);
                        mConfirm.setEnabled(true);
                    }
                }
            });

            boolean found = false;
            if (mState.get(position) != null) {
                for (Object tempUri : mSelectedPic) {
                    if (tempUri.equals(mState.get(position))) {
                        checkBox.setChecked(true);
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    mState.remove(position);
                    checkBox.setChecked(false);
                }
            } else {
                checkBox.setChecked(false);
            }
        }

        @Override
        public int setItemLayout(int type) {
            return R.layout.jfw_item_gv_photo_selector;
        }
    }
}
