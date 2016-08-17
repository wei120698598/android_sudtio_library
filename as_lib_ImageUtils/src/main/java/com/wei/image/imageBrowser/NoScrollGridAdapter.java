/*
 * Copyright (c) 2016. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.wei.image.imageBrowser;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.wei.image.R;

import java.util.ArrayList;

public class NoScrollGridAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<String> imageUrls;

    public NoScrollGridAdapter(Context context, ArrayList<String> urls) {
        this.context = context;
        this.imageUrls = urls;
        imageLoader = ImageLoader.getInstance();
    }

    @Override
    public int getCount() {

        return imageUrls == null ? 0 : imageUrls.size();
    }

    @Override
    public Object getItem(int position) {
        return imageUrls.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            holder = new Holder();
            convertView = View.inflate(context, R.layout.gridview_item_have_nomal, null);
            holder.imageView = (ImageView) convertView.findViewById(R.id.iv_image);
            holder.imageView.setTag(position);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        String img_url = imageUrls.get(position);
        if (img_url.contains("org")) {
            img_url = img_url.replace("org", "sma");
        }
        imageLoader.displayImage(img_url, holder.imageView);
        return convertView;
    }


    public Holder holder;
    private ImageLoader imageLoader;

    public class Holder {
        ImageView imageView;
    }

}
