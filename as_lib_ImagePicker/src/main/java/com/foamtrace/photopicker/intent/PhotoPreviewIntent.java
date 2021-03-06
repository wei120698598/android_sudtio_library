package com.foamtrace.photopicker.intent;

import android.content.Context;
import android.content.Intent;

import com.foamtrace.photopicker.PhotoPickerActivity;
import com.foamtrace.photopicker.PhotoPreviewActivity;

import java.util.ArrayList;

/**
 * 预览照片
 * Created by foamtrace on 2015/8/25.
 */
public class PhotoPreviewIntent extends Intent {

    public PhotoPreviewIntent(Context packageContext, boolean isClickImage) {
        super(packageContext,  PhotoPreviewActivity.class);
        this.putExtra(PhotoPickerActivity.EXTRA_CLICK_IMAGE,isClickImage);
    }

    /**
     * 选中的照片地址
     *
     * @param paths
     */
    public void setPhotoPaths(ArrayList<String> paths) {
        this.putStringArrayListExtra(PhotoPreviewActivity.EXTRA_PHOTOS, paths);
    }

    /**
     * 所有的照片地址
     *
     * @param paths
     */
    public void setAllPhotoPaths(ArrayList<String> paths) {
        this.putExtra(PhotoPreviewActivity.EXTRA_ALL_PHOTOS, paths);
    }

    /**
     * 当前照片的下标
     *
     * @param currentItem
     */
    public void setCurrentItem(int currentItem) {
        this.putExtra(PhotoPreviewActivity.EXTRA_CURRENT_ITEM, currentItem);
    }
}
