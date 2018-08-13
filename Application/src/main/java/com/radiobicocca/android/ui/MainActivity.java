/*
 * Copyright 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.radiobicocca.android.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;

import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import android.widget.ProgressBar;
import android.widget.TextView;


import com.jackandphantom.blurimage.BlurImage;
import com.jaeger.library.StatusBarUtil;
import com.radiobicocca.android.Client.MediaBrowserHelper;
import com.radiobicocca.android.Common.ConnectionDetector;
import com.radiobicocca.android.R;
import com.radiobicocca.android.Service.MusicService;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;


import java.util.List;



public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private static final String TAG = "MAIN ACTIVITY";

    private static final int blurPerc = 20;

    private ImageView mAlbumArt;
    private TextView mTitleTextView;
    private TextView mArtistTextView;
    private ImageView mMediaControlsImage;
    private MediaBrowserHelper mMediaBrowserHelper;
    private ProgressBar mProgressBar;
    private ImageButton mShareButton;
    private ImageButton mCommentButton;

    private ImageButton buttonPlay;
    private ImageButton buttonStop;
    private ImageView blurImage;

    private boolean mIsPlaying;

    private FirebaseAnalytics mFirebaseAnalytics;

    //TODO Controllare connessione internet

    //TODO Drawer ovunque

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMediaBrowserHelper = new MediaBrowserConnection(this);
        mMediaBrowserHelper.registerCallback(new MediaBrowserListener());





        Log.d(TAG, "oncreate, IS PLAYING? " + mIsPlaying);
        initializeUI();

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
    }


    /**
     * Customize the connection to our {@link android.support.v4.media.MediaBrowserServiceCompat}
     * and implement our app specific desires.
     */
    private class MediaBrowserConnection extends MediaBrowserHelper {
        private MediaBrowserConnection(Context context) {
            super(context, MusicService.class);
        }

        @Override
        protected void onConnected(@NonNull MediaControllerCompat mediaController) {
            Log.d(TAG, "On Connected, IS PLAYING? " + mIsPlaying);
        }

        @Override
        protected void onChildrenLoaded(@NonNull String parentId,
                                        @NonNull List<MediaBrowserCompat.MediaItem> children) {
            super.onChildrenLoaded(parentId, children);
            Log.d(TAG, "On Children Loaded, IS PLAYING? " + mIsPlaying);
            final MediaControllerCompat mediaController = getMediaController();

            // Queue up all media items for this simple sample.
            for (final MediaBrowserCompat.MediaItem mediaItem : children) {
                mediaController.addQueueItem(mediaItem.getDescription());
            }

            // Call prepare now so pressing play just works.
            mediaController.getTransportControls().prepare();
            Log.d(TAG, "after prpare, IS PLAYING? " + mIsPlaying);
        }
    }

    /**
     * Implementation of the {@link MediaControllerCompat.Callback} methods we're interested in.
     * <p>
     * Here would also be where one could override
     * {@code onQueueChanged(List<MediaSessionCompat.QueueItem> queue)} to get informed when items
     * are added or removed from the queue. We don't do this here in order to keep the UI
     * simple.
     */
    private class MediaBrowserListener extends MediaControllerCompat.Callback {
        @Override
        public void onPlaybackStateChanged(PlaybackStateCompat playbackState) {
            mIsPlaying = playbackState != null &&
                    playbackState.getState() == PlaybackStateCompat.STATE_PLAYING;

            if(playbackState != null){
                switch (playbackState.getState()) {
                    case PlaybackStateCompat.STATE_PLAYING:
                        buttonPlay.setImageResource(R.mipmap.ic_pause);
                        mProgressBar.setVisibility(View.GONE);
                        break;
                    case PlaybackStateCompat.STATE_PAUSED:
                        buttonPlay.setImageResource(R.mipmap.ic_play);
                        break;
                    case PlaybackStateCompat.STATE_BUFFERING:
                        break;
                    case PlaybackStateCompat.STATE_STOPPED:
                        buttonPlay.setImageResource(R.mipmap.ic_play);
                        break;
                    default:
                        break;
                }
            }
//            mMediaControlsImage.setPressed(mIsPlaying);
        }

        @Override
        public void onMetadataChanged(MediaMetadataCompat mediaMetadata) {
            if (mediaMetadata == null) {
                return;
            }
            if (mIsPlaying) {
                mTitleTextView.setText(
                        mediaMetadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE));
                mArtistTextView.setText(
                        mediaMetadata.getString(MediaMetadataCompat.METADATA_KEY_ARTIST));
                mAlbumArt.setImageBitmap(
                        mediaMetadata.getBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART));
                BlurImage.with(getApplicationContext())
                        .load(mediaMetadata.getBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART))
                        .intensity(blurPerc).Async(true).into(blurImage);
            }
            else{
                mTitleTextView.setText(getApplicationContext().getString(R.string.app_name));
                mArtistTextView.setText(getApplicationContext().getString(R.string.motto));
                mAlbumArt.setImageResource(R.drawable.logo);
                BlurImage.with(getApplicationContext())
                        .load(R.drawable.logo)
                        .intensity(blurPerc).Async(true).into(blurImage);
            }
        }

        @Override
        public void onSessionDestroyed() {
            super.onSessionDestroyed();
        }

        @Override
        public void onQueueChanged(List<MediaSessionCompat.QueueItem> queue) {
            super.onQueueChanged(queue);
        }
    }

    private void initializeUI() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        StatusBarUtil.setTransparent(MainActivity.this);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mTitleTextView = (TextView) findViewById(R.id.song_title);
        mArtistTextView = (TextView) findViewById(R.id.song_artist);
        mAlbumArt = (ImageView) findViewById(R.id.album_art);
//        mMediaControlsImage = (ImageView) findViewById(R.id.media_controls);

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.GONE);

        buttonPlay = findViewById(R.id.button_play);

        buttonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle params = new Bundle();
                params.putString("Tap", "play/pause");
                mFirebaseAnalytics.logEvent("tap_on_play_button", params);
                    if (mIsPlaying) {
                        buttonPlay.setImageResource(R.mipmap.ic_play);
                        mMediaBrowserHelper.getTransportControls().pause();
                    } else {
                        mProgressBar.setVisibility(View.VISIBLE);
                        if(ConnectionDetector.isConnected(view.getContext())){
                            buttonPlay.setImageResource(R.mipmap.ic_pause);
                            mMediaBrowserHelper.getTransportControls().play();
                        }
                        else{
                            mProgressBar.setVisibility(View.GONE);
                            ConnectionDetector.showNoConnectionError(view.getContext());
                        }
                    }
            }
        });

        buttonStop = findViewById(R.id.stopButton);

        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            buttonPlay.setImageResource(R.mipmap.ic_play);
            mMediaBrowserHelper.getTransportControls().stop();

            }
        });



        blurImage = findViewById(R.id.blurImage);
        BlurImage.with(getApplicationContext())
                .load(R.drawable.logo)
                .intensity(blurPerc)
                .Async(true)
                .into(blurImage);
    }

    @Override
    public void onStart() {
        super.onStart();
        mMediaBrowserHelper.onStart();
        Log.d(TAG, "On Start, IS PLAYING? " + mIsPlaying);
    }

    @Override
    public void onStop() {
        super.onStop();
//        mSeekBarAudio.disconnectController();
        mMediaBrowserHelper.onStop();
        Log.d(TAG, "On Stop, IS PLAYING? " + mIsPlaying);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_palinsesto) {
            Intent intent = new Intent(getApplicationContext(), ProgrammiActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_news) {
            Intent intent = new Intent(getApplicationContext(), BlogPostListActivity.class);
            startActivity(intent);
        } else if(id == R.id.nav_messaggi) {
            Intent intent = new Intent(getApplicationContext(), CommentActivity.class);
            startActivity(intent);
        } else if(id == R.id.nav_seguici){
            Intent intent = new Intent(getApplicationContext(), FollowUsActivity.class);
            startActivity(intent);
        } else if(id == R.id.nav_team){
            Intent intent = new Intent(getApplicationContext(), TeamActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_share) {
            String what = "";
            if (!mArtistTextView.getText().equals("BiPOP")) {
                what = mArtistTextView.getText()
                        + " - " + mTitleTextView.getText();
            }
            else{
                what = getString(R.string.share_diretta_string3);
            }
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT,
                    getString(R.string.share_diretta_string1)
                            + what
                            + getString(R.string.share_diretta_string2));
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        } else if (id == R.id.nav_settings) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }




}
