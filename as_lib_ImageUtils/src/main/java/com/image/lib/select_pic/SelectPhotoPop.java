package com.image.lib.select_pic;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.image.lib.R;
import com.image.lib.select_pic.photoview.ImagePicker;
import com.image.lib.select_pic.ui.ImageFileActivity;
import com.orhanobut.logger.Logger;


public class SelectPhotoPop {
    private PopupWindow pop;
    private LinearLayout ll_popup;
    public static Uri photoUri;

    public static final int CUSTOMER_ALUM = 1;
    public static final int NATIVE_ALUM = 0;

    /**
     * 初始化popupWindow
     *
     * @param context
     * @param alum_type 传递CUSTOMER_ALUM = 1使用自定义图片选择器并可同时选择多张，传递NATIVE_ALUM调用系统图片选择器只能一次选一张
     */
    public SelectPhotoPop(final Context context, final int alum_type) {
        initPop(context, alum_type);
    }

    /**
     * 初始化图片选器
     *
     * @param context
     * @param flag
     */
    private void initPop(final Context context, final int flag) {

        if (flag == CUSTOMER_ALUM) {
            Logger.d("初始化了");
            Bimp.bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.roominfo_add_btn_normal);
            Bimp.tempSelectBitmap.clear();
            Bimp.max = 0;
            Bimp.clazz = context.getClass();
        }

        pop = new PopupWindow(context);
        View view = View.inflate(context, R.layout.pop_item_camera, null);
        final Animation animation = AnimationUtils.loadAnimation(context, R.anim.activity_translate_out);
        animation.setFillAfter(true);
        ll_popup = (LinearLayout) view.findViewById(R.id.ll_popup);
        ll_popup.startAnimation(AnimationUtils.loadAnimation(context, R.anim.activity_translate_in));
        pop.setWidth(LayoutParams.MATCH_PARENT);
        pop.setHeight(LayoutParams.WRAP_CONTENT);
        pop.setBackgroundDrawable(new BitmapDrawable());
        pop.setFocusable(true);
        pop.setOutsideTouchable(true);
        pop.setContentView(view);

        RelativeLayout parent = (RelativeLayout) view.findViewById(R.id.parent);
        Button bt1 = (Button) view.findViewById(R.id.item_popupwindows_camera);
        Button bt2 = (Button) view.findViewById(R.id.item_popupwindows_Photo);
        Button bt3 = (Button) view.findViewById(R.id.item_popupwindows_cancel);
        // 整个线性布局
        parent.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                animation.setAnimationListener(new SelectPhotoPop.MyAnimationListener() {

                    @Override
                    public void a() {
                        pop.dismiss();
                    }
                });
                ll_popup.startAnimation(animation);
            }
        });
        // 拍照
        bt1.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                animation.setAnimationListener(new SelectPhotoPop.MyAnimationListener() {

                    @Override
                    public void a() {
                        pop.dismiss();
                        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
                            photoUri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                    new ContentValues());
                            // if (flag == 1) {
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                            ((Activity) context).startActivityForResult(intent, ImagePicker.PHOTO_CAMERA_CODE);
                            ((Activity) context).overridePendingTransition(R.anim.activity_translate_in, R.anim.activity_translate_out);
                            // } else {
                            // photoTakePath =
                            // ImagePicker.PickerFromCamera((Activity)
                            // context,
                            // android.os.Environment.getExternalStorageState()
                            // + "image");
                            // }
                        } else {
                            Toast.makeText(context, "没有内存卡不能拍照", Toast.LENGTH_LONG).show();
                        }
                    }
                });
                ll_popup.startAnimation(animation);
            }
        });
        // 相册
        bt2.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                animation.setAnimationListener(new SelectPhotoPop.MyAnimationListener() {

                    @Override
                    public void a() {
                        pop.dismiss();
                        if (flag == CUSTOMER_ALUM) {// 自定义相册
                            ((Activity) context).startActivityForResult(new Intent(context, ImageFileActivity.class), 0);
                            ((Activity) context).overridePendingTransition(R.anim.activity_translate_in, R.anim.activity_translate_out);
                        } else {
                            ImagePicker.PickerFromAlbum((Activity) context);
                        }
                    }
                });
                ll_popup.startAnimation(animation);

            }
        });
        // 取消
        bt3.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                animation.setAnimationListener(new SelectPhotoPop.MyAnimationListener() {

                    @Override
                    public void a() {
                        pop.dismiss();
                    }
                });
                ll_popup.startAnimation(animation);
            }
        });
    }

    private abstract class MyAnimationListener implements AnimationListener {

        @Override
        public void onAnimationStart(Animation animation) {
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            a();
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }

        public abstract void a();
    }

    /**
     * 显示图片选择器在parentView底部
     *
     * @param activity
     */
    public void showPop(Activity activity) {
        ll_popup.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.activity_translate_in));
        pop.showAtLocation(activity.getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
    }

    /**
     * 关闭页面时要及时清空图片缓存
     */
    public void clearBitmap(){
        Bimp.clearCache();
    }
}