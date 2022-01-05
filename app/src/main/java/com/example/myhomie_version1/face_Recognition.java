package com.example.myhomie_version1;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.gpu.GpuDelegate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class face_Recognition {
    // import interpreter
    private Interpreter interpreter;
    // define input size of model
    private int INPUT_SIZE;
    // define heigth and width of frame
    private int height = 0;
    private int width = 0;
    //define Gpudelegate
    private GpuDelegate gpuDelegate = null;
    // this is used to run model using Gpu
    // define CascadeClassifer
    private CascadeClassifier cascadeClassifier;

    DatabaseReference mData;

    //Create
    face_Recognition(AssetManager assetManager, Context context, String modelPath, int input_size) throws IOException {
        //call this class in CameraActivity
        //get inputsize
        INPUT_SIZE=input_size;
        //set gpu for interpreter
        Interpreter.Options options=new Interpreter.Options();
        gpuDelegate=new GpuDelegate(); // if you are using efficient model, you can use gpu
        //load model
        //before load add number of thread
        options.setNumThreads(4);
        interpreter=new Interpreter(loadModel(assetManager,modelPath),options);
        //when model is succesfully load
        Log.d("face_Recognition", "Model is loader");
        Toast.makeText(context, "Model is loaded", Toast.LENGTH_SHORT).show();

        // load harr cascade model
        try{
            //define input stream to read haar cascade file
            InputStream inputStream=context.getResources().openRawResource(R.raw.haarcascade_frontalface_alt);
            //create a new folder to save classifier
            File cascadeDir = context.getDir("cascade", Context.MODE_PRIVATE);
            //create a new cascade file in that folder
            File mCascadeFile = new File(cascadeDir, "haarcascade_frontalface_alt");
            // define output stream to save haarcascade_frontalface_alt in mCascadeFile
            FileOutputStream outputStream = new FileOutputStream(mCascadeFile);
            //create empty  byte buffer to store byte
            byte[] buffer = new byte[4096];
            int byteRead;
            // read byte in loop, when it read -1 that means  no data to read
            while ((byteRead=inputStream.read(buffer)) != -1){
                outputStream.write(buffer,0,byteRead);

            }
            // when reading file is complete
            inputStream.close();
            outputStream.close();

            // load cascade classifier
            cascadeClassifier = new CascadeClassifier(mCascadeFile.getAbsolutePath());
            // if cascade classifier is succesfully load
            Log.d("face_recognition","Classifier  is loaded");
            Toast.makeText(context, "Classifier is loaded", Toast.LENGTH_SHORT).show();


        }
        catch (IOException e){
            e.printStackTrace();
        }


    }

    public Mat recognizeImage(Mat mat_image){

        Core.flip(mat_image.t(),mat_image,1 );
        Mat grayscaleImage=new Mat();
        Imgproc.cvtColor(mat_image,grayscaleImage,Imgproc.COLOR_RGBA2GRAY);
        height=grayscaleImage.height();
        width=grayscaleImage.width();
        int absoluteFaceSize=(int) (height*0.1);
        MatOfRect faces=new MatOfRect();
        if (cascadeClassifier != null){
            cascadeClassifier.detectMultiScale(grayscaleImage,faces,1.1,2,2,
                    new Size(absoluteFaceSize,absoluteFaceSize),new Size());
        }
        Rect[] faceArray=faces.toArray();
        for (int i=0;i<faceArray.length;i++){
            Imgproc.rectangle(mat_image,faceArray[i].tl(), faceArray[i].br(), new Scalar(0,255,0,255), 2);
            Rect roi=new Rect((int)faceArray[i].tl().x,(int)faceArray[i].tl().y,
                    ((int)faceArray[i].br().x) - ((int)faceArray[i].tl().x),
                    ((int)faceArray[i].br().y) - ((int)faceArray[i].tl().y));
            Mat cropped_rgb=new Mat(mat_image,roi);
            Bitmap bitmap=null;
            bitmap=Bitmap.createBitmap(cropped_rgb.cols(),cropped_rgb.rows(),Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(cropped_rgb,bitmap);
            Bitmap scaledBitmap=Bitmap.createScaledBitmap(bitmap,INPUT_SIZE,INPUT_SIZE,false);
            ByteBuffer byteBuffer=convertBitmapToByteBuffer(scaledBitmap);
            float[][] face_value=new float[1][1];
            interpreter.run(byteBuffer,face_value);
            Log.d("face_Recognition" ,"Out: " + Array.get(Array.get(face_value,0), 0));
            float read_face=(float) Array.get(Array.get(face_value,0),0);
            String face_name=get_face_name(read_face);
            Imgproc.putText(mat_image,""+face_name,
                    new Point((int)faceArray[i].tl().x+10, (int)faceArray[i].tl().y+20),
                    1, 1.5,new Scalar(255,255,255,150), 2);

        }

        Core.flip(mat_image.t(),mat_image,0);

        return mat_image;
    }

    private String get_face_name(float read_face) {

        mData = FirebaseDatabase.getInstance().getReference();
        String val="";
        if (read_face>=0 & read_face<0.5){
            val="Couteney Cox";
        }
        else if (read_face>=0.5 & read_face<1.5){
            val="Arnold Schwarzenegger";
        }
        else if (read_face>=1.5 & read_face<2.5){
            val="Bhuvan Bam";
        }
        else if (read_face>=2.5 & read_face<3.5){
            val="Hardik Pandya";
        }
        else if (read_face>=3.5 & read_face<4.5){
            val="David Schwimer";
        }
        else if (read_face>=4.5 & read_face<5.5){
            val="Matt Leblanc";
        }
        else if (read_face>=5.5 & read_face<6.5){
            val="Simon Helberg";
        }
        else if (read_face>=6.5 & read_face<7.5){
            val="Scarllet Johhanson ";
        }
        else if (read_face>=7.5 & read_face<8.5){
            val="Pankaj Tripathi";
        }
        else if (read_face>=8.5 & read_face<9.5){
            val="Matthew Perry";
        }
        else if (read_face>=9.5 & read_face<10.5){
            val="Sylvester Stallon";
        }
        else if (read_face>=10.5 & read_face<11.5){
            val="Messi";
        }
        else if (read_face>=11.5 & read_face<12.5){
            val="Jim Parsons";
        }
        else if (read_face>=12.5 & read_face<13.5){
            val="phuong duy";
            mData.child("Cua").setValue("ON");
        }
        else if (read_face>=13.5 & read_face<14.5){
            val="Lisa Kudrow";
        }
        else if (read_face>=14.5 & read_face<15.5){
            val="Mohhamed ali";
        }
        else if (read_face>=15.5 & read_face<=16.5){
            val="Bratt Pit";
        }
        else if (read_face>=16.5 & read_face<17.5){
            val="Rolnaldo";
        }
        else if (read_face>=17.5 & read_face<18.5){
            val="Virat Kohli";
        }
        else if (read_face>=18.5 & read_face<19.5){
            val="Angelina Jolie";
        }
        else if (read_face>=19.5 & read_face<20.5){
            val="Kurnal nayya";
        }
        else if (read_face>=20.5 & read_face<21.5){
            val="Arnold Schwarzenegger";
        }
        else if (read_face>=21.5 & read_face<22.5){
            val="Arnold Schwarzenegger";
        }
        else if (read_face>=22.5 & read_face<23.5){
            val="Arnold Schwarzenegger";
        }
        else if (read_face>=23.5 & read_face<24.5){
            val="Dhoni";
        }
        else if (read_face>=24.5 & read_face<25.5){
            val="Pewdiepie";
        }
        else if (read_face>=25.5 & read_face<=26.5){
            val="Arnold Schwarzenegger";
        }
        else if (read_face>=26.5 & read_face<27.5){
            val="Arnold Schwarzenegger";
        }
        else if (read_face>=27.5 & read_face<28.5){
            val="Arnold Schwarzenegger";
        }
        else{
            val="Suresh Raina";
        }
        return val;
    }

    private ByteBuffer convertBitmapToByteBuffer(Bitmap scaledBitmap) {
        ByteBuffer byteBuffer;
        int input_size=INPUT_SIZE;
        byteBuffer=ByteBuffer.allocateDirect(4*1*input_size*input_size*3);
        byteBuffer.order(ByteOrder.nativeOrder());
        int[] intValues=new int[input_size*input_size];
        scaledBitmap.getPixels(intValues,0,scaledBitmap.getWidth(),0,0,scaledBitmap.getWidth(),scaledBitmap.getHeight());
        int pixels=0;
        for(int i=0;i<input_size;++i){
            for (int j=0;j<input_size;++j){
                final int val = intValues[pixels++];
                byteBuffer.putFloat((((val>>16)&0xFF))/255.0f);
                byteBuffer.putFloat((((val>>8)&0xFF))/255.0f);
                byteBuffer.putFloat(((val&0xFF))/255.0f);

            }
        }
        return byteBuffer;
    }

    //this function to load model
    private MappedByteBuffer loadModel(AssetManager assetManager, String modelPath) throws IOException {
        // this will give description of modelPath
        AssetFileDescriptor assetFileDescriptor=assetManager.openFd(modelPath);
        //create the input stream to read modelpath
        FileInputStream inputStream=new FileInputStream(assetFileDescriptor.getFileDescriptor());
        FileChannel fileChannel=inputStream.getChannel();
        long startOffset=assetFileDescriptor.getStartOffset();
        long declaredLength=assetFileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    };
}

