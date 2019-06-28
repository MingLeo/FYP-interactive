package com.example.fyp_interactive;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

    Button gyroscope, accelerometer, camera;
    TextView welcome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gyroscope = (Button) findViewById(R.id.gyroscopeButton);
        accelerometer = (Button) findViewById(R.id.accelerometerButton);
        camera = (Button) findViewById(R.id.cameraButton);


        gyroscope.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent gyroscopeIntent = new Intent(MainActivity.this, Gyroscope.class);
                startActivity(gyroscopeIntent);
            }
        });

        accelerometer.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent accelerometerIntent = new Intent(MainActivity.this, Accelerometer.class);
                startActivity(accelerometerIntent);
            }
        });


        camera.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent cameraIntent = new Intent(MainActivity.this, Camera.class);
                startActivity(cameraIntent);
            }
        });

        //openCV.setOnClickListener(new View.OnClickListener() {});

    }
}
