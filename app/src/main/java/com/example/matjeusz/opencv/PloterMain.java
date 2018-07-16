package com.example.matjeusz.opencv;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.zgkxzx.modbus4And.requset.ModbusParam;
import com.zgkxzx.modbus4And.requset.ModbusReq;
import com.zgkxzx.modbus4And.requset.OnRequestBack;

import java.util.Arrays;


public class PloterMain extends AppCompatActivity {

    //deklaracja elementów
    Button gora;
    Button dol;
    Button lewo;
    Button prawo;
    Button homing;
    Button usunbledy;
    ImageView i;
    String ip;
    TextView adres;
    SeekBar seekBar;
    TextView postep;
    int szybkosc;
    boolean flaga=false;
    Handler customHandler = new Handler();

    //parametry Modbus
    ModbusParam modbusParam = new ModbusParam();
    //utworzenie stałego TAGU dla PloterMain
    private static final String TAG = "PloterMain";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ploter_main);

        findViewById(R.id.loadingPanel).setVisibility(View.GONE);

        //odbiór IP z Logowanie i sprawdzenie działania
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            ip = extras.getString("sprawdzenie");
        }

        //wpis IP na ekran
        adres = findViewById(R.id.ipadres);
        adres.setText(ip);

        //przypisanie przycisków do elementów aktywności
        gora =  findViewById(R.id.góra);
        dol = findViewById(R.id.dół);
        lewo = findViewById(R.id.lewo);
        prawo =  findViewById(R.id.prawo);
        homing = findViewById(R.id.homing);
        usunbledy = findViewById(R.id.bledy);


        //zmienne do seekbaru
        seekBar = findViewById(R.id.szybkoscbar);
        postep = findViewById(R.id.postep);



        //ustawienie parametrów modbus
        modbusParam.setHost(ip);
        modbusParam.setPort(502);
        modbusParam.setEncapsulated(false);
        modbusParam.setKeepAlive(true);
        modbusParam.setTimeout(2000);
        modbusParam.setRetries(0);

        //rozpoczęcie komunikacji z urządzeniem modbus TCP
        ModbusReq.getInstance().setParam(modbusParam).init(new OnRequestBack<String>() {
            @Override
            public void onSuccess(String s) {
                Log.d(TAG, "onSucces" + s);
            }

            @Override
            public void onFailed(String msg) {
                Log.d(TAG, "onFailed " + msg);
            }
        });

        //funkcje OnTouch dla przycisków sterowania ploterem
        gora.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        gora(view);

                        return true;

                    case MotionEvent.ACTION_UP:
                        ngora(view);
                        return true;
                }

                return false;
            }
        });


        dol.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        dol(view);

                        return true;

                    case MotionEvent.ACTION_UP:
                        ndol(view);
                        return true;
                }

                return false;
            }
        });

        lewo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        lewo(view);

                        return true;

                    case MotionEvent.ACTION_UP:
                        nlewo(view);
                        return true;
                }

                return false;
            }
        });

        prawo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        prawo(view);

                        return true;

                    case MotionEvent.ACTION_UP:
                        nprawo(view);
                        return true;
                }

                return false;
            }
        });


        homing.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        homing(view);

                        return true;

                    case MotionEvent.ACTION_UP:
                        nhoming(view);
                        return true;
                }

                return false;
            }
        });

        usunbledy.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        usunbledy(view);

                        return true;

                    case MotionEvent.ACTION_UP:
                        nusunbledy(view);
                        return true;
                }

                return false;
            }
        });

        //pasek szybkosci

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                szybkosc=i;
                //wysyłanie szybkości do PLC
                SendSpeedToPLC(szybkosc);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //wysyłanie szybkości do PLC
                SendSpeedToPLC(szybkosc);
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                postep.setText(szybkosc+"/"+seekBar.getMax());
                //wysyłanie szybkości do PLC
                SendSpeedToPLC(szybkosc);
            }
        });




    }



    // funkcje wpisywania wartości w przekaźniki PLC
    public void gora(View view) {

        ModbusReq.getInstance().writeRegister(new OnRequestBack<String>() {
            @Override
            public void onSuccess(String s) {
                Log.e(TAG, "writeCoil onSuccess " + s);
            }

            @Override
            public void onFailed(String msg) {
                Log.e(TAG, "writeCoil onFailed " + msg);
            }
        }, 1, 30, 1);

    }

    public void ngora(View view) {

        ModbusReq.getInstance().writeRegister(new OnRequestBack<String>() {
            @Override
            public void onSuccess(String s) {
                Log.e(TAG, "writeCoil onSuccess " + s);
            }

            @Override
            public void onFailed(String msg) {
                Log.e(TAG, "writeCoil onFailed " + msg);
            }
        }, 1, 30, 0);

    }


    public void dol(View view) {

        ModbusReq.getInstance().writeRegister(new OnRequestBack<String>() {
            @Override
            public void onSuccess(String s) {
                Log.e(TAG, "writeCoil onSuccess " + s);
            }

            @Override
            public void onFailed(String msg) {
                Log.e(TAG, "writeCoil onFailed " + msg);
            }
        }, 1, 31, 1);

    }

    public void ndol(View view) {

        ModbusReq.getInstance().writeRegister(new OnRequestBack<String>() {
            @Override
            public void onSuccess(String s) {
                Log.e(TAG, "writeCoil onSuccess " + s);
            }

            @Override
            public void onFailed(String msg) {
                Log.e(TAG, "writeCoil onFailed " + msg);
            }
        }, 1, 31, 0);

    }



    public void lewo(View view) {

        ModbusReq.getInstance().writeRegister(new OnRequestBack<String>() {
            @Override
            public void onSuccess(String s) {
                Log.e(TAG, "writeCoil onSuccess " + s);
            }

            @Override
            public void onFailed(String msg) {
                Log.e(TAG, "writeCoil onFailed " + msg);
            }
        }, 1, 32, 1);

    }

    public void nlewo(View view) {

        ModbusReq.getInstance().writeRegister(new OnRequestBack<String>() {
            @Override
            public void onSuccess(String s) {
                Log.e(TAG, "writeCoil onSuccess " + s);
            }

            @Override
            public void onFailed(String msg) {
                Log.e(TAG, "writeCoil onFailed " + msg);
            }
        }, 1, 32, 0);

    }


    public void prawo(View view) {

        ModbusReq.getInstance().writeRegister(new OnRequestBack<String>() {
            @Override
            public void onSuccess(String s) {
                Log.e(TAG, "writeCoil onSuccess " + s);
            }

            @Override
            public void onFailed(String msg) {
                Log.e(TAG, "writeCoil onFailed " + msg);
            }
        }, 1, 33, 1);

    }


    public void nprawo(View view) {

        ModbusReq.getInstance().writeRegister(new OnRequestBack<String>() {
            @Override
            public void onSuccess(String s) {
                Log.e(TAG, "writeCoil onSuccess " + s);
            }

            @Override
            public void onFailed(String msg) {
                Log.e(TAG, "writeCoil onFailed " + msg);
            }
        }, 1, 33, 0);

    }

    public void setEnableButton()
    {
        findViewById(R.id.wzór).setEnabled(true);
        gora.setEnabled(true);
        prawo.setEnabled(true);
        lewo.setEnabled(true);
        dol.setEnabled(true);
        usunbledy.setEnabled(true);
        homing.setEnabled(true);
        usunbledy.setEnabled(true);
    }

    Runnable checkFlag = new Runnable() {
        @Override
        public void run() {
            if (flaga==true)
            {
                findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                setEnableButton();
                customHandler.postDelayed(checkFlag,0);
            }else
                customHandler.postDelayed(checkFlag,0);
        }
    };

    Runnable readFlag = new Runnable() {
        @Override
        public void run() {
            ModbusReq.getInstance().readHoldingRegisters(new OnRequestBack<short[]>() {
                public static final String TAG = "udalo sie";

                @Override
                public void onSuccess(short[] data) {
                    Log.d(TAG, "readHoldingRegisters onSuccess " + Arrays.toString(data));
                    if (data[0] == 1) {
                       flaga=true;
                        customHandler.postDelayed(readFlag, 0);
                    } else {
                        customHandler.postDelayed(readFlag, 0);
                        flaga=false;
                    }
                }

                @Override
                public void onFailed(String msg) {
                    Log.e(TAG, "readHoldingRegisters onFailed " + msg);
                }
            }, 1, 45, 1);

        }

    };


    public void homing(View view) {

        ModbusReq.getInstance().writeRegister(new OnRequestBack<String>() {
            @Override
            public void onSuccess(String s) {
                Log.e(TAG, "writeCoil onSuccess " + s);
            }

            @Override
            public void onFailed(String msg) {
                Log.e(TAG, "writeCoil onFailed " + msg);
            }
        }, 1, 34, 1);
        findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
        findViewById(R.id.wzór).setEnabled(false);
        gora.setEnabled(false);
        prawo.setEnabled(false);
        lewo.setEnabled(false);
        dol.setEnabled(false);
        usunbledy.setEnabled(false);
        homing.setEnabled(false);
        usunbledy.setEnabled(false);
        customHandler.postDelayed(readFlag, 0);
        customHandler.postDelayed(checkFlag,0);
    }

    public void nhoming(View view) {

        ModbusReq.getInstance().writeRegister(new OnRequestBack<String>() {
            @Override
            public void onSuccess(String s) {
                Log.e(TAG, "writeCoil onSuccess " + s);
            }

            @Override
            public void onFailed(String msg) {
                Log.e(TAG, "writeCoil onFailed " + msg);
            }
        }, 1, 34, 0);

    }

    public void usunbledy(View view) {

        ModbusReq.getInstance().writeRegister(new OnRequestBack<String>() {
            @Override
            public void onSuccess(String s) {
                Log.e(TAG, "writeCoil onSuccess " + s);
            }

            @Override
            public void onFailed(String msg) {
                Log.e(TAG, "writeCoil onFailed " + msg);
            }
        }, 1, 43, 1);

    }

    public void nusunbledy(View view) {

        ModbusReq.getInstance().writeRegister(new OnRequestBack<String>() {
            @Override
            public void onSuccess(String s) {
                Log.e(TAG, "writeCoil onSuccess " + s);
            }

            @Override
            public void onFailed(String msg) {
                Log.e(TAG, "writeCoil onFailed " + msg);
            }
        }, 1, 43, 0);

    }


    //uruchomienie kamery z przycisku "wzór"
    public void wzor(View view) {
        Intent wzor = new Intent(this, WyborRysowania.class);
        //Intent aparat = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); // uruchamianie aparatu
        this.startActivity(wzor);

    }



    public void SendSpeedToPLC(int szybkosc)
    {
        ModbusReq.getInstance().writeRegister(new OnRequestBack<String>() {
            @Override
            public void onSuccess(String s) {
                Log.e(TAG,"writeRegister on Success"+s);
            }

            @Override
            public void onFailed(String s) {
                Log.e(TAG,"writeRegister onFailed"+s);
            }
        },1,35,szybkosc);
    }
}

