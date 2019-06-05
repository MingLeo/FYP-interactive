package com.example.fyp_interactive;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

public class Accelerometer extends AppCompatActivity {

    private SensorManager sensorManager;
    private Sensor accelerometerSensor;
    private SensorEventListener accelerometerEventListener;
    private static final String TAG = "Accelerometer";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accelerometer);     //Learn to use the Gyroscope Sensor on Android YT Vid tutorial by Sylvain Saurel
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);   // calling the hardware service of the sensor component itself
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);   //implementing a gyroscop, using the phone's inbuilt hardware SENSORS


        /*public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            textView = new TextView(this);
            setContentView(textView);
            sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }
        */

        if (accelerometerSensor == null) {
            Toast.makeText(this, "this device does not have /come w a built-in accelerometer!", Toast.LENGTH_SHORT);   // SHORT_DURATION_TIMEOUT = 4s , LONG_DURATION_TIMEOUT = 7s
            finish();
        }

        accelerometerEventListener = new SensorEventListener() {

                /*
                @Override
                public void onSensorChanged(SensorEvent sensorEvent) {
                    if (sensorEvent.values[2]>0.2f){    //.values[0] x-axis , values[1] y-axis , values[0] z-axis

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
                    }
                    else if (sensorEvent.values[2]<0.2f){
                        getWindow().getDecorView().setBackgroundColor(Color.YELLOW);
                    }

                }
                */

            public void onSensorChanged(final SensorEvent event) {

                //final SensorEvent event = new android.hardware.SensorEvent();     //Variable 'event' is already defined in the scope
                //final SensorEvent event = new SensorEvent();     //Variable 'event' is already defined in the scope
                try {
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Do something after 5s = 5000ms
                            getWindow().getDecorView().setBackgroundColor(Color.BLUE);  //https://developer.android.com/guide/topics/sensors/sensors_position
                            float x = event.values[0];
                            float y = event.values[1];
                            float z = event.values[2];
                            update(x, y, z);
                            Log.d(TAG,"newThread");
                        }
                    }, 5000);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    getWindow().getDecorView().setBackgroundColor(Color.YELLOW);
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }

        };
    }


        public void update(float x, float y, float z) {

            TextView textView = (TextView) findViewById(R.id.output);
            textView.setText("x: " + x + " y: " + y + " z: " + z);
        }


        @Override
        protected void onResume () {
            super.onResume();
            sensorManager.registerListener(accelerometerEventListener, accelerometerSensor, sensorManager.SENSOR_DELAY_FASTEST);
        }

        @Override
        protected void onPause () {
            super.onPause();
            sensorManager.unregisterListener(accelerometerEventListener);
        }
}