package com.example.fyp_interactive;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class Camera extends AppCompatActivity {


    //Surface Text plane (a.k.a Canvas) on which we load/Open our Camera/Image on.
    private TextureView textureView;   //a blank canvas aka the "texture" on which/where we gonna work with, the camera photo/video display gonna be.

    private TextureView.SurfaceTextureListener surfaceTextureListener = new TextureView.SurfaceTextureListener() {   //surface Texture, surface on which we gonna interface/interact with
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            Toast.makeText(getApplicationContext(), "TextureView is available", Toast.LENGTH_SHORT).show();    //once texture view is available, fully created & inflated, can notify next service that is dependent on it aka "listening/waiting to it to be ready" to execute.
            try {
                setupCamera(width, height);
                connectCamera();
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            //>?????? NO IMPLEMENTATION YET >???????
        }

        //@org.jetbrains.annotations.Contract(pure = true)
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
    private CameraDevice cameraDevice;     // camera Device hardware itself !

    private CameraDevice.StateCallback mCameraDeviceStateCallback = new CameraDevice.StateCallback() {     //A listener to detect callback states, gather inputs from surface fed by user. Example he press button to open camera, take pic, etc...., or process send to background when switch to a diff app "switch application context"
                                                                                                           // A callback instance must be provided to the openCamera(String, CameraDevice.StateCallback, Handler) method to open a camera device.
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            cameraDevice = camera;
            Toast.makeText(getApplicationContext(), "Camera connection made!", Toast.LENGTH_SHORT).show();
            startPreview();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            camera.close();
            cameraDevice = null;   // release the camera to free up resources, CPU Cache.
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            camera.close();
            cameraDevice = null;    // unable to find suitable camera to connect to.
        }
    };




    //use CameraManager to handle/Manage the camera device. Stategy pattern('Managing'), to achieve Greater Decoupling. So next time if you change the camera, this CameraManager implementation will not be affected.
    // calling the Camera device (CameraId) via the CameraCharactersitics() property to call down directly to the Camera Hardware itself.
    // returns all the different available cameras on the phone.
    public String mCameraId;
    public String mCamId, camId, i;
    public Size mPreviewSize;
    public void setupCamera(int width, int height) throws CameraAccessException {    // set up the Camera service!

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
                Log.d("entered Cam2 Loop","997");   //  Log | .d debug  .i info  .v verbose  .w warning  .e error
                Log.d("cameraId:", cameraId);            //  Log - internal msgs that only reflect inside the IDE
                if (cameraCharacteristics.get(CameraCharacteristics.LENS_FACING) ==
                        CameraCharacteristics.LENS_FACING_FRONT){
                    continue;   // continues on w a new/re-iteration of For loop, on the other hand "break" exits for loop.
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
                //assert map != null;   //https://stackoverflow.com/questions/6176441/how-to-use-assert-in-android
                if (map != null) {
                    mPreviewSize = chooseOptimalSize(map.getOutputSizes(SurfaceTexture.class), rotatedWidth, rotatedHeight);   //chooses the closes possible match that it can find for the preview texture resolution that matches our screen device width & height.   See below for method implemented we created.
                }
                mCameraId = cameraId;    //OBTAINING THE id OF THE hardware CAMERA Device!
                return;
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

    }



    private static final int REQUEST_CAMERA_PERMISSION_RESULT = 0;   // Static variables are variables which belongs to the class and not to object(instance) .   Static variables are initialized only once, at the start of the execution.   These variables will be initialized first, before the initialization of any instance variables.    A single copy to be shared by all instances of the class.   A static variable can be accessed directly by the class name and doesn't need any object.


    private void connectCamera(){    //ask for user permission if require higher granted request, else connect & open up Camera.

        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);    //creates an instance of the Camera Object using/calling getSystemService() method.  get/retrieves info from AndroidOS
        try {
            //**For people have problems with Manifest.permission.CAMERA change to android.Manifest.permission.CAMERA
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {   //for MARSHMELLOW & LTR OS, nd to perform Permission checks    .M (Marshmello)  .O (Oreo)  .P (Pie)
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {   //If Permission alrdy granted, go ahead to open the Camera.
                    cameraManager.openCamera(mCameraId, mCameraDeviceStateCallback, mBackgroundHandler);
                } else {
                    if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {    //if Permission not granted yet, make a request to the user to accept.
                        Toast.makeText(this, "Video App requires access to camera", Toast.LENGTH_SHORT).show();
                    }
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION_RESULT);    // var "REQUEST_CAMERA_PERMISSION_RESULT" will store the result granted by user.  INVOKE/call "requestPermissions" method of the particular Android version that our Camera would need to run on!
                }
            }else{
                cameraManager.openCamera(mCameraId,mCameraDeviceStateCallback,mBackgroundHandler);   //else otherwise tf permission required by camera is lower than current Android OS vers of phone, proceed as intended :)
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {    // A callback to process whether permission request is successful .  Parses in & Process the request result granted by the user.

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);   //calls reference to same method defined in Super class "FragmentActivity" method to perform the check, this can be known as method @Override, cos my this subclass extends implementation from the SuperClass. Like having/adding addition lines of code for checking/ own Unique implementation base on requirements of my own App!
        if (requestCode == REQUEST_CAMERA_PERMISSION_RESULT){
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED){   //if grantResult is deemed to fail, meaning Permission is not granted.
                Toast.makeText(getApplicationContext(), "Application will not run if  permission not granted for camera services", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private CaptureRequest.Builder mCaptureRequestBuilder;

    private void startPreview(){
        SurfaceTexture surfaceTexture = textureView.getSurfaceTexture();
        surfaceTexture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());    //set the surfaceTexture orientation to the width & height.
        Surface previewSurface = new Surface(surfaceTexture);   //Surface inflate to the orientations as defined by/obtained from the  surfaceTexture field.

        try {
            mCaptureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            mCaptureRequestBuilder.addTarget(previewSurface);    //add previewSurface as the target to launch / load the texture onto that particular Surface.

            cameraDevice.createCaptureSession(Arrays.asList(previewSurface),
                    new CameraCaptureSession.StateCallback() {    //callback service from CaptureSession.
                        @Override
                        public void onConfigured(@NonNull CameraCaptureSession session) {
                            try {
                                session.setRepeatingRequest(mCaptureRequestBuilder.build(),
                                        null, mBackgroundHandler);   //repeatedly request a service from Camera, so that can take multiple pics.
                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                            }
                        }
                        @Override
                        public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                            Toast.makeText(getApplicationContext(), "Unable to setup camera preview ", Toast.LENGTH_SHORT).show();
                        }
                    }, null);

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

    }



    public void closeCamera(){    //when not in use, like when we pause the app, like put it into background, we want it to free up the camera so as not to hog the camera application!
        if (cameraDevice != null){
            cameraDevice.close();
            cameraDevice=null;
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
    protected void onCreate(@Nullable Bundle savedInstanceState) {     // A SavedStateHandle, which is very similar to a Bundle; it’s a key-value map of data. This SavedStateHandle “bundle” is in the ViewModel and it survives background process death. Any data you had to save before in onSaveInstanceState can now be saved in the SavedStateHandle. For example, the id of a user is something you might store in the SavedStateHandle.
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
                connectCamera();    //after all the set up configuration for the surfacetexture for the camera like the width height in the line above, we can now go ahead and connect up the Camera!
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        } else {    //if textureview has not yet been fully created/inflated, we have got a listener that notifies us when that happens.
            textureView.setSurfaceTextureListener(surfaceTextureListener);   //if textureView is not yet available, we have a listener to notify when it is ready. check out surfaceTextureListener implementation above.
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
