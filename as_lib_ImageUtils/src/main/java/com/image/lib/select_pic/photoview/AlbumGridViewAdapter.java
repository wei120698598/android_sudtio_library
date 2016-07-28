package com.image.lib.select_pic.photoview;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.image.lib.R;
import com.image.lib.select_pic.BitmapCache;
import com.image.lib.select_pic.ImageItem;

import java.io.IOException;
import java.util.ArrayList;


/**
 * 这个是显示一个文件夹里面的所有图片时用的适配器
 *
 * @author king
 * @version 2014年10月18日 下午11:49:35
 * @QQ:595163260
 */
public class AlbumGridViewAdapter extends BaseAdapter {
    final String TAG = getClass().getSimpleName();
    private Context context;
    private ArrayList<com.image.lib.select_pic.ImageItem> dataList;
    private ArrayList<ImageItem> selectedDataList;
    private DisplayMetrics dm;
    BitmapCache cache;

    public AlbumGridViewAdapter(Context c, ArrayList<ImageItem> dataList, ArrayList<ImageItem> selectedDataList) {
        context = c;
        cache = new BitmapCache();
        this.dataList = dataList;
        this.selectedDataList = selectedDataList;
        dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
    }

    public int getCount() {
        return dataList.size();
    }

    public Object getItem(int position) {
        return dataList.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    BitmapCache.ImageCallback callback = new com.image.lib.select_pic.BitmapCache.ImageCallback() {
        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        @Override
        public void imageLoad(ImageView imageView, Bitmap bitmap, Object... params) {
            if (imageView != null && bitmap != null) {
                String url = (String) params[0];
                if (url != null && url.equals((String) imageView.getTag())) {
                    int degree = readPictureDegree(url);
                    imageView.setRotation(degree);
                    imageView.setImageBitmap(bitmap);
                } else {
                    Log.e(TAG, "callback, bmp not match");
                }
            } else {
                Log.e(TAG, "callback, bmp null");
            }
        }
    };

    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (degree == 0) {
            return degree;
        } else {
            return degree;
        }
    }

    /**
     * 存放列表项控件句柄
     */
    private class ViewHolder {
        public ImageView imageView;
        public ToggleButton toggleButton;
        public Button choosetoggle;
        public TextView textView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = View.inflate(context, R.layout.activity_plugin_camera_select_imageview, null);
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.image_view);
            viewHolder.toggleButton = (ToggleButton) convertView.findViewById(R.id.toggle_button);
            viewHolder.choosetoggle = (Button) convertView.findViewById(R.id.choosedbt);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        String path;
        if (dataList != null && dataList.size() > position)
            path = dataList.get(position).getImagePath();
        else
            path = "camera_default";
        if (path.contains("camera_default")) {
            viewHolder.imageView.setImageResource(R.drawable.moren);
        } else {
            final com.image.lib.select_pic.ImageItem item = dataList.get(position);
            viewHolder.imageView.setTag(item.getImagePath());
            cache.displayBmp(viewHolder.imageView, item.getThumbnailPath(), item.getImagePath(), callback);
        }
        viewHolder.toggleButton.setTag(position);
        viewHolder.choosetoggle.setTag(position);
        viewHolder.toggleButton.setOnClickListener(new ToggleClickListener(viewHolder.choosetoggle));
        if (selectedDataList.contains(dataList.get(position))) {
            viewHolder.toggleButton.setChecked(true);
            viewHolder.choosetoggle.setVisibility(View.VISIBLE);
        } else {
            viewHolder.toggleButton.setChecked(false);
            viewHolder.choosetoggle.setVisibility(View.GONE);
        }
        return convertView;
    }

    public int dipToPx(int dip) {
        return (int) (dip * dm.density + 0.5f);
    }

    private class ToggleClickListener implements OnClickListener {
        Button chooseBt;

        public ToggleClickListener(Button choosebt) {
            this.chooseBt = choosebt;
        }

        @Override
        public void onClick(View view) {
            if (view instanceof ToggleButton) {
                ToggleButton toggleButton = (ToggleButton) view;
                int position = (Integer) toggleButton.getTag();
                if (dataList != null && mOnItemClickListener != null && position < dataList.size()) {
                    mOnItemClickListener.onItemClick(toggleButton, position, toggleButton.isChecked(), chooseBt);
                }
            }
        }
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener l) {
        mOnItemClickListener = l;
    }

    public interface OnItemClickListener {
        public void onItemClick(ToggleButton view, int position, boolean isChecked, Button chooseBt);
    }

}
