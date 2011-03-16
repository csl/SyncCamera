package com.camera;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

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
      /* ����User���w��SD�O�Хd */
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
    
    //�HSurfaceView�@���۾�Preview���� 
    mSurfaceView01 = (SurfaceView) findViewById(R.id.mSurfaceView1);
    
    // ô��SurfaceView�A���oSurfaceHolder���� 
    mSurfaceHolder01 = mSurfaceView01.getHolder();
    
    // Activity������@SurfaceHolder.Callback
    mSurfaceHolder01.addCallback(mCamera.this);
    
    //�B�~���]�w�w���j�p�]�w�A�b�����ϥ� 
    //mSurfaceHolder01.setFixedSize(320, 240);
    
    //�HSURFACE_TYPE_PUSH_BUFFERS(3)�@��SurfaceHolder��ܫ��A
    mSurfaceHolder01.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    
    initCamera();
    
    //�ۭq���m�۾��A�������۾��w����� 
    //resetCamera();
  }
  
  //
  private void initCamera()
  {
    if(!bIfPreview)
    {
      /* �Y�۾��D�b�w���Ҧ��A�h�}�Ҭ۾� */
      mCamera01 = Camera.open();
    }
    
    if (mCamera01 != null && !bIfPreview)
    {
      Log.i(TAG, "inside the camera");
      
      // �إ�Camera.Parameters���� 
      Camera.Parameters parameters = mCamera01.getParameters();
      
      // �]�w�ۤ��榡��JPEG
      parameters.setPictureFormat(PixelFormat.JPEG);
      
      // ���wpreview���ù��j�p 
      parameters.setPreviewSize(320, 240);
      
      //�]�w�Ϥ��ѪR�פj�p
      parameters.setPictureSize(320, 240);
      
      //�NCamera.Parameters�]�w��Camera
      mCamera01.setParameters(parameters);

      try
      {
    	  mCamera01.setPreviewDisplay(mSurfaceHolder01);
      }
      catch (Exception X)
      {
    	  
      }
      
      //�ߧY����Preview
      mCamera01.startPreview();
      bIfPreview = true;
    }
  }
  
  /* ����^���v�� */ 
  private void takePicture() 
  {
      /* ��O�Хd�s�b�~���\��ӡA�x�s�Ȧs�v���� */
      if(checkSDCard())
      {
    	    if (mCamera01 != null && bIfPreview) 
    	    {
    	      /* �I�stakePicture()��k��� */
    	      mCamera01.takePicture(shutterCallback, rawCallback, jpegCallback);
    	    }
      }
      else 
      {
        /* �O�Хd���s�b��ܴ��� */
        //mTextView01.setText
        //(
          //getResources().getText(R.string.str_err_nosd).toString()
        //);
      }	  
  }
  
  /* �۾����m */
  private void resetCamera()
  {
    if (mCamera01 != null && bIfPreview)
    {
      mCamera01.stopPreview();
      /* �����ǲߡA����Camera���� */
      //mCamera01.release();
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
      
      /* onPictureTaken�ǤJ���Ĥ@�ӰѼƧY���ۤ���byte */
      Bitmap bm = BitmapFactory.decodeByteArray(_data, 0, _data.length); 
      
      /* �إ߷s�� */
      File myCaptureFile = new File(strCaptureFilePath);
      try
      {
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
        /* �ĥ����Y���ɤ�k */
        bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);
        
        /* �I�sflush()��k�A��sBufferStream */
        bos.flush();
        
        /* ����OutputStream */
        bos.close();
        
        /* �N��ӤU�ӥB�x�s���������ɡA��ܥX�� */ 
        //mImageView01.setImageBitmap(bm);
        
        /* ��ܧ����ɡA�ߧY���m�۾��A�������w�� */
        resetCamera();
        
        /* �A���s�Ұʬ۾��~��w�� */
        initCamera();
      }
      catch (Exception e)
      {
        Log.e(TAG, e.getMessage());
      }
    }
  };
  
  /* �ۭq�R���ɮר�� */
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
    /* �P�_�O�Хd�O�_�s�b */
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
  }
  
  @Override
  public void surfaceCreated(SurfaceHolder surfaceholder)
  {
    // TODO Auto-generated method stub
    Log.i(TAG, "Surface Changed");
  }
  
  @Override
  public void surfaceDestroyed(SurfaceHolder surfaceholder)
  {
    // TODO Auto-generated method stub
    /* ��Surface���s�b�A�ݭn�R������ */
    try
    {
      delFile(strCaptureFilePath);
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
    Log.i(TAG, "Surface Destroyed");
  }
}
