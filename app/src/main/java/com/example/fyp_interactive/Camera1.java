/*
package com.example.fyp_interactive;

import android.content.Context;
import android.graphics.SurfaceTexture;      //created as a result of the SurfaceTextureistener??

//import android.hardware.camera2.     // recommended API for newer or advanced camera applications.  Provides an interface to individual camera devices connected to an Android device.
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;  //
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.media.ImageReader;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v7.app.AppCompatActivity;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.TextureView;    //textureview, surfaceview for display screen / camera.
import android.view.View;    //method setOnClickListener is called from this class
import android.widget.Button;   //use for creating interfacing UI "Buttons" on the screen.
import android.widget.Toast;

import com.example.fyp_interactive.R;


public class Camera1 extends AppCompatActivity {

    private Button btnCapture;
    private TextureView textureView3;

    //Check state orientation of output image
    private static final SparseIntArray ORIENTATION = new SparseIntArray();    //instantiate a new SparseIntArray object with each method call.
    static{
        ORIENTATION.append(Surface.ROTATION_0,0);   //An arrau of (key,value) pair
        ORIENTATION.append(Surface.ROTATION_90,90);
        ORIENTATION.append(Surface.ROTATION_180,180);
        ORIENTATION.append(Surface.ROTATION_270,270);
    }

    private String CameraId;
    private CameraDevice cameraDevice;
    private CameraCaptureSession cameraCaptureSession;
    private CaptureRequest.Builder captureRequestBuilder;
    private Size imageDimension;
    private ImageReader imageReader;

    //private File file;
    //private static final int REQUEST_CAMERA_PERMISSION = 200;
    //private boolean mFlashSupported;
    private Handler mBackgroundHandler;
    private HandlerThread mBackgroundThread;


    /*
    private CameraManager cameraManager;
    private CameraDevice cameraDevice;
    private CameraCharacteristics cameraCharacteristics;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera1);     //set the view to the template as specified in the camera activity xml file for the UI plane.

        textureView3 = (TextureView) findViewById(R.id.textureView3);    //attach/tag/symbolically link object @ backend to process id of front end UI pane. To know which input to pass to which backend object to handle.
        assert textureView3!= null;    //same effect as using if statement to check like I did in the accelerometer plane.


        textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                openCamera();
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {

            }
        });     // all listeneres is like a wrapper enclosing all the methods that this listener will try execute base on inputs from user.
        btnCapture = (Button)findViewById(R.id.btnCapture);
        btnCapture.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                takePicture();
            }    //like wise, this listener is a wrapper defining all types of methods that it can handle base on the user inputs that it receives.


        }
    }

    private void takePicture() {
        if(CameraDevice == null) {
            Toast.makeText(this, "this device does not have/support a Camera!", Toast.LENGTH_SHORT);   // SHORT_DURATION_TIMEOUT = 4s , LONG_DURATION_TIMEOUT = 7s
            finish();
        }
        CameraManager cameraManager = (CameraManager)getSystemService(Context.CAMERA_SERVICE);
        try{
            CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraDevice.getId());


        }

    }

    private void openCamera() {
    }


}

}

*/