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

package com.radiobicocca.android.Service;

import android.app.Notification;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaBrowserServiceCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.radiobicocca.android.Common.ConnectionDetector;
import com.radiobicocca.android.Service.contentcatalogs.MusicLibrary;
import com.radiobicocca.android.Service.notifications.MediaNotificationManager;
import com.radiobicocca.android.Service.players.MediaPlayerAdapter;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class MusicService extends MediaBrowserServiceCompat {

    private static final String TAG = MusicService.class.getSimpleName();

    private MediaSessionCompat mSession;
    private MediaPlayerAdapter mPlayback;
    private MediaNotificationManager mMediaNotificationManager;
    private MediaSessionCallback mCallback;
    private boolean mServiceInStartedState;

    private Timer timer;
    @Override
    public void onCreate() {
        super.onCreate();

        // Create a new MediaSession.
        mSession = new MediaSessionCompat(this, "MusicService");
        mCallback = new MediaSessionCallback();
        mSession.setCallback(mCallback);
        mSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSessionCompat.FLAG_HANDLES_QUEUE_COMMANDS |
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        setSessionToken(mSession.getSessionToken());

        mMediaNotificationManager = new MediaNotificationManager(this);

        mPlayback = new MediaPlayerAdapter(this, new MediaPlayerListener());
        Log.d(TAG, "onCreate: MusicService creating MediaSession, and MediaNotificationManager");


        timer = new Timer();
        callAsynchronousTask(); //LP

    }
    // LP
    public void callAsynchronousTask() {
        final Handler handler = new Handler();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        if(ConnectionDetector.isConnected(getBaseContext())) {
                            try {

                                final MediaMetadataCompat.Builder builder = new MediaMetadataCompat.Builder();

                                String onAirMetaDataURL = "http://radiobicocca.it/pubblicazioni/OnAir.txt";
                                // Instantiate the RequestQueue.
                                final RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                                // Request a string response from the provided URL.
                                StringRequest stringRequest = new StringRequest(Request.Method.GET, onAirMetaDataURL,
                                        new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {
                                                String artista, titolo;
                                                if (response.toString().contains("-")) {
                                                    String splited[] = response.toString().split("-");
                                                    artista = splited[1];
                                                    titolo = splited[0];
                                                } else {
                                                    artista = "Radio Bicocca";
                                                    titolo = response.toString();
                                                }
                                                Log.d("SERVICE METADATA", response.toString());
                                                builder
                                                        .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, "stream")
                                                        .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, "Album")
                                                        .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, artista)
                                                        .putLong(MediaMetadataCompat.METADATA_KEY_DURATION,
                                                                TimeUnit.MILLISECONDS.convert(10000, TimeUnit.DAYS))
                                                        .putString(MediaMetadataCompat.METADATA_KEY_GENRE, "Genere")
                                                        .putString(
                                                                MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI,
                                                                "http://radiobicocca.it/pubblicazioni/OnAir.jpg")
                                                        .putString(
                                                                MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON_URI,
                                                                "http://radiobicocca.it/pubblicazioni/OnAir.jpg")
                                                        .putString(MediaMetadataCompat.METADATA_KEY_TITLE, titolo);
                                                final MediaMetadataCompat metadataWithoutBitmap = builder.build();


                                                new DownloadImageTask(new Listener() {
                                                    @Override
                                                    public void onImageDownloaded(Bitmap bitmap) {
                                                        MediaMetadataCompat.Builder builder2 = new MediaMetadataCompat.Builder();
                                                        for (String key :
                                                                new String[]{
                                                                        MediaMetadataCompat.METADATA_KEY_MEDIA_ID,
                                                                        MediaMetadataCompat.METADATA_KEY_ALBUM,
                                                                        MediaMetadataCompat.METADATA_KEY_ARTIST,
                                                                        MediaMetadataCompat.METADATA_KEY_GENRE,
                                                                        MediaMetadataCompat.METADATA_KEY_TITLE
                                                                }) {
                                                            builder2.putString(key, metadataWithoutBitmap.getString(key));
                                                        }
                                                        builder2.putLong(
                                                                MediaMetadataCompat.METADATA_KEY_DURATION,
                                                                metadataWithoutBitmap.getLong(MediaMetadataCompat.METADATA_KEY_DURATION));
                                                        builder2.putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, bitmap);
                                                        MediaMetadataCompat metadata = builder2.build();

                                                        mSession.setMetadata(metadata);
                                                        mPlayback.update(metadata);
                                                    }

                                                    @Override
                                                    public void onImageDownloadError() {
                                                        Log.d("IMAGE ERROR", "NON SCARICATA");
                                                    }
                                                }).execute("http://radiobicocca.it/pubblicazioni/OnAir.jpg");
                                            }
                                        }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                    }
                                });
                                // Add the request to the RequestQueue.
                                queue.add(stringRequest);


                            } catch (Exception e) {
//                                throw e;
                            }
                        }
                        else{
                            timer.cancel();
                            mPlayback.stop();
                            mSession.release();
                        }
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 0, 6500); //execute in every 7000 ms
    }

    public interface Listener {
        void onImageDownloaded(final Bitmap bitmap);
        void onImageDownloadError();
    }

    public class DownloadImageTask
            extends AsyncTask<String, Void, Bitmap> {
        Listener listener;

        public DownloadImageTask(final Listener listener) {
            this.listener = listener;
        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            // Logic to download an image from an URL
            final String url = urls[0];
            Bitmap bitmap = null;

            try {
                final InputStream inputStream = new URL(url).openStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
            } catch (final MalformedURLException malformedUrlException) {
                // Handle error
            } catch (final IOException ioException) {
                // Handle error
            }
            return bitmap;
        }
        @Override
        protected void onPostExecute(Bitmap downloadedBitmap) {
            // Download is done
            if (null != downloadedBitmap) {
                listener.onImageDownloaded(downloadedBitmap);
            } else {
                listener.onImageDownloadError();
            }
        }

    }
    //LP


    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        stopSelf();
    }

    @Override
    public void onDestroy() {
        mMediaNotificationManager.onDestroy();
        mPlayback.stop();
        mSession.release();
        timer.cancel();
        Log.d(TAG, "onDestroy: MediaPlayerAdapter stopped, and MediaSession released");
    }

    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName,
                                 int clientUid,
                                 Bundle rootHints) {
        return new BrowserRoot(MusicLibrary.getRoot(), null);
    }

    @Override
    public void onLoadChildren(
            @NonNull final String parentMediaId,
            @NonNull final Result<List<MediaBrowserCompat.MediaItem>> result) {
        result.sendResult(MusicLibrary.getMediaItems());
    }

    // MediaSession Callback: Transport Controls -> MediaPlayerAdapter
    public class MediaSessionCallback extends MediaSessionCompat.Callback {
        private final List<MediaSessionCompat.QueueItem> mPlaylist = new ArrayList<>();
        private int mQueueIndex = -1;
        private MediaMetadataCompat mPreparedMedia;

        @Override
        public void onAddQueueItem(MediaDescriptionCompat description) {
            mPlaylist.add(new MediaSessionCompat.QueueItem(description, description.hashCode()));
            mQueueIndex = (mQueueIndex == -1) ? 0 : mQueueIndex;
            mSession.setQueue(mPlaylist);
        }

        @Override
        public void onRemoveQueueItem(MediaDescriptionCompat description) {
            mPlaylist.remove(new MediaSessionCompat.QueueItem(description, description.hashCode()));
            mQueueIndex = (mPlaylist.isEmpty()) ? -1 : mQueueIndex;
            mSession.setQueue(mPlaylist);
        }

        @Override
        public void onPrepare() {

            if (mQueueIndex < 0 && mPlaylist.isEmpty()) {
                // Nothing to play.
                return;
            }

            final String mediaId = mPlaylist.get(mQueueIndex).getDescription().getMediaId();
            mPreparedMedia = MusicLibrary.getMetadata(MusicService.this, mediaId);


            mSession.setMetadata(mPreparedMedia);

            if (!mSession.isActive()) {
                mSession.setActive(true);
            }
        }

        @Override
        public void onPlay() {

            if (!isReadyToPlay()) {
                // Nothing to play.
                return;
            }

            if (mPreparedMedia == null) {
                onPrepare();
            }

            mPlayback.playFromMedia(mPreparedMedia);
            Log.d(TAG, "onPlayFromMediaId: MediaSession active");
        }

        @Override
        public void onPause() {
            mPlayback.pause();
        }

        @Override
        public void onStop() {
            mPlayback.stop();
            mSession.setActive(false);
        }

        @Override
        public void onSkipToNext() {
            mQueueIndex = (++mQueueIndex % mPlaylist.size());
            mPreparedMedia = null;
            onPlay();
        }

        @Override
        public void onSkipToPrevious() {
            mQueueIndex = mQueueIndex > 0 ? mQueueIndex - 1 : mPlaylist.size() - 1;
            mPreparedMedia = null;
            onPlay();
        }

        @Override
        public void onSeekTo(long pos) {
            mPlayback.seekTo(pos);
        }

        private boolean isReadyToPlay() {
            return (!mPlaylist.isEmpty());
        }
    }

    // MediaPlayerAdapter Callback: MediaPlayerAdapter state -> MusicService.
    public class MediaPlayerListener extends PlaybackInfoListener {

        private final ServiceManager mServiceManager;

        MediaPlayerListener() {
            mServiceManager = new ServiceManager();
        }

        @Override
        public void onPlaybackStateChange(PlaybackStateCompat state) {
            // Report the state to the MediaSession.
            mSession.setPlaybackState(state);

            // Manage the started state of this service.
            switch (state.getState()) {
                case PlaybackStateCompat.STATE_PLAYING:
                    mServiceManager.moveServiceToStartedState(state);
                    break;
                case PlaybackStateCompat.STATE_PAUSED:
                    mServiceManager.updateNotificationForPause(state);
                    break;
                case PlaybackStateCompat.STATE_STOPPED:
                    mServiceManager.moveServiceOutOfStartedState(state);
                    break;
            }
        }

        class ServiceManager {

            private void moveServiceToStartedState(PlaybackStateCompat state) {
                Notification notification =
                        mMediaNotificationManager.getNotification(
                                mPlayback.getCurrentMedia(), state, getSessionToken());

                if (!mServiceInStartedState) {
                    ContextCompat.startForegroundService(
                            MusicService.this,
                            new Intent(MusicService.this, MusicService.class));
                    mServiceInStartedState = true;
                }

                startForeground(MediaNotificationManager.NOTIFICATION_ID, notification);
            }

            private void updateNotificationForPause(PlaybackStateCompat state) {
                stopForeground(false);
                Notification notification =
                        mMediaNotificationManager.getNotification(
                                mPlayback.getCurrentMedia(), state, getSessionToken());
                mMediaNotificationManager.getNotificationManager()
                        .notify(MediaNotificationManager.NOTIFICATION_ID, notification);
            }

            private void moveServiceOutOfStartedState(PlaybackStateCompat state) {
                stopForeground(true);
                stopSelf();
                mServiceInStartedState = false;
            }
        }

    }

}