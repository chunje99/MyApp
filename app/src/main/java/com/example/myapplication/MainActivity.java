package com.example.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.PermissionRequest;
import android.widget.VideoView;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
    };

    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static void verifyPermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    private SurfaceHolder.Callback surfaceListener = new SurfaceHolder.Callback() {

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            // TODO Auto-generated method stub
            //camera.release();
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            //// TODO Auto-generated method stub
            //camera = Camera.open();
            //try {
            //   camera.setPreviewDisplay(holder);
            //} catch (IOException e) {
            //    // TODO Auto-generated catch block
            //    e.printStackTrace();
            //}

        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            //// TODO Auto-generated method stub
            //Camera.Parameters parameters = camera.getParameters();
            //parameters.setPreviewSize(width, height);
            //camera.startPreview();
        }
    };

    MediaRecorder mRecorder = null;
    MediaPlayer mPlayer = null;
    private SurfaceView mPreview;
    private SurfaceHolder mHolder;
    VideoView mVideo;
    boolean mIsStart = false;
    String Path = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        verifyPermissions(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //mPreview = (SurfaceView)findViewById(R.id.surface);
        mPreview = findViewById(R.id.surface);
        mHolder = mPreview.getHolder();
        //mHolder.addCallback(this);
        mHolder.addCallback(surfaceListener);
        //mVideo = (VideoView)findViewById(R.id.videoview);
        mVideo = findViewById(R.id.videoview);

        mPlayer = new MediaPlayer();
        try{
            //Uri ur = Uri.parse("https://sample-videos.com/audio/mp3/wave.mp3");
            //Uri ur = Uri.parse("http://www.hochmuth.com/mp3/Haydn_Cello_Concerto_D-1.mp3");
            //Log.d("button","url");
            //mPlayer.setDataSource("https://sample-videos.com/audio/mp3/wave.mp3");
            //mPlayer.setDataSource("http://www.hochmuth.com/mp3/Haydn_Cello_Concerto_D-1.mp3");
            //mPlayer.setDataSource(this, ur);
            //mPlayer.setDataSource(getApplicationContext(), ur);
        } catch(Exception e) {
            Log.d("error", e.getMessage());
        }
        String sd = Environment.getExternalStorageDirectory().getAbsolutePath();
        Path = sd + "/recvideo.mp4";
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPreview.setVisibility(View.VISIBLE);
                mVideo.setVisibility(View.VISIBLE);
                if( mIsStart == false ) {
                    if (mRecorder == null) {
                        mRecorder = new MediaRecorder();
                        Log.d("start","new MediaRecorder");
                    } else {
                        mRecorder.reset();
                        Log.d("start", "reset()");
                    }
                    Log.d("start", Path);
                    mRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
                    Log.d("start", "setVideoSource");
                    mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                    Log.d("start", "setAudioSource");
                    mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                    Log.d("start","setOutputFormat");
                    mRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);
                    mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                    mRecorder.setOutputFile(Path);
                    mRecorder.setPreviewDisplay(mHolder.getSurface());
                    try {
                        mRecorder.prepare();
                        mRecorder.start();
                    } catch (IllegalStateException e) {
                        Log.d("start", e.getMessage());
                        return;
                    } catch (IOException e) {
                        Log.d("start", e.getMessage());
                        return;
                    } catch (Exception e) {
                        Log.d("start", e.getMessage());
                        return;
                    }
                    mIsStart = true;
                } else {
                    Log.d("start","release");
                    mRecorder.stop();
                    mRecorder.release();
                    mRecorder = null;
                    mIsStart = false;
                }
                /*
                Log.d("button","onClick before");
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                Log.d("button","onClick after");

                try{
                    mPlayer.prepare();
                    mPlayer.start();
                } catch(Exception e) {
                    Log.d("error", e.getMessage());
                }
                */
            }
        });
        FloatingActionButton fab2 = findViewById(R.id.fab2);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("fab2", "stop");
                mPreview.setVisibility(View.INVISIBLE);
                mVideo.setVisibility(View.INVISIBLE);
                mVideo.setVideoPath(Path);
                mVideo.start();
                Log.d("fab2", "startVideo");
                /*
                Log.d("button","onClick before");
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                Log.d("button","onClick after");

                try{
                    mPlayer.stop();
                } catch(Exception e) {
                    Log.d("error", e.getMessage());
                }
                */
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onDestroy() {
        Log.d("onDestroy", "onDestroy");
        super.onDestroy();
        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }
        Log.d("surface", "onDestroy()");
    }
}
