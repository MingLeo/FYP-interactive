package com.example.fyp_interactive;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Gyroscope extends AppCompatActivity{

    private SensorManager sensorManager;
    private Sensor gyroscopeSensor;
    private SensorEventListener gyroscopeEventListener;
    private static final String TAG = "Gyroscope";

    TextView xGyro, yGyro, zGyro;

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

        xGyro = (TextView) findViewById(R.id.xGyroUpdate);   //Type cast as a check ensure that it is ""calling"/"updating" to the correct type.
        yGyro = (TextView) findViewById(R.id.yGyroUpdate);
        zGyro = (TextView) findViewById(R.id.zGyroUpdate);

        gyroscopeEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                Log.d(TAG,"onGyroSensorChanged: X" + sensorEvent.values[0] + "Y" + sensorEvent.values[1] + "Z" + sensorEvent.values[2]);    //Log is like an internal msg print only on Android Studio IDE, is sorta like a Git Tag "an internal debug Msg" for developers to see, is hidden away from App UI users.
                xGyro.setText("xGyroUpdate:" + sensorEvent.values[0]);
                yGyro.setText("yGyroUpdate:" + sensorEvent.values[1]);
                zGyro.setText("zGyroUpdate:" + sensorEvent.values[2]);   //Relax, even if is the same class sensorEvent use for Accelro/Gyro, is differentiated by the ListenerType that you use!!
            }

                /*
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

                */

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
