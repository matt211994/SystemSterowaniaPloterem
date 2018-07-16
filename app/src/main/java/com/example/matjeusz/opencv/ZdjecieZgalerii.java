package com.example.matjeusz.opencv;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewDebug;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.zgkxzx.modbus4And.requset.ModbusReq;
import com.zgkxzx.modbus4And.requset.OnRequestBack;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.Arrays;


public class ZdjecieZgalerii extends AppCompatActivity {

    Bitmap temp1, bmp;
    ImageView image;
    Button Send,SetStartPosition,Draw;
    SeekBar seekBar;
    TextView postep,ilosc;
    int precision=10, temp=1;
    int blackPiksels=0;
    Handler FlagStartHandler = new Handler();
    boolean flagStart=false;
    boolean isChecked;
    CheckBox fee_checkbox;





    static {
        System.loadLibrary("native-lib");
    }


    String TAG = "Wybrane zdjecie";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zdjecie_zgalerii);
        findViewById(R.id.loadingPanel).setVisibility(View.GONE);
        Send = findViewById(R.id.PrinitngZdj);
        byte[] byteArray = getIntent().getByteArrayExtra("bitmap");
        bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        image = findViewById(R.id.imageView3);
        seekBar = findViewById(R.id.precision);
        postep = findViewById(R.id.precisionText);
        SetStartPosition =  findViewById(R.id.StartPosition);
        Draw = findViewById(R.id.Draw);
        fee_checkbox = (CheckBox)findViewById(R.id.findLine);
        ilosc=findViewById(R.id.textView4);
        temp1 = bmp.copy(Bitmap.Config.ARGB_8888, true);
        Log.i("bitmap", "kopie udana");
        BitmapToMat();
        findBlackPixel();
        sendResolution();

      fee_checkbox.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              if(fee_checkbox.isChecked())
              {
                  isChecked=true;
              }else
                  isChecked=false;
          }
      });

        SetStartPosition.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        SetStartPosition(view);

                        return true;

                    case MotionEvent.ACTION_UP:
                        nSetStartPosition(view);
                        return true;
                }

                return false;
            }
        });

        Draw.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        Draw(view);

                        return true;

                    case MotionEvent.ACTION_UP:
                        nDraw(view);
                        return true;
                }

                return false;
            }
        });


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                temp = i;
                if (temp==0)
                {
                    precision = 10;
                }
                if (temp == 1) {
                    precision = 6;
                }
                if (temp == 2) {
                    precision = 4;
                }
                //wysyłanie szybkości do PLC

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                postep.setText(temp+1 + "/" + 3);
                //wysyłanie szybkości do PLC

            }
        });

    }



    public void BitmapToMat() {
        Mat mat = new Mat(temp1.getHeight(), temp1.getWidth(), CvType.CV_8UC4);
        Utils.bitmapToMat(temp1, mat);
        Imgproc.cvtColor(mat, mat, Imgproc.COLOR_BGR2GRAY);
        Imgproc.adaptiveThreshold(mat, mat, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY, 15, 20);
        Utils.matToBitmap(mat, temp1);
        Drawable d = new BitmapDrawable(temp1);
        image.setBackground(d);
    }


    public void sendResolution() {
        ModbusReq.getInstance().writeRegister(new OnRequestBack<String>() {
            @Override
            public void onSuccess(String s) {
                Log.e(TAG, "writeRegister on Success" + s);
            }

            @Override
            public void onFailed(String s) {
                Log.e(TAG, "writeRegister onFailed" + s);
            }
        }, 1, 2, temp1.getWidth());

        ModbusReq.getInstance().writeRegister(new OnRequestBack<String>() {
            @Override
            public void onSuccess(String s) {
                Log.e(TAG, "writeRegister on Success" + s);
            }

            @Override
            public void onFailed(String s) {
                Log.e(TAG, "writeRegister onFailed" + s);
            }
        }, 1, 3, temp1.getHeight());



    }

    public void findBlackPixel() {
        final int[][] tab = new int[temp1.getWidth()][temp1.getHeight()];
        int i, j;
        for (i = 10; i < temp1.getWidth() - 10; i++) {
            for (j = 10; j < temp1.getHeight() - 10; j++) {
                int p = temp1.getPixel(i, j);
                int r = Color.red(p);
                int g = Color.green(p);
                int b = Color.blue(p);
                if (r == 255 && g == 255 && b == 255) {
                } else {
                    blackPiksels++;
                    tab[i][j] = 1;
                    j = temp1.getHeight() - 10;
                }
            }
        }

        Send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findBlackPixel();
                if(isChecked==true) {
                    //Hough(tab);
                    findLines(tab);
                }
                SendPixelsToPLC(tab);
            }
        });

    }

  /*  public void Hough (int[][] tab)
    {
        Mat mat = new Mat(temp1.getHeight(), temp1.getWidth(), CvType.CV_8UC4);
        Utils.bitmapToMat(temp1, mat);
        Mat lines = new Mat(); // will hold the results of the detection
        Imgproc.HoughLines(mat,lines,1,Math.PI/180,150,0,0);
        for (int x = 0; x < lines.rows(); x++) {
            double rho = lines.get(x, 0)[0],
                    theta = lines.get(x, 0)[1];
            double a = Math.cos(theta), b = Math.sin(theta);
            double x0 = a*rho, y0 = b*rho;
            Point pt1 = new Point(Math.round(x0 + 1000*(-b)), Math.round(y0 + 1000*(a)));
            Point pt2 = new Point(Math.round(x0 - 1000*(-b)), Math.round(y0 - 1000*(a)));
            tab[(int)pt1.x][(int)pt1.y]=1;
            tab[(int)pt2.x][(int)pt2.y]=1;
            Imgproc.line(mat, pt1, pt2, new Scalar(0, 0, 255), 3, Imgproc.LINE_AA, 0);
        }
    }
*/
    public void findLines(int[][] tab)
    {
        int i,j,k=0;
        double distancePrevious,distanceNext;
        int pixeli[] = new int[1000];
        int pixelj[]= new int[1000];


        for (i=0;i<temp1.getWidth();i++)
        {
            for (j = 0; j < temp1.getHeight()-1; j++)
            {
                if (tab[i][j]==1) {
                    pixeli[k] = i;
                    pixelj[k] = j;
                    k++;
                }
            }
        }
        k=0;
        for (i=0;i<temp1.getWidth();i++)
        {
            for (j=0;j<temp1.getHeight();j++)
            {
                if(tab[i][j]==1) {
                    distancePrevious = Math.hypot((pixeli[k+1] - pixeli[k]),(pixelj[k+1] - pixelj[k]));
                    distanceNext = Math.hypot(pixeli[k + 2] - pixeli[k + 1],pixelj[k + 2] - pixelj[k + 1]);
                    k++;
                      Log.i("distance","distancePrevious"+distancePrevious);
                      Log.i("distance","distanceNext"+distanceNext);
                      Log.i("distance","distance"+Math.abs(distanceNext - distancePrevious));
                    if ((Math.abs(distanceNext - distancePrevious)) <0.41 ) {
                        tab[i][j] = 0;
                    }
                }
            }
        }
    }


    public void SendPixelsToPLC(final int[][] x) {
        int i, j;
        int k=0;
        int l=0;
        int pixeliPrevious=0,pixeliNext=0,pixeljPrevious=0,pixeljNext=0;
        cleanRegisters();
        ModbusReq.getInstance().writeRegister(new OnRequestBack<String>() {
            @Override
            public void onSuccess(String s) {
                Log.e(TAG, "writeRegister on Success" + s);
            }

            @Override
            public void onFailed(String s) {
                Log.e(TAG, "writeRegister onFailed" + s);
            }
        }, 1, 4, blackPiksels/precision);

        Bitmap tempCheck;
        tempCheck = Bitmap.createBitmap(temp1.getWidth(), temp1.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(tempCheck);
        Paint p = new Paint();
        p.setColor(Color.rgb(255,0,0));
        for (i = 0; i < temp1.getWidth(); i = i + precision) {
            for (j = 0; j < temp1.getHeight(); j++) {

                if (x[i][j] == 1) {
                    if (l==1)
                    {
                        pixeliNext=i;
                        pixeljNext=j;
                       c.drawLine(pixeliPrevious,pixeljPrevious,pixeliNext,pixeljNext,p);
                    }
                    tempCheck.setPixel(i, j, Color.rgb(255, 0, 0));
                    pixeliPrevious=i;
                    pixeljPrevious=j;
                    l=1;
                    Log.d(TAG, "poczatek ifa");
                    ModbusReq.getInstance().writeRegister(new OnRequestBack<String>() {
                        @Override
                        public void onSuccess(String s) {
                            Log.e(TAG, "writeRegister on Success 1" + s);
                        }

                        @Override
                        public void onFailed(String s) {
                            Log.e(TAG, "writeRegister onFailed" + s);
                        }
                    }, 1, 500+k, i);

                    ModbusReq.getInstance().writeRegister(new OnRequestBack<String>() {
                        @Override
                        public void onSuccess(String s) {
                            Log.e(TAG, "writeRegister on Success 2" + s);
                        }

                        @Override
                        public void onFailed(String s) {
                            Log.e(TAG, "writeRegister onFailed" + s);
                        }
                    }, 1, 800+k, j);
                    k++;
                }
            }
        }
        ilosc.setText("Ilość pikseli: "+k);
        l=0;
        Drawable d = new BitmapDrawable(tempCheck);
        image.setBackground(d);
        Toast.makeText(getApplicationContext(),"Wysłano dane do PLC",Toast.LENGTH_LONG).show();
    }

    public void SetStartPosition(View view)
    {
        ModbusReq.getInstance().writeRegister(new OnRequestBack<String>() {
            @Override
            public void onSuccess(String s) {
                Log.e(TAG, "writeCoil onSuccess " + s);
            }

            @Override
            public void onFailed(String msg) {
                Log.e(TAG, "writeCoil onFailed " + msg);
            }
        }, 1, 14, 1);

      findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
      FlagStartHandler.postDelayed(flagStartPosition,0);
      FlagStartHandler.postDelayed(CheckFlagStart,0);
      Send.setEnabled(false);
      SetStartPosition.setEnabled(false);
      Draw.setEnabled(false);
        ///////////////////sprawdzić które rejestry wolne////////////////////////////
    }

    public void nSetStartPosition(View view)
    {
        ModbusReq.getInstance().writeRegister(new OnRequestBack<String>() {
            @Override
            public void onSuccess(String s) {
                Log.e(TAG, "writeCoil onSuccess " + s);
            }

            @Override
            public void onFailed(String msg) {
                Log.e(TAG, "writeCoil onFailed " + msg);
            }
        }, 1, 14, 0);
        ///////////////////sprawdzić które rejestry wolne////////////////////////////
    }

    public void Draw(View view)
    {
        ModbusReq.getInstance().writeRegister(new OnRequestBack<String>() {
            @Override
            public void onSuccess(String s) {
                Log.e(TAG, "writeCoil onSuccess " + s);
            }

            @Override
            public void onFailed(String msg) {
                Log.e(TAG, "writeCoil onFailed " + msg);
            }
        }, 1, 15, 1);
        ///////////////////sprawdzić które rejestry wolne////////////////////////////
    }

    public void nDraw(View view)
    {
        ModbusReq.getInstance().writeRegister(new OnRequestBack<String>() {
            @Override
            public void onSuccess(String s) {
                Log.e(TAG, "writeCoil onSuccess " + s);
            }

            @Override
            public void onFailed(String msg) {
                Log.e(TAG, "writeCoil onFailed " + msg);
            }
        }, 1, 15, 0);
        ///////////////////sprawdzić które rejestry wolne////////////////////////////
       Intent intent = new Intent(this, Stoper.class);
       this.startActivity(intent);
    }

    public void cleanRegisters()
    {
        int i;
        for(i=300;i<1100;i++)
        {
            ModbusReq.getInstance().writeRegister(new OnRequestBack<String>() {
                @Override
                public void onSuccess(String s) {
                    Log.e(TAG, "writeRegister on Success 1" + s);
                }

                @Override
                public void onFailed(String s) {
                    Log.e(TAG, "writeRegister onFailed" + s);
                }
            }, 1, i, 0);
        }
    }

    Runnable flagStartPosition= new Runnable() {
        @Override
        public void run() {
            ModbusReq.getInstance().readHoldingRegisters(new OnRequestBack<short[]>() {
                public static final String TAG = "udalo sie";

                @Override
                public void onSuccess(short[] data) {
                    Log.d(TAG, "readHoldingRegisters onSuccess " + Arrays.toString(data));
                    if (data[0] == 1) {
                        flagStart=true;
                        FlagStartHandler.postDelayed(flagStartPosition, 0);
                    } else {
                        FlagStartHandler.postDelayed(flagStartPosition, 0);
                        flagStart=false;
                    }
                }
                @Override
                public void onFailed(String msg) {
                    Log.e(TAG, "readHoldingRegisters onFailed " + msg);
                }
            }, 1, 60, 1);
        }
    };

    Runnable CheckFlagStart = new Runnable() {
        @Override
        public void run() {
            if(flagStart==true)
            {
                findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                Send.setEnabled(true);
                Draw.setEnabled(true);
                SetStartPosition.setEnabled(true);
                FlagStartHandler.postDelayed(CheckFlagStart,0);
            }else
            {
                FlagStartHandler.postDelayed(CheckFlagStart,0);
            }
        }
    };




}



//////////////////////////////////////////CAŁOŚĆ ZASTOSOWAĆ W KLASIE ZDJĘCIE////////////////

///////////////////////////////////////////////////