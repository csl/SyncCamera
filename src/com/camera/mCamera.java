package com.camera;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;

//import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;

import android.hardware.Camera;

import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
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
  private String TAG = "HIPPO";
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
    	    if (mCamera01 != null && bIfPreview) 
    	    {
    	      mCamera01.takePicture(shutterCallback, rawCallback, jpegCallback);
    	    }
      }
      else 
      {
        //mTextView01.setText
        //(
          //getResources().getText(R.string.str_err_nosd).toString()
        //);
      }	  
  }
  
  private void resetCamera()
  {
    if (mCamera01 != null && bIfPreview)
    {
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
    mCamera01 = Camera.open();
  }
  
  @Override
  public void surfaceDestroyed(SurfaceHolder surfaceholder)
  {
    // TODO Auto-generated method stub
    try
    {
    	mCamera01.stopPreview();
    	mCamera01.release();
    	mCamera01 = null;
    	bIfPreview = false;
       delFile(strCaptureFilePath);
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
    Log.i(TAG, "Surface Destroyed");
  }
}
