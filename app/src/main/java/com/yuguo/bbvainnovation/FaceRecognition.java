package com.yuguo.bbvainnovation;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;


import com.yuguo.bbvainnovation.listeners.PictureCapturingListener;
import com.yuguo.bbvainnovation.services.APictureCapturingService;

import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;


public class FaceRecognition extends Activity implements PictureCapturingListener, ActivityCompat.OnRequestPermissionsResultCallback {
    private APictureCapturingService pictureService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.face_recognition);
        handler.sendEmptyMessageDelayed(0, 3000);

        Timer timer = new Timer();
        for (int i = 1; i < 61; i++) {
            int finalI = i;
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    TextView textId = findViewById(R.id.animationNum);
                    textId.setText(((int) ((finalI - 1) / 20 )+ ""));
                    /*int textSize = (int) ((finalI % 20));
                    textId.setTextSize(textSize);*/

                }
            }, 50 * i);

        }
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
            showToast("Done capturing all photos!");
            return;
        }
        showToast("No camera detected!");
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
            showToast("Picture saved to " + pictureUrl);
        }
    }

    private void showToast(final String text) {
        runOnUiThread(() ->
                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show()
        );
    }
}


