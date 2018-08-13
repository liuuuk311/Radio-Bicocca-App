package com.radiobicocca.android.ui;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.VideoView;

import com.radiobicocca.android.R;

public class EasterEggActivity extends AppCompatActivity {

    private VideoView videoEgg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_easter_egg);


        videoEgg = findViewById(R.id.videoEgg);
        videoEgg.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                startActivity(intent);
            }
        });


        String uri = "android.resource://" + getPackageName() + "/" + R.raw.egg;
        videoEgg.setVideoURI(Uri.parse(uri));
        videoEgg.start();

    }

}
