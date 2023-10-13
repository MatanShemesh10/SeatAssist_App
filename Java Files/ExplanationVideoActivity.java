package com.example.iot_proj;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import android.widget.VideoView;

public class ExplanationVideoActivity extends AppCompatActivity {

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explanation_video);

        VideoView vv = (VideoView)this.findViewById(R.id.videoView);
        String uri =
                "android.resource://" + getPackageName() + "/" +
                        R.raw.explanation_video;
        vv.setVideoURI(Uri.parse(uri));
        vv.start();
        findViewById(R.id.returnHomePage).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                try
                {
                    Intent intent = new Intent(ExplanationVideoActivity.this, MainActivity.class);
                    startActivity(intent);
                }
                catch (Exception ex)
                {
                    Toast.makeText(ExplanationVideoActivity.this,ex.getMessage().toString(), Toast.LENGTH_LONG).show();
                    ex.printStackTrace();
                }
            }
        });
    }
}