package com.example.ge.trackfaceopencv1;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import static org.opencv.imgproc.Imgproc.rectangle;


public class MainActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener{

    // Used to load the 'native-lib' library on application startup.
    //static {
    //    System.loadLibrary("native-lib");
    //}

    private CameraBridgeViewBase openCvCameraView;
    private CascadeClassifier cascadeClassifier;
    private Mat grayscaleImage;
    private int absoluteFaceSize;


    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status){
            switch (status){
                case LoaderCallbackInterface.SUCCESS:
                    initializeOpenCVDependencies();
                    break;
                default:
                    super.onManagerConnected(status);
                    break;
            }
        }
    };

    private void initializeOpenCVDependencies(){
        try{
            InputStream is = getResources().openRawResource(R.raw.lbpcascade_frontalface);
            File casadeDir = getDir("cascade", Context.MODE_PRIVATE);
            File mCascadeFile = new File(casadeDir,"lbpcascade_frontalface.xml");
            FileOutputStream os = new FileOutputStream(mCascadeFile);

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1){
                os.write(buffer, 0, bytesRead);
            }
            is.close();
            os.close();

            cascadeClassifier = new CascadeClassifier(mCascadeFile.getAbsolutePath());

        } catch (Exception e) {
            Log.e("OpenCVActivity","Error Loading casade",e);
        }

        openCvCameraView.enableView();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        openCvCameraView = new JavaCameraView(this, -1);
        setContentView(openCvCameraView);
        openCvCameraView.setCvCameraViewListener(this);

        /*setContentView(R.layout.activity_main);

        // Example of a call to a native method
        TextView tv = (TextView) findViewById(R.id.sample_text);
        tv.setText(stringFromJNI());
        */
    }

    @Override
    public void onCameraViewStarted(int width, int height){
        grayscaleImage = new Mat(height, width, CvType.CV_8UC4);

        absoluteFaceSize = (int)(height * 0.2);

    }

    @Override
    public void onCameraViewStopped(){

    }

    @Override
    public Mat onCameraFrame(Mat aInputFrame){
        Imgproc.cvtColor(aInputFrame, grayscaleImage, Imgproc.COLOR_RGBA2RGB);

        MatOfRect faces = new MatOfRect();

        if (cascadeClassifier != null){
            cascadeClassifier.detectMultiScale(grayscaleImage,faces,1.1,2,2,new Size(absoluteFaceSize,absoluteFaceSize),new Size());
        }

        Rect[] faceArray = faces.toArray();
        for (int i = 0; i <faceArray.length; i++)

            //原代码是Core.rectangle(aInputFrame, faceArray[i].tl(), faceArray[i].br(), new Scalar(0, 255, 0, 255), 3);
            //但好像不行

            Imgproc.rectangle(aInputFrame, faceArray[i].tl(), faceArray[i].br(), new Scalar(0, 255, 0, 255), 3);

        return aInputFrame;
    }

    /*
    @Override
    public void onResume(){
        super.onResume();

        if(!OpenCVLoader.initDebug()){
            Log.e("log_wons", "OpenCV init error");

        }

        initializeOpenCVDependencies();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_4_0, this, mLoaderCallback);

    }
    */

    @Override
    public void onResume(){
        super.onResume();
        if (!OpenCVLoader.initDebug()){
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_4_0, this, mLoaderCallback);

        } else {
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }


    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    //public native String stringFromJNI();
}
