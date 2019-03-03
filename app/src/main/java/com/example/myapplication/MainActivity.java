package com.example.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
//import android.graphics.Camera;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
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

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import static android.os.ParcelFileDescriptor.MODE_CREATE;
import static android.os.ParcelFileDescriptor.MODE_READ_ONLY;
import static android.os.ParcelFileDescriptor.MODE_READ_WRITE;
import static android.os.ParcelFileDescriptor.MODE_TRUNCATE;
import static android.os.ParcelFileDescriptor.MODE_WRITE_ONLY;

public class MainActivity extends AppCompatActivity {

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.INTERNET,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    /**
     * Checks if the app has permission to write to device storage
     * <p>
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
    Camera mCamera = null;
    Socket mSocket = null;
    ParcelFileDescriptor mFileDescriptor = null;
    ParcelFileDescriptor mPFD = null;
    long offset = 0;

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

        String sd = Environment.getExternalStorageDirectory().getAbsolutePath();
        Path = sd + "/recvideo.mp4";

        //mSocket = new Socket();
        //Thread conThread = new Thread(null, conSocket, "conSocket");
        Thread conThread = new Thread(conSocket);
        //Thread conThread = new Thread(new ConnectionSocket(mSocket));
        conThread.start();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Thread sendThread = new Thread(sendMsg);
                //sendThread.start();

                mPreview.setVisibility(View.VISIBLE);
                mVideo.setVisibility(View.VISIBLE);
                //Thread conThread = new Thread(null, conSocket, "conSocket");
                //conThread.start();
                if (mCamera == null) {
                    //mCamera = Camera.open(0);
                    //mCamera.startPreview();
                    //mCamera.setDisplayOrientation(180);
                    //mCamera.unlock();
                }
                if (mIsStart == false) {
                    if (mRecorder == null) {
                        mRecorder = new MediaRecorder();
                        Log.d("start", "new MediaRecorder");
                    } else {
                        mRecorder.reset();
                        Log.d("start", "reset()");
                    }
                    Log.d("start", Path);
                    //Thread startThread = new Thread(new startRecording(mRecorder, Path, mHolder, mCamera, mFileDescriptor));
                    Thread startThread = new Thread(startRecorde);
                    Log.i("Media", "start Thread Created");
                    startThread.start();

                    mIsStart = true;
                } else {
                    Log.d("start", "stop");
                    //mRecorder.stop();
                    Thread stopThread = new Thread(new stopRecording(mRecorder));
                    Log.i("Media", "stop Thread Created");
                    stopThread.start();
                    Log.i("Media", "stop Recording");
                    Log.d("start", "release");
                    //mRecorder.release();
                    Log.d("start", "recorder null");
                    //mRecorder = null;
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
                if (mCamera != null) {
                    Log.i("Media", "stop camera");
                    mCamera.stopPreview();
                    mCamera.release();
                    mCamera = null;
                }
                Thread stopThread = new Thread(new stopRecording(mRecorder));
                Log.i("Media", "stop Thread Created");
                stopThread.start();
                mRecorder = null;
                Log.d("fab2", "stop");
                mPreview.setVisibility(View.INVISIBLE);
                mVideo.setVisibility(View.INVISIBLE);
                mVideo.setVideoPath(Path);
                mVideo.start();
                Log.d("fab2", "startVideo");

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

    public Runnable conSocket = new Runnable() {
        public void run() {
            Log.d("conSocket", "in");
            try {
                mSocket = new Socket("192.168.0.16", 8888);

                mFileDescriptor = ParcelFileDescriptor.fromSocket(mSocket);
                Log.d("start", "Connection Socket");
                //mSocket.close();
            } catch (IOException e) {
                Log.d("start", e.getMessage());
            }
            Log.d("conSocket", "out");
        }
    };
    public Runnable sendMsg = new Runnable() {
        public void run() {
            Log.d("sendMsg", "in");
            try {
                //mFileDescriptor = ParcelFileDescriptor.fromSocket(mSocket);
                DataOutputStream dOut = new DataOutputStream(mSocket.getOutputStream());
                dOut.writeUTF("This is the first type of message.");
                dOut.flush();
                FileOutputStream anotFileInputStream = new FileOutputStream(mFileDescriptor.getFileDescriptor());
                String aa = "abcdef";
                anotFileInputStream.write(aa.getBytes());
                Log.d("start", "sendMsg  Socket");
            } catch (IOException e) {
                Log.d("start", e.getMessage());
            }
            Log.d("sendMsg", "out");
        }
    };
    public Runnable readData = new Runnable() {
        public void run() {
            Log.d("readData", "in");
            try {
                int i = 0;
                FileOutputStream anotFileInputStream = new FileOutputStream(mFileDescriptor.getFileDescriptor());
                while (mRecorder != null) {
                    File file = new File(Path);
                    FileReader filereader = new FileReader(file);
                    filereader.skip(0);
                    while ((i = filereader.read()) <= -1) {
                        anotFileInputStream.write((char) i);
                        offset++;
                    }
                    filereader.close();
                    /*
                    ParcelFileDescriptor pfd = ParcelFileDescriptor.open(new File(Path), MODE_READ_ONLY);
                    FileInputStream fis2 = new FileInputStream(pfd.getFileDescriptor());
                    fis2.skip(offset);
                    while ((i = fis2.read()) <= 0) {
                        anotFileInputStream.write((char) i);
                        offset++;
                    }
                    fis2.close();
                    pfd.close();
                    */
                    Log.d("readData", "offset :" + String.valueOf(offset));
                    Thread.sleep(100);
                }
            } catch(InterruptedException e){
                Log.d("start", e.getMessage());
            } catch (IOException e) {
                Log.d("start", e.getMessage());
            }
            Log.d("readData", "out");
        }
    };
    public MediaRecorder.OnInfoListener recorderInfoListener = new MediaRecorder.OnInfoListener() {
        @Override
        public void onInfo(MediaRecorder mr, int what, int extra) {
            switch (what) {
                case MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED:
                    Log.d("INFO", "MAX_DURATION");
                    break;
                case MediaRecorder.MEDIA_RECORDER_INFO_MAX_FILESIZE_APPROACHING:
                    Log.d("INFO", "MAX_FILESIZE_APPROCH");
                    break;
                case MediaRecorder.MEDIA_RECORDER_INFO_MAX_FILESIZE_REACHED:
                    Log.d("INFO", "MAX_FILESIZE_REACHED");
                    break;
                default:
                    Log.d("INFO", "???????");
                    break;
            }
        }
    };
    public Runnable startRecorde = new Runnable() {
        public void run() {
            Log.d("startRecorde", "in");

            //this.mRecorder.setCamera(this.mCamera);
            mRecorder.setOnInfoListener(recorderInfoListener);
            mRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            //this.mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_2_TS);
            mRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            mRecorder.setVideoSize(1920, 1080);
            mRecorder.setMaxFileSize(1000000);
            //this.mRecorder.setVideoEncodingBitRate(1024);
            //this.mRecorder.setOrientationHint(180);
            //this.mRecorder.setOutputFile(mPath);
            //create a socket connection to another device
            //this.mRecorder.setOutputFile(this.mFileDescriptor.getFileDescriptor());

            mRecorder.setPreviewDisplay(mHolder.getSurface());

            try {
                //Thread readDataThread = new Thread(readData);
                //readDataThread.start();

                //ParcelFileDescriptor[] pipe = ParcelFileDescriptor.createPipe();
                //ParcelFileDescriptor pfd = ParcelFileDescriptor.open(new File(Path), MODE_READ_WRITE | MODE_TRUNCATE | MODE_CREATE);
                mPFD = ParcelFileDescriptor.open(new File(Path), MODE_READ_WRITE | MODE_TRUNCATE | MODE_CREATE);
                //ParcelFileDescriptor readSide = pipe[0];
                //ParcelFileDescriptor writeSide = pipe[1];
                //this.mRecorder.setOutputFile(readSide.getFileDescriptor());
                mRecorder.setOutputFile(mPFD.getFileDescriptor());
                //this.mRecorder.setOutputFile(mPath);

                mRecorder.prepare();
                mRecorder.start();

                Thread readThread = new Thread(readData);
                readThread.start();

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
            Log.d("startRecorde", "out");
        }
    };
}

class startRecording implements Runnable {
    private MediaRecorder mRecorder;
    private String mPath;
    private SurfaceHolder mSurfaceHolder;
    private Camera mCamera;
    private ParcelFileDescriptor mFileDescriptor;
    private FileDescriptor outputPipe = null;
    private FileDescriptor inputPipe = null;


    public startRecording(MediaRecorder recorder2, String path,
                          SurfaceHolder surfaceHolder, Camera camera,
                          ParcelFileDescriptor fileDescriptor) {
        Log.i("Media", "Stop in Cos");
        // TODO Auto-generated constructor stub
        try {
            this.mRecorder = recorder2;
            this.mPath = path;
            this.mSurfaceHolder = surfaceHolder;
            this.mCamera = camera;
            this.mFileDescriptor = fileDescriptor;
        } catch (Exception e) {
            Log.i("Media", "Start out  Cos" + e.getMessage());
        }

    }

    public void run() {
        Log.i("Media", "Start in RUN");
        startRecording();
        Log.i("Media", "Start out of RUN");

    }

    public void startRecording() {


        //this.mRecorder.setCamera(this.mCamera);
        this.mRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        this.mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        //this.mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        this.mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_2_TS);
        this.mRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        this.mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        this.mRecorder.setVideoSize(1920, 1080);
        //this.mRecorder.setVideoEncodingBitRate(1024);
        //this.mRecorder.setOrientationHint(180);
        //this.mRecorder.setOutputFile(mPath);
        //create a socket connection to another device
        //this.mRecorder.setOutputFile(this.mFileDescriptor.getFileDescriptor());

        this.mRecorder.setPreviewDisplay(mSurfaceHolder.getSurface());

        try {
            //Thread readDataThread = new Thread(readData);
            //readDataThread.start();

            //ParcelFileDescriptor[] pipe = ParcelFileDescriptor.createPipe();
            ParcelFileDescriptor pfd = ParcelFileDescriptor.open(new File(mPath), MODE_READ_WRITE | MODE_TRUNCATE | MODE_CREATE);
            //ParcelFileDescriptor readSide = pipe[0];
            //ParcelFileDescriptor writeSide = pipe[1];
            //this.mRecorder.setOutputFile(readSide.getFileDescriptor());
            this.mRecorder.setOutputFile(pfd.getFileDescriptor());
            //this.mRecorder.setOutputFile(mPath);

            this.mRecorder.prepare();
            this.mRecorder.start();

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
    }

    FileDescriptor getPipeFD() {
        final String FUNCTION = "getPipeFD";
        try {
            ParcelFileDescriptor[] pipe = ParcelFileDescriptor.createPipe();
            inputPipe = pipe[0].getFileDescriptor();
            outputPipe = pipe[1].getFileDescriptor();
        } catch (Exception e) {
            Log.e("TAG", FUNCTION + " : " + e.getMessage());
        }

        return outputPipe;
    }
}

class stopRecording implements Runnable {
    private MediaRecorder recorder;

    public stopRecording(MediaRecorder recorder2) {
        Log.i("Media", "Stop in Cos");
        // TODO Auto-generated constructor stub
        try {
            this.recorder = recorder2;
        } catch (Exception e) {
            Log.i("Media", "Stop out  Cos" + e.getMessage());
        }

    }

    public void run() {
        Log.i("Media", "Stop in RUN");
        stopRecording();
        Log.i("Media", "Stop out of RUN");

    }

    public void stopRecording() {
        try {
            this.recorder.stop();
            this.recorder.release();
        } catch (RuntimeException ex) {
            //Ignore
            Log.d("stopRecording", ex.getMessage());
        }
    }
}

class ConnectionSocket implements Runnable {
    private Socket mSocket;

    public ConnectionSocket(Socket socket) {
        Log.i("ConnectionSocket", "in Cos");
        // TODO Auto-generated constructor stub
        try {
            this.mSocket = socket;
        } catch (Exception e) {
            Log.i("Media", "out  Cos" + e.getMessage());
        }

    }

    public void run() {
        Log.i("Media", "Stop in RUN");
        startCon();
        Log.i("Media", "Stop out of RUN");

    }

    public void startCon() {
        try {
            SocketAddress sock_addr = new InetSocketAddress("192.168.0.16", 8888);
            this.mSocket.connect(sock_addr);

        } catch (IOException ex) {
            //Ignore
            Log.d("stopRecording", ex.getMessage());
        }
    }
}