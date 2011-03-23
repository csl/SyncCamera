package com.camera;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

//import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;

import android.hardware.Camera;

import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class mCamera extends Activity implements SurfaceHolder.Callback
{
  private Camera mCamera01;
  
  //private ImageView mImageView01;
  //private TextView mTextView01;
  
  public static SensorManager mSensorManager = null; 
  private float mCameraOrientation;//拍照時候的方向 
  private int jpegQuality;
  private float fAccuracy, fBearing; 
  private String TAG = "SyncCamera";
  private SurfaceView mSurfaceView01;
  private SurfaceHolder mSurfaceHolder01;
  //private int intScreenX, intScreenY;
  
  private boolean bIfPreview = false;
  private String strCaptureFilePath = "/sdcard/camera_snap.jpg";
  
  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    requestWindowFeature(Window.FEATURE_NO_TITLE);

    setContentView(R.layout.mcamera);
    
    if(!checkSDCard())
    {
      mMakeTextToast
      (
        getResources().getText(R.string.str_err_nosd).toString(),
        true
      );
    }
    
    mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

    if (mSensorManager != null) {
        if(mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_UI)){
        	//openMessageDialog(" sensors enable!");
        }else{
        	//openMessageDialog(" sensors disable!");
        }
        mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_UI);
     } else Log.d(TAG, "No sensors!");
  
    DisplayMetrics dm = new DisplayMetrics();
    getWindowManager().getDefaultDisplay().getMetrics(dm);

    //intScreenX = dm.widthPixels;
    //intScreenY = dm.heightPixels;
    //Log.i(TAG, Integer.toString(intScreenX));
    
    //import android.content.pm.ActivityInfo;
    //this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    
    //mTextView01 = (TextView) findViewById(R.id.myTextView1);
    //mImageView01 = (ImageView) findViewById(R.id.myImageView1);
    
    mSurfaceView01 = (SurfaceView) findViewById(R.id.mSurfaceView1);
    mSurfaceHolder01 = mSurfaceView01.getHolder();
    mSurfaceHolder01.addCallback(this);
    
    //mSurfaceHolder01.setFixedSize(320, 240);
    
    mSurfaceHolder01.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    
    
    bIfPreview = false;
    //initCamera();
    //resetCamera();
  }
  
  private SensorEventListener mSensorListener = new SensorEventListener() 
  {
	               private static final int matrix_size = 16;
	               float[] R = new float[matrix_size];
	               float[] outR = new float[matrix_size];
	               float[] I = new float[matrix_size];
	               float[] values = new float[3];
	               float[] mags = null;
	               float[] accels = null;
	               public void onAccuracyChanged(Sensor sensor, int accuracy) {
	                       //Log.d (TAG, Thread.currentThread().getId()+"  onAccuracyChanged......");
	               }
	               public void onSensorChanged(SensorEvent event) {
	                       //if(isTaken)return ;
	                       //Log.d (TAG, Thread.currentThread().getId()+"  onSensorChanged......");
	                       if (event.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE) return;
	                       switch (event.sensor.getType()) {
	                           case Sensor.TYPE_MAGNETIC_FIELD:
	                               mags = event.values;
	                               break;
	                       case Sensor.TYPE_ACCELEROMETER:
	                               accels = event.values;
	                               break;
	                   }
	                       if (mags != null && accels != null) {
	                               //Log.d ("hei1798", "  onSensorChanged and caculator some things!");
	                           SensorManager.getRotationMatrix(R, I, accels, mags);
	                           // Correct if screen is in Landscape
	                           SensorManager.remapCoordinateSystem(R, SensorManager.AXIS_X, SensorManager.AXIS_Z, outR);
	                           SensorManager.getOrientation(outR, values);
	                           // Convert radian to degree
	                           // Bearing: (0-North, 90-East, 180-South, 270-North)
	   //                        fBearing =values[0] *v;// 57.32=180 / 3.14
	   //                        if (fBearing < 0) fBearing +=i360;
	                           fBearing =values[0];
	  
	                           // Convert radian to degree
                           // Roll: (0-horizontal, 90-vertical, 180-horizontal, upside down, 270-vertical, upside down  57.32=180 / 3.14
   //                        mCameraOrientation =(int)(values[2]*v) + i90;
	                           mCameraOrientation =values[2];

	                      		//mSensorManager.unregisterListener(mSensorListener);
	                           mags = null;
	                           accels = null;
	                   }
	                   }
	           }; 
    
  private void initCamera()
  {
    if(!bIfPreview)
    {
      //mCamera01.release();
 	  mCamera01 = Camera.open();
    }    
    
    if (mCamera01 != null && !bIfPreview)
    {
      Log.i(TAG, "inside the camera");
      
      Camera.Parameters parameters = mCamera01.getParameters();
      parameters.setPictureFormat(PixelFormat.JPEG);
      
      //parameters.setPreviewSize(360, 240);
      //parameters.setPictureSize(360, 240);
      
      mCamera01.setParameters(parameters);

      try
      {
    	mCamera01.setPreviewDisplay(mSurfaceHolder01);
      }
      catch (Exception X)
      {  
    	  mCamera01.release();
    	  mCamera01 = null;            
      }
      
      mCamera01.startPreview();
      bIfPreview = true;
    }
  }
  
  
  
  private void takePicture() 
  {
      if(checkSDCard())
      {
    	   mSensorManager.unregisterListener(mSensorListener);

           try{
    			//計算方位，度數
    			fBearing =(float) (fBearing*180/3.14);// 57.32=180 / 3.14
    			if (fBearing < 0) fBearing +=360;
    			int value =(int)( mCameraOrientation*180/3.14) + 90;
    			//openMessageDialog(Integer.toString(value));
    			int angle = roundOrientation(value);
    			try{
    				mCamera01.setDisplayOrientation(angle);
    			}
    			catch (Exception X)
    			{
    				
    			} 		 
    		}catch (Exception X)
    		{
    		}	     	   
    	  
    	    if (mCamera01 != null && bIfPreview) 
    	    {
    	      mCamera01.takePicture(shutterCallback, rawCallback, jpegCallback);
    	    }
      }
      else 
      {
      }	  
  }
  
  private void resetCamera()
  {
    if (mCamera01 != null && bIfPreview)
    {
        if (mSensorManager != null) {
            if(mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_UI)){
            	//openMessageDialog(" sensors enable!");
            }else{
            	//openMessageDialog(" sensors disable!");
            }
            mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_UI);
         } else Log.d(TAG, "No sensors!");
    	
        mCamera01.stopPreview();
        mCamera01.release();
        mCamera01 = null;
        bIfPreview = false;
    }
  }
   
  private ShutterCallback shutterCallback = new ShutterCallback() 
  { 
    public void onShutter() 
    { 
      // Shutter has closed 
    } 
  }; 
   
  private PictureCallback rawCallback = new PictureCallback() 
  { 
    public void onPictureTaken(byte[] _data, Camera _camera) 
    { 
      // TODO Handle RAW image data 
    } 
  }; 

  private PictureCallback jpegCallback = new PictureCallback() 
  {
    public void onPictureTaken(byte[] _data, Camera _camera)
    {
      // TODO Handle JPEG image data
      
      Bitmap bm = BitmapFactory.decodeByteArray(_data, 0, _data.length); 
      
      File myCaptureFile = new File(strCaptureFilePath);
      openMessageDialog(strCaptureFilePath);
      
      try
      {
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
        bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);
        bos.flush();
        
        bos.close();
        
        //mImageView01.setImageBitmap(bm);
        
        resetCamera();        
        initCamera();
      }
      catch (Exception e)
      {
        Log.e(TAG, e.getMessage());
      }
    }
  };
  
  private void delFile(String strFileName)
  {
    try
    {
      File myFile = new File(strFileName);
      if(myFile.exists())
      {
        myFile.delete();
      }
    }
    catch (Exception e)
    {
      Log.e(TAG, e.toString());
      e.printStackTrace();
    }
  }
  
  public void mMakeTextToast(String str, boolean isLong)
  {
    if(isLong==true)
    {
      Toast.makeText(mCamera.this, str, Toast.LENGTH_LONG).show();
    }
    else
    {
      Toast.makeText(mCamera.this, str, Toast.LENGTH_SHORT).show();
    }
  }
  
  private boolean checkSDCard()
  {
    if(android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
    {
      return true;
    }
    else
    {
      return false;
    }
  }
  
  @Override
  public void surfaceChanged(SurfaceHolder surfaceholder, int format, int w, int h)
  {
    // TODO Auto-generated method stub
    Log.i(TAG, "Surface Changed");
    
    if(bIfPreview){
    	mCamera01.stopPreview();
        bIfPreview = false;
   	 }
    
   	 try {
   		 
         try{
 			//計算方位，度數
 			fBearing =(float) (fBearing*180/3.14);// 57.32=180 / 3.14
 			if (fBearing < 0) fBearing +=360;
 			int value =(int)( mCameraOrientation*180/3.14) + 90;
 			//openMessageDialog(Integer.toString(value));
 			int angle = roundOrientation(value);
 			try{
 				mCamera01.setDisplayOrientation(angle);
 			}
 			catch (Exception X)
 			{
 				
 			} 		 
 		}catch (Exception X)
 		{
 			
 		}	 
   		 
    	  mCamera01.setPreviewDisplay(surfaceholder);
    	  mCamera01.startPreview();
    	  bIfPreview = true;
   	 } catch (IOException e) {
    	  // TODO Auto-generated catch block
    	  e.printStackTrace();
    }
  }
  
  @Override
  public void surfaceCreated(SurfaceHolder surfaceholder)
  {
    // TODO Auto-generated method stub
	  	Log.i(TAG, "surfaceCreated");
    	//mCamera01 = Camera.open();
	  	initCamera();
   }
  
  @Override
  public void surfaceDestroyed(SurfaceHolder surfaceholder)
  {
    // TODO Auto-generated method stub
    try
    {
    	mSensorManager.unregisterListener(mSensorListener); 
    	mCamera01.stopPreview();
    	mCamera01.release();
    	mCamera01 = null;
    	bIfPreview = false;
       //delFile(strCaptureFilePath);
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
    Log.i(TAG, "Surface Destroyed");
  }
  
  private int roundOrientation(int orientationInput) 
  {
	  int orientation = orientationInput;
	  int retVal;

	  if (orientation == OrientationEventListener.ORIENTATION_UNKNOWN) 
	  {
		  orientation = 0;
	  }
	  orientation = orientation % 360;
	  
	  if (orientation < 45) {
		  retVal = 0;
	  } else if (orientation <135) {//90+45
		  retVal = 90;
	  } else if (orientation < 225) {//180+45
		  retVal = 180;
	  } else if (orientation < 315) {//270+45
		  retVal = 270;
	  } else {
		  retVal = 0;
	  }
	  // Log.d(TAG,"roundOrientation o:"+orientationInput+"　r:"+retVal);
	   return retVal;
	} 
  
  //error message
  private void openMessageDialog(String info)
	{
	    new AlertDialog.Builder(this)
	    .setTitle("message")
	    .setMessage(info)
	    .setPositiveButton("OK",
	        new DialogInterface.OnClickListener()
	        {
	         public void onClick(DialogInterface dialoginterface, int i)
	         {
	         }
	        }
	        )
	    .show();
	}
}
