package com.radiobicocca.android.ui;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.liuguangqiang.swipeback.SwipeBackLayout;
import com.radiobicocca.android.R;

public class FollowUsActivity extends AppCompatActivity {

    private ConstraintLayout ig;
    private ConstraintLayout fb;
    private ConstraintLayout tw;
    private ConstraintLayout yt;
    private ConstraintLayout sp;
    private ConstraintLayout sc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow_us);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setTitle(getTitle());

        ig = findViewById(R.id.instagram);
        fb = findViewById(R.id.facebook);
        tw = findViewById(R.id.twitter);
        yt = findViewById(R.id.youtube);

        ig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse(getString(R.string.url_instagram));
                startActivity(new Intent(Intent.ACTION_VIEW, uri));
            }
        });

        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(newFacebookIntent(view.getContext().getPackageManager(),
                        getString(R.string.url_facebook)));
            }
        });

        tw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse(getString(R.string.url_twitter));
                startActivity(new Intent(Intent.ACTION_VIEW, uri));
            }
        });

        yt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse(getString(R.string.url_youtube));
                startActivity(new Intent(Intent.ACTION_VIEW, uri));
            }
        });

    }

    public static Intent newFacebookIntent(PackageManager pm, String url) {
        Uri uri = Uri.parse(url);
        try {
            ApplicationInfo applicationInfo = pm.getApplicationInfo("com.facebook.katana", 0);
            if (applicationInfo.enabled) {
                // http://stackoverflow.com/a/24547437/1048340
                uri = Uri.parse("fb://facewebmodal/f?href=" + url);
            }
        } catch (PackageManager.NameNotFoundException ignored) {
        }
        return new Intent(Intent.ACTION_VIEW, uri);
    }

}
