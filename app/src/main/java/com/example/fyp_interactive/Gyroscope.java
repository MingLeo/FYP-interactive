package com.example.fyp_interactive;

import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.os.Bundle;
import android.os.Handler;


public class Gyroscope extends AppCompatActivity {

    private SensorManager sensorManager;
    private Sensor gyroscopeSensor;
    private SensorEventListener gyroscopeEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gyroscope);     //Learn to use the Gyroscope Sensor on Android YT Vid tutorial by Sylvain Saurel
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);   // calling the hardware service of the sensor component itself
        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);   //implementing a gyroscop, using the phone's inbuilt hardware SENSORS

        if (gyroscopeSensor == null){
            Toast.makeText(this, "this device does not have /come w a built-in gyroscope!", Toast.LENGTH_SHORT);   // SHORT_DURATION_TIMEOUT = 4s , LONG_DURATION_TIMEOUT = 7s
            finish();
        }

        gyroscopeEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                if (sensorEvent.values[2]>0.2f){    //.values[0] x-axis , values[1] y-axis , values[0] z-axis
                    /*
                    try {
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // Do something after 5s = 5000ms
                                getWindow().getDecorView().setBackgroundColor(Color.BLUE);  //https://developer.android.com/guide/topics/sensors/sensors_position
                            }
                        }, 5000);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    */
                }
                else if (sensorEvent.values[2]<0.2f){
                    getWindow().getDecorView().setBackgroundColor(Color.YELLOW);
                }
                else if (sensorEvent.values[1]>0.2f){
                    getWindow().getDecorView().setBackgroundColor(Color.GREEN);
                }
                else if (sensorEvent.values[1]<0.2f){
                    getWindow().getDecorView().setBackgroundColor(Color.MAGENTA);
                }
                else if (sensorEvent.values[0]>0.2f){
                    getWindow().getDecorView().setBackgroundColor(Color.GRAY);
                }
                else if (sensorEvent.values[0]<0.2f){
                    getWindow().getDecorView().setBackgroundColor(Color.WHITE);
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(gyroscopeEventListener, gyroscopeSensor, sensorManager.SENSOR_DELAY_FASTEST) ;
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(gyroscopeEventListener);
    }
}
