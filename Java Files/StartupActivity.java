package com.example.iot_proj;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class StartupActivity extends AppCompatActivity {

    private static final int STARTUP_DURATION = 3000; // Splash screen duration in milliseconds
    private ImageView logoImageView;
    private ImageView newLogoImageView;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);

        logoImageView = findViewById(R.id.logoImageView);
        newLogoImageView = findViewById(R.id.newLogoImageView);

        mediaPlayer = MediaPlayer.create(this, R.raw.startup_music);
        mediaPlayer.start();

        // rotation animation
        ObjectAnimator rotateAnimation = ObjectAnimator.ofFloat(logoImageView, "rotation", 0f, 360f);
        rotateAnimation.setDuration(2000);

        // translation animation
        ObjectAnimator translateAnimation = ObjectAnimator.ofFloat(logoImageView, "translationY", 0f, -400f);
        translateAnimation.setDuration(2000);
        translateAnimation.setInterpolator(new AccelerateDecelerateInterpolator());

        // scale animation
        ObjectAnimator scaleAnimationX = ObjectAnimator.ofFloat(logoImageView, "scaleX", 1f, 1f);
        scaleAnimationX.setDuration(1000);
        ObjectAnimator scaleAnimationY = ObjectAnimator.ofFloat(logoImageView, "scaleY", 1f, 1f);
        scaleAnimationY.setDuration(1000);
        ObjectAnimator restoreScaleAnimationX = ObjectAnimator.ofFloat(logoImageView, "scaleX", 0.5f, 1f);
        restoreScaleAnimationX.setDuration(1000);
        ObjectAnimator restoreScaleAnimationY = ObjectAnimator.ofFloat(logoImageView, "scaleY", 0.5f, 1f);
        restoreScaleAnimationY.setDuration(1000);

        // animation set and add animations
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(rotateAnimation, translateAnimation, scaleAnimationX, scaleAnimationY, restoreScaleAnimationX, restoreScaleAnimationY);

        // apply animation to logoImageView
        animatorSet.start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(STARTUP_DURATION);
                    logoImageView.post(new Runnable() {
                        @Override
                        public void run() {
                            logoImageView.animate().alpha(0f).setDuration(1000).start();
                            newLogoImageView.setAlpha(0f);
                            newLogoImageView.animate().alpha(1f).setDuration(1000).start();
                        }
                    });
                    Thread.sleep(STARTUP_DURATION - 1000);
                    Intent intent = new Intent(StartupActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
