package com.wei.image.imageChoose;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.wei.image.R;
import com.wei.image.imageChoose.photoview.PublicWay;
import com.wei.image.imageChoose.ui.GalleryActivity;
import com.wei.image.imageUtils.ImageUtils;
import com.wei.utils.utils.HideSoftKeyBoard;


/**
 * Created by wei on 2016/7/4.
 */
public class GridAlumAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private int selectedPosition = -1;
    private boolean shape;

    public boolean isShape() {
        return shape;
    }

    public void setShape(boolean shape) {
        this.shape = shape;
    }

    private Activity context;

    public GridAlumAdapter(final Activity context, GridView gridView, final SelectPhotoPop selectPhotoPop) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {// 查看某个照片

            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                if (arg2 == Bimp.tempSelectBitmap.size()) {
                    HideSoftKeyBoard.hideSoftKeyboard(context);
                    selectPhotoPop.showPop(context);
                } else {
                    Intent intent = new Intent(context, GalleryActivity.class);
                    intent.putExtra("position", "1");
                    intent.putExtra("ID", arg2);
                    context.startActivity(intent);
                }
            }
        });
    }


    public void resume() {
        loading();
    }

    public int getCount() {
        if (Bimp.tempSelectBitmap.size() == PublicWay.num) {
            return PublicWay.num;
        }
        return (Bimp.tempSelectBitmap.size() + 1);
    }

    public Object getItem(int arg0) {
        return null;
    }

    public long getItemId(int arg0) {
        return 0;
    }

    public void setSelectedPosition(int position) {
        selectedPosition = position;
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    @SuppressLint("NewApi")
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.gridview_item_published, parent, false);
            holder = new ViewHolder();
            holder.image = (ImageView) convertView.findViewById(R.id.item_grida_image);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (position == Bimp.tempSelectBitmap.size()) {
            holder.image.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.roominfo_add_btn_normal));
            if (position == PublicWay.num) {
                holder.image.setVisibility(View.GONE);
            }
        } else {
            ImageItem imageItem = Bimp.tempSelectBitmap.get(position);
            int degree = ImageUtils.readPictureDegree(imageItem.getImagePath());
            holder.image.setRotation(degree);
            holder.image.setImageBitmap(imageItem.getBitmap());
        }

        return convertView;
    }

    public class ViewHolder {
        public ImageView image;
    }

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    notifyDataSetChanged();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    private void loading() {
        new Thread(new Runnable() {
            public void run() {
                while (true) {
                    if (Bimp.max == Bimp.tempSelectBitmap.size()) {// 用来判断是否是拍照刷新图片
                        Message message = new Message();
                        message.what = 1;
                        handler.sendMessage(message);
                        break;
                    } else {
                        Bimp.max += 1;
                        Message message = new Message();
                        message.what = 1;
                        handler.sendMessage(message);
                    }
                }
            }
        }).start();
    }
}
