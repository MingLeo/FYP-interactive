package com.example.fyp_interactive;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Camera extends AppCompatActivity {


    //Surface Text plane (a.k.a Canvas) on which we load/Open our Camera/Image on.
    private TextureView textureView;
    private TextureView.SurfaceTextureListener surfaceTextureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            Toast.makeText(getApplicationContext(), "TextureView is available", Toast.LENGTH_SHORT).show();
            try {
                setupCamera(width, height);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            //>?????? NO IMPLEMENTATION YET >???????
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
            //>????? NO IMPLEMENTATION YET >???????
        }
    };



    //set up/ call/instantiate an instance of the CameraDevice directly, & assigning methods to state what we can do w it ~~ Open, cLOSE, pAUSE the Camera etc....
    private CameraDevice cameraDevice;
    private CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {   //A callback objects for receiving updates about the state of a camera device. A callback instance must be provided to the openCamera(String, CameraDevice.StateCallback, Handler) method to open a camera device.
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            cameraDevice = camera;
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            camera.close();
            cameraDevice = null;
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            camera.close();
            cameraDevice = null;
        }
    };

    public void closeCamera(){    //when not in use, like when we pause the app, like put it into background, we want it to free up the camera so as not to hog the camera application!
        if (cameraDevice != null){
            cameraDevice.close();
            cameraDevice=null;
        }
    }


    //use CameraManager to handle/Manage the camera device. Stategy pattern('Managing'), to achieve Greater Decoupling. So next time if you change the camera, this CameraManager implementation will not be affected.
    // calling the Camera device (CameraId) via the CameraCharactersitics() property to call down directly to the Camera Hardware itself.
    // returns all the different available cameras on the phone.
    private String mCameraId;
    private String mCamId, camId, i;
    private Size mPreviewSize;
    public void setupCamera(int width, int height) throws CameraAccessException {
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        /*
        String[] camIds;
        String  mCamId, camId, i;
        */

        /*
        final String[] cameraIdList = cameraManager.getCameraIdList();
        for (String camId : cameraIdList){
            Log.d("entered Cam1 Loop","999");
            Log.d("CameraId:", camId);   // for internal logging purposes. "print" out cameraId for Debugging purposes
            Toast.makeText(this, "entered CamId loop", Toast.LENGTH_SHORT).show();    //only show on front UI.
        }
        */

        try {
            for(String cameraId : cameraManager.getCameraIdList()){     // For each loop in the form of : “for (A b : c)” in Java
                CameraCharacteristics cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraId);    //getting the characteristics of each indiv diff Camera.
                Log.d("entered Cam2 Loop","997");
                Log.d("cameraId:", cameraId);
                if (cameraCharacteristics.get(CameraCharacteristics.LENS_FACING) ==
                        CameraCharacteristics.LENS_FACING_FRONT){
                    continue;
                }
                StreamConfigurationMap map = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);    //map that contains all the list of various resolution represented by the preview, by the camera, by the video, by the raw camera......
                int deviceOrientation = getWindowManager().getDefaultDisplay().getRotation();
                int totalRotation = sensorToDeviceRotation(cameraCharacteristics, deviceOrientation);    //see method the sensorToDeviceRotation method that we alrdy implemented below.
                boolean swapRotation = totalRotation == 90 || totalRotation == 270;   //setting a flag call swapRotation, set/indicate the conditions for true, so that if/once conditions/criterias are met, trigger & set this flag.
                int rotatedWidth = width;
                int rotatedHeight = height;
                if(swapRotation == true){    //swap btwn portrait and landscape mode, length now becomes the new breadth, and breadth becomes the new length.
                    rotatedWidth = height;
                    rotatedHeight = width;
                }
                mPreviewSize = chooseOptimalSize(map.getOutputSizes(SurfaceTexture.class), rotatedWidth, rotatedHeight);   //chooses the closes possible match that it can find for the preview texture resolution that matches our screen device width & height.   See below for method implemented we created.
                mCameraId = cameraId;    //OBTAINING THE id OF THE hardware CAMERA Device!
                return;
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

    }


    private static SparseIntArray ORIENTATIONS = new SparseIntArray();
    static{
        ORIENTATIONS.append(Surface.ROTATION_0,0);
        ORIENTATIONS.append(Surface.ROTATION_90, 90);
        ORIENTATIONS.append(Surface.ROTATION_180, 180);
        ORIENTATIONS.append(Surface.ROTATION_270, 270);
    }

    private static int sensorToDeviceRotation(CameraCharacteristics cameraCharacteristics, int deviceOrientation){
        int sensorOrientation = cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
        deviceOrientation = ORIENTATIONS.get(deviceOrientation);
        return (sensorOrientation + deviceOrientation + 360)%360;   //moduluo 260 to keep angle within 360 range.
    }

    private static class CompareSizeByArea implements Comparator<Size> {

        @Override
        public int compare(Size o1, Size o2) {    //comparing between 2 objects -> object 1 & object 2.
            return Long.signum(((long) o1.getWidth() * o1.getHeight())/((long) o2.getWidth() * o2.getHeight()));
        }
    }

    private static Size chooseOptimalSize(Size[] choices, int width, int height){
        List<Size> bigEnough = new ArrayList<Size>();    //a list to contain all the preview options
        for(Size option : choices){
            //check for 3 things, 3 conditions to be met. aspect ratio is correct match our textureview, nd to check that the value from the prev orientation/sensor is big enough by width and height wise for our requested textureview.
            if(option.getHeight() == option.getWidth() * height/width &&     //this is the aspect ratio check so we multiply by height, divide by width. that's 1 thing we gonna check for that match
                    option.getWidth() >= width &&      // check for width match
                    option.getHeight() >= height){     //now we gonna check the height match
                bigEnough.add(option);    //if the above 3 conditions are met, add it to our List.
            }
        }
        if(bigEnough.size()>0){    //in other words, do we have any orientation in our list that is bigger than the current view Orientation?
            return Collections.min(bigEnough, new CompareSizeByArea());
        }else{
            return choices[0];  //default return 1st choice in the Array if no match is found.  //Nd to return something for an int method.
        }
    }





    //Creating background threads to push all our time consuming tasks off the UI Thread, cos we dont want to cause any frame losses.
    //Can handle tasks at the background instead of foreground, to free up resources, prevent APP from Hang/Crash. MultiTask ability, able to handle diff tasks ASYNCHRONOUSLY simultaneously @ the same time!
    private HandlerThread mBackgroundHandlerThread;
    private Handler mBackgroundHandler;

    private void startBackgroundThread(){      //to be use in onResume() method of Camera
        mBackgroundHandlerThread = new HandlerThread("Camera2VideoImage");
        mBackgroundHandlerThread.start();
        mBackgroundHandler = new Handler(mBackgroundHandlerThread.getLooper());  //assign a handler to handle this Background thread
    }

    private void stopBackgroundThread(){       //to be use in onPause() method of Camera, to free up Memory/Thread so as not to hog up resouces so that Phone doesnt Crash!
        mBackgroundHandlerThread.quitSafely();
        try {
            mBackgroundHandlerThread.join();
            mBackgroundHandlerThread = null;   //once done, set thread to null
            mBackgroundHandler = null;    //once done, set Handler to null also.
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }




    //surface types : TextureView, ImageReader, MediaRecorder... (DONE!)

    //size[] configuration of the surface texture that the Camera gonna cover/take up.
    //createCaptureSession() , sessionStateListener()

    //create a CaptureRequest
    //RequestBuilder - shld be Asynchornous, can have multiple SessionRequestBuilders. Nd assign 1 for each type of surfaces that you gonna use.


    // this is the CRUX of the camera, the real deal, the real meat, the real piece of shit, the method to start this whole CAMERA ACTIVITY!! Without it/which, all the other methods will be for naught, wont be able to run!!  This is where it all begins(start).
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        textureView = (TextureView) findViewById(R.id.textureView);    //attach/tag/symbolically link object @ backend to process id of front end UI pane. To know which input to pass to which backend object to handle.

    }

    @Override
    protected void onResume() {    //checks whether the textureView is available whenever we Create it.
        super.onResume();

        startBackgroundThread();

        if(textureView.isAvailable()){
            try {
                setupCamera(textureView.getWidth(), textureView.getHeight());    //get the width and height of the textureview, dimensions to which we will try to ask the Camera to match to, to open it up to fullscreen!
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        } else {
            textureView.setSurfaceTextureListener(surfaceTextureListener);   //pls tell when the textureView is available.
        }
    }

    @Override
    protected void onPause() {
        closeCamera();

        stopBackgroundThread();

        super.onPause();
    }


    protected void onWindowsFocusChanged(boolean hasFocus){
        super.onWindowFocusChanged(hasFocus);
        View decorView = getWindow().getDecorView();
        if (hasFocus){
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                    //View.SYSTEM_UI_FLAG_IMMERSIVE |
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                    View.SYSTEM_UI_FLAG_FULLSCREEN |
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        }
    }





}