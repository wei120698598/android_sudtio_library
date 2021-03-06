/*
 * Copyright (c) 2016. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.wei.image.zxing.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.wei.image.R;
import com.wei.image.zxing.camera.CameraManager;
import com.wei.image.zxing.decoding.CaptureActivityHandler;
import com.wei.image.zxing.decoding.DecodeFormatManager;
import com.wei.image.zxing.decoding.InactivityTimer;
import com.wei.image.zxing.decoding.RGBLuminanceSource;
import com.wei.image.zxing.view.ViewfinderView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Hashtable;
import java.util.Vector;

/**
 * Initial the camera
 *
 * @author Ryan.Tang
 */
public class MipcaCaptureActivity extends Activity implements Callback {

    public static final String LOG_TAG = "com.wei.image";

    private CaptureActivityHandler handler;
    private ViewfinderView viewfinderView;
    private boolean hasSurface;
    private Vector<BarcodeFormat> decodeFormats;
    private String characterSet;
    private InactivityTimer inactivityTimer;
    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    private static final float BEEP_VOLUME = 0.10f;
    private boolean vibrate;
    public static final int REQUEST_CODE_LOCAL = 99;
    public static final String REQUEST_RESULT_TEXT = "request_result_text";
    public static final String REQUEST_RESULT_BITMAP = "request_result_bitmap";
    private boolean isGetBitmap;

    /**
     * 启动扫描界面
     *
     * @param context     上下文
     * @param isGetBitmap 是否返回二维码截图
     */
    public static void startActivity(Activity context, boolean isGetBitmap) {
        Intent intent = new Intent(context, MipcaCaptureActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("isGetBitmap", isGetBitmap);
        context.startActivityForResult(intent, MipcaCaptureActivity.REQUEST_CODE_LOCAL);
    }


    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);
        // ViewUtil.addTopView(getApplicationContext(), this,
        // R.string.scan_card);

        if (getIntent().hasExtra("isGetBitmap")) {
            isGetBitmap = getIntent().getBooleanExtra("isGetBitmap", false);
        }

        CameraManager.init(getApplication());
        viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);

        ImageView button_menu = (ImageView) findViewById(R.id.button_menu);// 选择图片
        button_menu.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent;
                if (Build.VERSION.SDK_INT < 19) {
                    intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                } else {
                    intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                }
                String circleCrop = null;

                if (isCropChooseImage) {
                    intent.putExtra("crop", "true");
                    intent.putExtra("aspectX", 1);
                    intent.putExtra("aspectY", 1);
                    intent.putExtra("outputX", 300);
                    intent.putExtra("outputY", 300);
                    intent.putExtra("circleCrop", circleCrop);
                    intent.putExtra("return-data", true);
                    intent.putExtra("noFaceDetection", true);
                }

                Intent wrapperIntent = Intent.createChooser(intent, "选择二维码图片");
                startActivityForResult(wrapperIntent, REQUEST_CODE_LOCAL);
            }
        });
        ImageView mButtonBack = (ImageView) findViewById(R.id.button_back);// 返回
        mButtonBack.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
                // int pid = android.os.Process.myPid();
                // android.os.Process.killProcess(pid);
            }
        });

        btn_light = (ImageView) findViewById(R.id.btn_light);
        btn_light.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    CameraManager.get().setTorch(!newSetting, handler);
                    newSetting = !newSetting;
                    // boolean isFlashOpen = CameraManager.get().isLightOpen();
                    // if (isFlashOpen) {
                    // CameraManager.get().closeLight(btn_light);
                    // } else {
                    // CameraManager.get().openLight(btn_light);
                    // }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        hasSurface = false;
        inactivityTimer = new InactivityTimer(this);
    }

    private boolean newSetting = false;

    private String photo_path;
    private ProgressDialog mProgress;
    private Bitmap scanBitmap;

    // @Override
    // public boolean onKeyDown(int kCode, KeyEvent kEvent) {
    // switch (kCode) {
    // case KeyEvent.KEYCODE_BACK:
    // int pid = android.os.Process.myPid();
    // android.os.Process.killProcess(pid);
    // return true;
    // }
    // return super.onKeyDown(kCode, kEvent);
    // }

    @Override
    protected void onResume() {
        super.onResume();
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        decodeFormats = null;
        characterSet = null;

        playBeep = true;
        AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }
        initBeepSound();
        vibrate = true;

    }

    private static final int PARSE_BARCODE_SUC = 300;
    private static final int PARSE_BARCODE_FAIL = 400;

    /**
     * 选择图片后是否进行裁剪
     */
    private static final boolean isCropChooseImage = true;
    Bitmap image = null;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        try {
            if (requestCode == REQUEST_CODE_LOCAL && resultCode == RESULT_OK && data != null) {

                if (isCropChooseImage) {
                    image = data.getParcelableExtra("data");
                } else {
                    Cursor cursor = getContentResolver().query(data.getData(), null, null, null, null);
                    if (cursor != null && cursor.moveToFirst()) {
                        photo_path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                    }
                    cursor.close();
                    image = getBitmapByPath(photo_path);
                }

                if (image == null) {
                    Toast.makeText(MipcaCaptureActivity.this, "图片获取失败!", Toast.LENGTH_SHORT).show();
                    return;
                }

                mProgress = new ProgressDialog(MipcaCaptureActivity.this);
                mProgress.setMessage("正在扫描...");
                mProgress.setCancelable(false);
                mProgress.show();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Result result = scanningImage(image);
                        if (result != null) {
                            Message m = mHandler.obtainMessage();
                            m.what = PARSE_BARCODE_SUC;
                            m.obj = result.getText();
                            mHandler.sendMessage(m);
                        } else {
                            Message m = mHandler.obtainMessage();
                            m.what = PARSE_BARCODE_FAIL;
                            m.obj = "图片解析失败!";
                            mHandler.sendMessage(m);
                        }
                    }
                }).start();

            } else {
                Toast.makeText(MipcaCaptureActivity.this, "扫描失败!", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(MipcaCaptureActivity.this, "解析出现异常，扫描失败!", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            if (mProgress != null && mProgress.isShowing())
                mProgress.dismiss();
            super.handleMessage(msg);
            switch (msg.what) {
                case PARSE_BARCODE_SUC:
                    handleDecode((String) msg.obj, scanBitmap);
                    break;
                case PARSE_BARCODE_FAIL:
                    Toast.makeText(MipcaCaptureActivity.this, (String) msg.obj, Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };

    private String recode(String str) {
        String formart = "";

        try {
            boolean ISO = Charset.forName("ISO-8859-1").newEncoder().canEncode(str);
            if (ISO) {
                formart = new String(str.getBytes("ISO-8859-1"), "UTF-8");
            } else {
                formart = str;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return formart;
    }

    /**
     * 扫描二维码图片的方法
     *
     * @return
     */
    public Result scanningImage(Bitmap bitmap) {
        if (bitmap == null) {
            Toast.makeText(this, "解析图片失败", Toast.LENGTH_SHORT).show();
            return null;
        }

        scanBitmap = bitmap;

        Hashtable<DecodeHintType, Object> hints = new Hashtable<DecodeHintType, Object>();
        // 可以解析的编码类型
        Vector<BarcodeFormat> decodeFormats = new Vector<BarcodeFormat>();
        if (decodeFormats == null || decodeFormats.isEmpty()) {
            decodeFormats = new Vector<BarcodeFormat>();

            // 这里设置可扫描的类型，我这里选择了都支持
            decodeFormats.addAll(DecodeFormatManager.ONE_D_FORMATS);
            decodeFormats.addAll(DecodeFormatManager.QR_CODE_FORMATS);
            decodeFormats.addAll(DecodeFormatManager.DATA_MATRIX_FORMATS);
            decodeFormats.addAll(DecodeFormatManager.PRODUCT_FORMATS);
        }
        hints.put(DecodeHintType.POSSIBLE_FORMATS, decodeFormats);
        hints.put(DecodeHintType.CHARACTER_SET, "UTF8"); // 设置二维码内容的编码


        RGBLuminanceSource source = new RGBLuminanceSource(scanBitmap);
        BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
        QRCodeReader reader = new QRCodeReader();
        try {
            return reader.decode(bitmap1, hints);

        } catch (NotFoundException e) {
            e.printStackTrace();
        } catch (ChecksumException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Bitmap getBitmapByPath(String path) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; // 先获取原大小
        Bitmap scanBitmap = BitmapFactory.decodeFile(path, options);
        options.inJustDecodeBounds = false; // 获取新的大小
        int sampleSize = (int) (options.outHeight / (float) 200);
        if (sampleSize <= 0)
            sampleSize = 1;
        options.inSampleSize = sampleSize;
        scanBitmap = BitmapFactory.decodeFile(path, options);
//        Log.d(LOG_TAG,"压缩后图片大小"+scanBitmap.)
        return scanBitmap;
    }

    public static Bitmap getBitmapByBytes(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] bytes = baos.toByteArray();
        //对于图片的二次采样,主要得到图片的宽与高
        int width = 0;
        int height = 0;
        int sampleSize = 1; //默认缩放为1
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;  //仅仅解码边缘区域
        //如果指定了inJustDecodeBounds，decodeByteArray将返回为空
        BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
        //得到宽与高
        height = options.outHeight;
        width = options.outWidth;

        //图片实际的宽与高，根据默认最大大小值，得到图片实际的缩放比例
        while ((height / sampleSize > 200)
                || (width / sampleSize > 200)) {
            sampleSize *= 2;
        }

        //不再只加载图片实际边缘
        options.inJustDecodeBounds = false;
        //并且制定缩放比例
        options.inSampleSize = sampleSize;
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        CameraManager.get().closeDriver();
    }

    @Override
    protected void onDestroy() {
        if (inactivityTimer != null)
            inactivityTimer.shutdown();
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }

        super.onDestroy();
        if (scanBitmap != null && !scanBitmap.isRecycled()) {
            scanBitmap.recycle();
        }
        if (image != null && !image.isRecycled()) {
            image.recycle();
        }
    }

    /**
     * @param resultString
     * @param barcode
     */
    public void handleDecode(String resultString, Bitmap barcode) {// 返回扫描的数据
        inactivityTimer.onActivity();
        playBeepSoundAndVibrate();
        Log.d(LOG_TAG, resultString);
        if (resultString.equals("")) {
            Toast.makeText(MipcaCaptureActivity.this, "解析结果为空!", Toast.LENGTH_SHORT).show();
        } else {
            Intent resultIntent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString(REQUEST_RESULT_TEXT, recode(resultString));
            if (isGetBitmap) {
                try {
                  Bitmap  bitmap = getBitmapByBytes(barcode);

                    if (bitmap != null) {
                        Log.d(LOG_TAG, "返回结果图片");
                        bundle.putParcelable(REQUEST_RESULT_BITMAP, bitmap);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            resultIntent.putExtras(bundle);
            setResult(RESULT_OK, resultIntent);
        }
        finish();
        // if(handler!=null) //实现连续扫描
        // handler.restartPreviewAndDecode();
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);
        } catch (IOException ioe) {
            return;
        } catch (RuntimeException e) {
            return;
        }
        if (handler == null) {
            handler = new CaptureActivityHandler(this, decodeFormats, characterSet);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;

    }

    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public Handler getHandler() {
        return handler;
    }

    public void drawViewfinder() {
        viewfinderView.drawResultBitmap(scanBitmap);
    }

    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            // The volume on STREAM_SYSTEM is not adjustable, and users found it
            // too loud,
            // so we now play on the music stream.
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);

            AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.beep);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepareAsync();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }

    private static final long VIBRATE_DURATION = 200L;

    private void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    /**
     * When the beep has finished playing, rewind to queue up another one.
     */
    private final OnCompletionListener beepListener = new OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };
    private ImageView btn_light;


}