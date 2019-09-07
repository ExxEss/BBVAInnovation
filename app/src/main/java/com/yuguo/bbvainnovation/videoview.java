package com.yuguo.bbvainnovation;
import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.util.Timer;
import java.util.TimerTask;


public class videoview extends Activity {

    public static videoview inst = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setActivityBackgroundColor(0x94060C50);
        super.onCreate(savedInstanceState);
        inst = this;
        // 全屏显示
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.my_videoview);

        //final VideoView vv = (VideoView) this.findViewById(R.id.videoView);
        final CustomVideoView vv = this.findViewById(R.id.videoView);
        final String uri = "android.resource://" + getPackageName() + "/" + R.raw.face;


        vv.setVideoURI(Uri.parse(uri));
        vv.start();
        vv.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {


            @Override
            public void onPrepared(final MediaPlayer mp) {
                mp.start();
                mp.setLooping(true);

                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        mp.setLooping(false);
                    }
                }, 6000L); // 300 is the delay in millis
            }
        });


        vv.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {


            @Override
            public void onCompletion(MediaPlayer mp) {
                Intent intent = new Intent(inst, MainActivity.class);
                startActivity(intent);
                inst.finish();
            }
        });


    }

    public void setActivityBackgroundColor(int color) {
        View view = this.getWindow().getDecorView();
        view.setBackgroundColor(color);
    }
}