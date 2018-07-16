package com.example.matjeusz.opencv;


import android.content.Intent;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;

import java.util.Calendar;
import java.util.Date;


public class MainActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
        System.loadLibrary("opencv_java3");
    }


    private static String TAG = "MainActivity";
    JavaCameraView javaCameraView;
    Mat mRgba,mGray,save;
    int Corner1x, Corner1y, Corner2x,Corner2y;
    Scalar colorR,colorG,color;
   // public native int add();
  //  public native int add1();
  //  public native int add2();
  //  public native int add3();
    String filename,filename1;
    boolean bool = false;



    BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status){
                case BaseLoaderCallback.SUCCESS:
                    javaCameraView.enableView();
                    break;
                    default:
                        super.onManagerConnected(status);
                        break;
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        javaCameraView = (JavaCameraView)findViewById(R.id.java_camera_view);
        javaCameraView.setVisibility(View.VISIBLE);
        javaCameraView.setCvCameraViewListener(this);

    }


    @Override
    protected void onPause(){
        super.onPause();
        if(javaCameraView!=null)
        {
            javaCameraView.disableView();
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        if(javaCameraView!=null)
        {
            javaCameraView.disableView();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (OpenCVLoader.initDebug())
        {
            Log.i(TAG,"OpenCV loaded succesfully");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }else
        {
            Log.i(TAG,"OpenCV notloaded" );
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_4_0,this,mLoaderCallback);
        }


    }


    @Override
    public void onCameraViewStarted(int width, int height) {

        mRgba = new Mat(height,width, CvType.CV_8UC4);
        mGray = new Mat();
        colorR = new Scalar(255,0,0);
        colorG = new Scalar(0,255,0);
        color = new Scalar(0,0,0);


    }

    @Override
    public void onCameraViewStopped() {
        mRgba.release();

    }



    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();
       // OpenCVNativClass.convertGray(mRgba.getNativeObjAddr(),mGray.getNativeObjAddr());


       // Corner1x = add();
     //   Corner1y = add1();
     //   Corner2x = add2();
     //   Corner2y = add3();


        Imgproc.rectangle(mRgba,new Point(200,20),new  Point (1180,690),color);

        double distance =  Math.hypot(Corner1x-Corner2x,Corner1y-Corner2y);
        double distance1 = Math.hypot(200-Corner1x,20-Corner1y);



        Log.i("distance",""+distance);
        Log.i("distance1",""+distance1);

        if(distance > 1120 && distance<1130 && distance1>645 && distance1<665)
        {
          color = colorG;
          save=mRgba;
          Date currentTime = Calendar.getInstance().getTime();
          File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES+"/PLOTER/");
          filename1 = currentTime+".jpg";
          File file = new File(path,filename1);
          filename=filename1;
          filename = file.toString();
          bool = Imgcodecs.imwrite(filename,save);


            Intent intent = new Intent(this,Zdjecie.class);
            intent.putExtra("cx1",Corner1x);
            intent.putExtra("cy1",Corner1y);
            intent.putExtra("cx2",Corner2x);
            intent.putExtra("cy2",Corner2y);
            intent.putExtra("nazwa",filename1);
            this.startActivity(intent);
        }
        else color = colorR;

        return mRgba;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int action = event.getAction();
        int keyCode = event.getKeyCode();

        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                if (action == KeyEvent.ACTION_UP) {
                    color = colorG;
                    save=mRgba;
                    Date currentTime = Calendar.getInstance().getTime();
                    File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES+"/PLOTER/");
                    filename1 = currentTime+".jpg";
                    File file = new File(path,filename1);
                    filename=filename1;
                    filename = file.toString();
                    bool = Imgcodecs.imwrite(filename,save);


                    Intent intent = new Intent(this,Zdjecie.class);
                    intent.putExtra("cx1",200);
                    intent.putExtra("cy1",20);
                    intent.putExtra("cx2",1180);
                    intent.putExtra("cy2",690);
                    intent.putExtra("nazwa",filename1);
                    this.startActivity(intent);
                }
                return true;
            case KeyEvent.ACTION_DOWN:
                if (action == KeyEvent.ACTION_DOWN) {
                }
                return true;
            default:
                return super.dispatchKeyEvent(event);
        }
    }


}





