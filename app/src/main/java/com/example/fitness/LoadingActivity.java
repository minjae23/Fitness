package com.example.fitness;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.VideoView;
import androidx.appcompat.app.AppCompatActivity;

public class LoadingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        VideoView videoView = findViewById(R.id.loadingVideoView);
        Uri video = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.running_jump);
        videoView.setVideoURI(video);
        videoView.setOnCompletionListener(mp -> {
            // 비디오 재생이 끝나면 로그인 액티비티로 이동
            startActivity(new Intent(LoadingActivity.this, LoginActivity.class));
            finish();
        });
        videoView.start();
    }
}
