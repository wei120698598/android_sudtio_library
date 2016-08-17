/*
 * Copyright (c) 2016. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.wei.view.customer.view.draggridview;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.wei.view.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Wei on 2016/8/17.
 */

public class DragAdapter extends BaseAdapter implements DragGridBaseAdapter {
    private int mHidePosition = -1;
    public boolean isOk = false;
    private Context context;

    private ArrayList<String> images = new ArrayList<>();

    public DragAdapter(Context context, ArrayList<String> images) {
        this.context = context;
        this.images = images;
    }

    @Override
    public int getCount() {

        return images.size();
    }

    @Override
    public String getItem(int position) {
        return images.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * 由于复用convertView导致某些item消失了，所以这里不复用item，
     */
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        convertView = View.inflate(context, R.layout.grid_item, null);
        ImageView mImageView = (ImageView) convertView.findViewById(R.id.item_image);
        ImageView iv_del = (ImageView) convertView.findViewById(R.id.iv_del);
        if (position == mHidePosition) {
            convertView.setVisibility(View.INVISIBLE);
        }
        if (position == images.size()) {
            mImageView.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.roominfo_add_btn_normal));
        } else {
            iv_del.setVisibility(View.VISIBLE);
            mImageView.setImageURI(Uri.fromFile(new File(images.get(position))));
        }
        iv_del.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (images.size() > 1) {
                    images.remove(position);
                    notifyDataSetChanged();
                } else {
                    Toast.makeText(context, "最后一项不可以删除", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return convertView;
    }

    @Override
    public void reorderItems(int oldPosition, int newPosition) {
        String temp = images.get(oldPosition);
        if (oldPosition < newPosition) {
            for (int i = oldPosition; i < newPosition; i++) {
                Collections.swap(images, i, i + 1);
            }
        } else if (oldPosition > newPosition) {
            for (int i = oldPosition; i > newPosition; i--) {
                Collections.swap(images, i, i - 1);
            }
        }

        images.set(newPosition, temp);
    }

    @Override
    public void setHideItem(int hidePosition) {
        this.mHidePosition = hidePosition;
        notifyDataSetChanged();
    }
}
