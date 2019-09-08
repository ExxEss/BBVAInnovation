package com.yuguo.bbvainnovation;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.camera2.CameraDevice;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.yuguo.bbvainnovation.listeners.PictureCapturingListener;
import com.yuguo.bbvainnovation.services.APictureCapturingService;
import com.yuguo.bbvainnovation.services.PictureCapturingServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;


public class FaceRecognition extends Activity implements PictureCapturingListener, ActivityCompat.OnRequestPermissionsResultCallback {
    private APictureCapturingService pictureService;

    private static final String[] requiredPermissions = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
    };
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_CODE = 1;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.face_recognition);
        handler.sendEmptyMessageDelayed(0, 15000);

        checkPermissions();
        pictureService = PictureCapturingServiceImpl.getInstance(this);
        pictureService.startCapturing(this);
        ImageView im = findViewById(R.id.imageview);

        int[] imgs = {R.mipmap.front, R.mipmap.left, R.mipmap.right, R.mipmap.up, R.mipmap.down};

        final Handler handler1 = new Handler();
        Runnable r = new Runnable() {
            int i = 0;

            FaceRecognition fr = new FaceRecognition();
            public void run() {
                im.setImageResource(imgs[i]);


                i++;

                if (i >= imgs.length)
                    i = 0;
                handler1.postDelayed(this, 3000);
            }
        };

        handler1.postDelayed(r, 0);

        final float endSize = 70;
        final int animationDuration = 1000; // Animation duration in ms

        final Handler handlerTemp = new Handler();
        Runnable r1 = new Runnable() {
            int i = 0;
            @Override
            public void run() {
                i++;
                TextView textId = findViewById(R.id.animationNum);
                textId.setText(i+"");
                textId.setTextSize(60);
                if (i==3)
                    i = 0;
                ValueAnimator animator = ObjectAnimator.ofFloat(textId, "textSize", endSize);
                animator.setDuration(animationDuration);
                animator.start();
                handlerTemp.postDelayed(this,1000);
            }
        };

        handlerTemp.postDelayed(r1,0);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            getHome();
            super.handleMessage(msg);
        }
    };

    public void getHome() {
        Intent intent = new Intent(FaceRecognition.this, MainActivity.class);
        startActivity(intent);
        finish();
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onDoneCapturingAllPhotos(TreeMap<String, byte[]> picturesTaken) {
        if (picturesTaken != null && !picturesTaken.isEmpty()) {
            picturesTaken.forEach((pictureUrl, pictureData) -> {
                //convert the byte array 'pictureData' to a bitmap (no need to read the file from the external storage) but in case you
                //You can also use 'pictureUrl' which stores the picture's location on the device
                final Bitmap bitmap = BitmapFactory.decodeByteArray(pictureData, 0, pictureData.length);
            });
            return;
        }
    }

    @Override
    public void onCaptureDone(String pictureUrl, byte[] pictureData) {
        if (pictureData != null && pictureUrl != null) {
            runOnUiThread(() -> {
                //convert byte array 'pictureData' to a bitmap (no need to read the file from the external storage)
                final Bitmap bitmap = BitmapFactory.decodeByteArray(pictureData, 0, pictureData.length);
                //scale image to avoid POTENTIAL "Bitmap too large to be uploaded into a texture" when displaying into an ImageView
                final int nh = (int) (bitmap.getHeight() * (512.0 / bitmap.getWidth()));
                final Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 512, nh, true);
                //do whatever you want with the bitmap or the scaled one...
            });
        }
    }

    private void showToast(final String text) {
        runOnUiThread(() ->
                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show()
        );
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_CODE: {
                if (!(grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    checkPermissions();
                }
            }
        }
    }


    @TargetApi(Build.VERSION_CODES.M)
    private void checkPermissions() {
        final List<String> neededPermissions = new ArrayList<>();
        for (final String permission : requiredPermissions) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(),
                    permission) != PackageManager.PERMISSION_GRANTED) {
                neededPermissions.add(permission);
            }
        }
        if (!neededPermissions.isEmpty()) {
            requestPermissions(neededPermissions.toArray(new String[]{}),
                    MY_PERMISSIONS_REQUEST_ACCESS_CODE);
        }
    }
}


