package com.example.matjeusz.opencv;

import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.zgkxzx.modbus4And.requset.ModbusReq;
import com.zgkxzx.modbus4And.requset.OnRequestBack;

import org.w3c.dom.Text;

import java.util.Arrays;

public class Stoper extends AppCompatActivity {

    TextView txtTimer, finish;
    long startTimer = 0L, timeMiliseconds = 0L, timeSwapBuff = 0L, updateTime = 0L;
    Handler customHandler = new Handler();
    Handler flagHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stoper);

        txtTimer = (TextView) findViewById(R.id.Stoper);
        finish = (TextView) findViewById(R.id.Finish);
        startTimer();
    }

    Runnable updateTimeTread = new Runnable() {
        @Override
        public void run() {
            timeMiliseconds = SystemClock.uptimeMillis() - startTimer;
            updateTime = timeSwapBuff + timeMiliseconds;
            int secs = (int) (updateTime / 1000);
            int mins = (int) (secs / 60);
            secs %= 60;
            int miliseconds = (int) (updateTime % 1000);
            txtTimer.setText("" + mins + ":" + String.format("%2d", secs) + ":" + String.format("%3d", miliseconds));

        }
    };

    public void startTimer() {
        startTimer = SystemClock.uptimeMillis();
        customHandler.postDelayed(updateTimeTread, 0);
        flagHandler.postDelayed(readFlag, 0);

    }


    Runnable readFlag = new Runnable() {
        @Override
        public void run() {
            ModbusReq.getInstance().readHoldingRegisters(new OnRequestBack<short[]>() {
                public static final String TAG = "udalo sie";

                @Override
                public void onSuccess(short[] data) {
                    Log.d(TAG, "readHoldingRegisters onSuccess " + Arrays.toString(data));
                    if (data[0] == 1) {
                       //timeSwapBuff += timeMiliseconds;
                       // flagHandler.postDelayed(readFlag, 0);
                    } else {
                        flagHandler.postDelayed(readFlag, 0);
                        customHandler.postDelayed(updateTimeTread, 0);
                    }
                }

                @Override
                public void onFailed(String msg) {
                    Log.e(TAG, "readHoldingRegisters onFailed " + msg);
                }
            }, 1, 25, 1);

        }

    };
}