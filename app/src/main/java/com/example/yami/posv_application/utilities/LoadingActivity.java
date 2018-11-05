package com.example.yami.posv_application.utilities;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.example.yami.posv_application.R;
import com.example.yami.posv_application.activities.BaseActivity;
import com.example.yami.posv_application.activities.BaseActivity2;

public class LoadingActivity extends BaseActivity2 {

    private ImageView imgAndroid;
    private Animation anim;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lbs_loading);

        startLoading();

    }
    private void startLoading() {
        imgAndroid = (ImageView) findViewById(R.id.img_android);
        anim = AnimationUtils.loadAnimation(this, R.anim.loading);
        imgAndroid.setAnimation(anim);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 4500);
    }

}