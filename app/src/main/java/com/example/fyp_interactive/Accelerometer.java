package com.example.fyp_interactive;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Accelerometer extends AppCompatActivity implements SensorEventListener{

    private SensorManager sensorManager;
    private Sensor accelerometerSensor;
    private SensorEventListener accelerometerEventListener;
    private static final String TAG = "Accelerometer";    //Give a Tagging for your Msg type, for ease of Internal Debugging. To identify!!

    //private float x,y,z;
    TextView xValue, yValue, zValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accelerometer);     //Learn to use the Gyroscope Sensor on Android YT Vid tutorial by Sylvain Saurel
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);   // calling the hardware service of the sensor component itself
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
            Toast.makeText(this, "this device does not have /come w a built-in accelerometer!", Toast.LENGTH_SHORT).show();   // SHORT_DURATION_TIMEOUT = 4s , LONG_DURATION_TIMEOUT = 7s
            finish();
        }

        xValue = (TextView) findViewById(R.id.xAcclUpdate);   //Type cast as a check ensure that it is ""calling"/"updating" to the correct type.
        yValue = (TextView) findViewById(R.id.yAcclUpdate);
        zValue = (TextView) findViewById(R.id.zAcclUpdate);

        Log.d(TAG, "onCreate: Initializing Sensor Services");
        sensorManager.registerListener(Accelerometer.this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);  // Typecasting again, notice how we alrdy create an object earlier, now is just to sorta "check" to play safe, ensure pass in to the correct Object Type.
        Log.d(TAG, "onCreate: Registered accelerometer listener");
    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Log.d(TAG, "onAcceleroSensorChanged: X" + sensorEvent.values[0] + "Y" + sensorEvent.values[1] + "Z" + sensorEvent.values[2]);    //Log is like an internal msg print only on Android Studio IDE, is sorta like a Git Tag "an internal debug Msg" for developers to see, is hidden away from App UI users.
        xValue.setText("xValueUpdate:" + sensorEvent.values[0]);
        yValue.setText("yValueUpdate:" + sensorEvent.values[1]);
        zValue.setText("zValueUpdate:" + sensorEvent.values[2]);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

        /*
        accelerometerEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent){
                if (sensorEvent.values[2] > 0.5f){
                    getWindow().getDecorView().setBackgroundColor(Color.BLUE);
                }else if (sensorEvent.values[2] < -0.5f){
                    getWindow().getDecorView().setBackgroundColor(Color.YELLOW);
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };

    }



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

           /*
            public void onSensorChanged(final SensorEvent event) {

                //final SensorEvent event = new android.hardware.SensorEvent();     //Variable 'event' is already defined in the scope
                //final SensorEvent event = new SensorEvent();     //Variable 'event' is already defined in the scope
                try {
                    //final Handler handler = new Handler();
                    //handler.postDelayed(new Runnable() {
                       // @Override
                       // public void run(){
                            // Do something after 5s = 5000ms
                            getWindow().getDecorView().setBackgroundColor(Color.BLUE);  //https://developer.android.com/guide/topics/sensors/sensors_position
                            x = event.values[0];
                            y = event.values[1];
                            z = event.values[2];
                            update(x, y, z);    //see method declaration below, updates to id.output which is deplayed onto the UI Screen as declared in activity_accelerometer.
                            //Log.d(TAG,"newThread");
                        //}
                    //}, 5000);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    getWindow().getDecorView().setBackgroundColor(Color.WHITE);
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }

        };
    }


        public void update(float x, float y, float z) {

            new Timer().schedule(
                    new TimerTask(){
                        @Override

                        public void run(){
                            TextView textView = (TextView) findViewById(R.id.output);
                            textView.setText("x: " + x + " y: " + y + " z: " + z);
                        }
                    }, 2000);
        }

        */


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