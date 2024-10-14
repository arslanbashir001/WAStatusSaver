//package com.devatrii.statussaver;
//
//import static android.content.Context.CAMERA_SERVICE;
//
//import android.app.Activity;
//import android.content.Context;
//import android.database.Cursor;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.Matrix;
//import android.hardware.camera2.CameraAccessException;
//import android.hardware.camera2.CameraCharacteristics;
//import android.hardware.camera2.CameraManager;
//import android.media.ExifInterface;
//import android.net.Uri;
//import android.os.AsyncTask;
//import android.os.Build;
//import android.os.Environment;
//import android.os.Handler;
//import android.os.Looper;
//import android.provider.MediaStore;
//import android.util.SparseArray;
//import android.util.SparseIntArray;
//import android.view.Surface;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.RequiresApi;
//
//import com.google.android.gms.tasks.OnFailureListener;
//import com.google.android.gms.tasks.OnSuccessListener;
//
//import com.google.mlkit.vision.common.InputImage;
//import com.google.mlkit.vision.text.Text;
//import com.google.mlkit.vision.text.TextRecognition;
//import com.google.mlkit.vision.text.TextRecognizer;
//import com.google.mlkit.vision.text.chinese.ChineseTextRecognizerOptions;
//import com.google.mlkit.vision.text.devanagari.DevanagariTextRecognizerOptions;
//import com.google.mlkit.vision.text.japanese.JapaneseTextRecognizerOptions;
//import com.google.mlkit.vision.text.korean.KoreanTextRecognizerOptions;
//import com.google.mlkit.vision.text.latin.TextRecognizerOptions;
//
//import java.io.ByteArrayInputStream;
//import java.io.ByteArrayOutputStream;
//import java.io.File;
//import java.io.FileDescriptor;
//import java.io.IOException;
//import java.io.InputStream;
//import java.lang.ref.WeakReference;
//import java.util.Arrays;
//
//public class ImageUtils {
//    private int recognizeCounter=0;
//    String textRecognizedOne="";
//    String textRecognizedTwo="";
//    String textRecognizedThree="";
//    String textRecognizedFour="";
//    String textRecognizedFive="";
//    Text textOne,textTwo,textThree,textFour,textFive;
//    public static ImageUtils newInstance(){
//
//        return  new ImageUtils();
//    }
//    public void getImageBitmapToTextV2(Activity context,Bitmap bitmap){
//        if (bitmap==null){
//            return;
//        }
//        try {
//            InputImage image = InputImage.fromBitmap(bitmap, getRotationCompensation(context,false));
//
//            com.google.mlkit.vision.text.TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
//            reconizeTextAction(context,recognizer,image,1);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    private void recognizeSingleScript(Context context, com.google.mlkit.vision.text.TextRecognizer recognizer,InputImage image){
//        recognizer.process(image).addOnSuccessListener(new OnSuccessListener<Text>() {
//            @Override
//            public void onSuccess(Text text) {
//                if (listener!=null){
//                    if (text.getText().toString().isEmpty()){
//                        listener.OnTextNotFount();
//                    }else {
//
//                        listener.OnTextExtracted(text,text.getText().toString());
//                    }
//                }
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                if (listener!=null){
//                    listener.OnTextNotFount();
//                }
//            }
//        });
//    }
//
//    private void reconizeTextAction(Context context,com.google.mlkit.vision.text.TextRecognizer recognizer, InputImage image,int recognizeCount) {
//
//        recognizer.process(image)
//                .addOnSuccessListener(
//                        new OnSuccessListener<Text>() {
//                            @Override
//                            public void onSuccess(Text texts) {
//                                switch (recognizeCount){
//                                    case 1:
//                                        textRecognizedOne=texts.getText();
//                                        recognizeCounter=recognizeCount+1;
//                                        textOne=texts;
//                                        TextRecognizer recognizerOne =
//                                                TextRecognition.getClient(new ChineseTextRecognizerOptions.Builder().build());
//                                        reconizeTextAction(context,recognizerOne,image,recognizeCounter);
//                                        break;
//                                    case 2:
//                                        textRecognizedTwo=texts.getText();
//                                        recognizeCounter=recognizeCount+1;
//                                        textTwo=texts;
//                                        TextRecognizer recognizerTwo =
//                                                TextRecognition.getClient(new DevanagariTextRecognizerOptions.Builder().build());
//                                        reconizeTextAction(context,recognizerTwo,image,recognizeCounter);
//                                        break;
//                                    case 3:
//                                        textRecognizedThree=texts.getText();
//                                        recognizeCounter=recognizeCount+1;
//                                        textThree=texts;
//                                        TextRecognizer recognizerThree =
//                                                TextRecognition.getClient(new JapaneseTextRecognizerOptions.Builder().build());
//                                        reconizeTextAction(context,recognizerThree,image,recognizeCounter);
//                                        break;
//                                    case 4:
//                                        textRecognizedFour=texts.getText();
//                                        textFour=texts;
//                                        recognizeCounter=recognizeCount+1;
//                                        TextRecognizer recognizerFour =
//                                                TextRecognition.getClient(new KoreanTextRecognizerOptions.Builder().build());
//                                        reconizeTextAction(context,recognizerFour,image,recognizeCounter);
//                                        break;
//                                    case 5:
//                                        textRecognizedFive=texts.getText();
//                                        textFive=texts;
//                                        int[] array=new int[]{
//                                                textRecognizedOne.trim().length(),
//                                                textRecognizedTwo.trim().length(),
//                                                textRecognizedThree.trim().length(),
//                                                textRecognizedFour.trim().length(),
//                                                textRecognizedFive.trim().length(),
//                                        };
//                                        Arrays.sort(array);
//                                        int maxLength=array[array.length-1];
//                                        String finalText="";
//                                        Text finalTextObj=null;
//                                        if (maxLength==textRecognizedOne.length()){
//                                            finalText=textRecognizedOne;
//                                            finalTextObj=textOne;
//                                        }else if (maxLength==textRecognizedTwo.length()){
//                                            finalText=textRecognizedTwo;
//                                            finalTextObj=textTwo;
//                                        }else if (maxLength==textRecognizedThree.length()){
//                                            finalText=textRecognizedThree;
//                                            finalTextObj=textThree;
//                                        }else if (maxLength==textRecognizedFour.length()){
//                                            finalText=textRecognizedFour;
//                                            finalTextObj=textFour;
//                                        }else if (maxLength==textRecognizedFive.length()){
//                                            finalText=textRecognizedFive;
//                                            finalTextObj=textFive;
//                                        }
//                                        if (listener!=null){
//                                            if (finalText.isEmpty()){
//                                                listener.OnTextNotFount();
//                                            }else {
//                                                listener.OnTextExtracted(finalTextObj,finalText);
//                                            }
//                                        }
//                                        break;
//
//                                }
//                            }
//                        })
//                .addOnFailureListener(
//                        new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
//                                if (listener!=null){
//                                    listener.OnTextNotFount();
//                                }
//                                e.printStackTrace();
//                            }
//                        });
//
//    }
//    private OnTextExtractedListener listener;
//
//    public void setListener(OnTextExtractedListener listener) {
//        this.listener = listener;
//    }
//
//    public abstract static class OnTextExtractedListener{
//        public abstract void OnTextExtracted(Text finalTextObj,String tex);
//        public abstract void OnTextNotFount();
//    }
//    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
//    static {
//        ORIENTATIONS.append(Surface.ROTATION_0, 0);
//        ORIENTATIONS.append(Surface.ROTATION_90, 90);
//        ORIENTATIONS.append(Surface.ROTATION_180, 180);
//        ORIENTATIONS.append(Surface.ROTATION_270, 270);
//    }
//    public static InputStream bitmapToInputStream(Bitmap bitmap) {
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
//        byte[] bitmapBytes = outputStream.toByteArray();
//        return new ByteArrayInputStream(bitmapBytes);
//    }
//    public static Matrix getRotation(Context context,InputStream inputStream){
//        ExifInterface exif = null;
//        Matrix matrix = new Matrix();
//        try {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                exif = new ExifInterface(inputStream);
//            }
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        if (exif==null){
//            return matrix;
//        }
//        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
//
//        if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
//            matrix.postRotate(90);
//
//            return matrix;
//        } else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
//            matrix.postRotate(180);
//
//            return matrix;
//        } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
//            matrix.postRotate(270);
//
//            return matrix;
//        }
//
//        return matrix;
//    }
//    public static int getRotationCompensation( Activity activity, boolean isFrontFacing)
//            throws CameraAccessException {
//
//        int deviceRotation = activity.getWindowManager().getDefaultDisplay().getRotation();
//        int rotationCompensation = ORIENTATIONS.get(deviceRotation);
//        CameraManager cameraManager = (CameraManager) activity.getSystemService(CAMERA_SERVICE);
//        String cameraId=cameraManager.getCameraIdList()[0];
//        int sensorOrientation = cameraManager
//                .getCameraCharacteristics(cameraId)
//                .get(CameraCharacteristics.SENSOR_ORIENTATION);
//
//        if (isFrontFacing) {
//            rotationCompensation = (sensorOrientation + rotationCompensation) % 360;
//        } else { // back-facing
//            rotationCompensation = (sensorOrientation - rotationCompensation + 360) % 360;
//        }
//        rotationCompensation = (sensorOrientation - rotationCompensation + 360) % 360;
//        return rotationCompensation;
//    }
//    public static String getImagePathFromBitmap(Context context, Bitmap bitmap) {
//        Uri uri = getImageUriFromBitmap(context, bitmap);
//        String[] projection = { MediaStore.Images.Media.DATA };
//        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
//        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//        cursor.moveToFirst();
//        String path = cursor.getString(column_index);
//        cursor.close();
//        return path;
//    }
//
//    public static Uri getImageUriFromBitmap(Context context, Bitmap bitmap) {
//        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
//        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Title", null);
//        return Uri.parse(path);
//    }
//}


//usage

//import android.graphics.Bitmap;
//import android.graphics.Canvas;
//import android.graphics.Color;
//import android.graphics.Matrix;
//import android.graphics.Paint;
//import android.graphics.Rect;
//import android.media.ExifInterface;
//import android.widget.Toast;
//
//import java.util.List;
//
//private void scanImage(Bitmap bitmap) {
//    if (bitmap == null) {
//        return;
//    }
//    try {
//        ImageUtils imageUtils = ImageUtils.newInstance();
//        Bitmap mutableBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
//        Matrix matrix = new Matrix();
//        switch (orientation) {
//            case ExifInterface.ORIENTATION_ROTATE_90:
//                matrix.postRotate(90);
//                break;
//            case ExifInterface.ORIENTATION_ROTATE_180:
//                matrix.postRotate(180);
//                break;
//            case ExifInterface.ORIENTATION_ROTATE_270:
//                matrix.postRotate(270);
//                break;
//            default:
//                break;
//        }
//
//        Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
//        int[] pixels = new int[rotatedBitmap.getWidth() * rotatedBitmap.getHeight()];
//        rotatedBitmap.getPixels(pixels, 0, rotatedBitmap.getWidth(), 0, 0, rotatedBitmap.getWidth(), rotatedBitmap.getHeight());
//        mutableBitmap.setPixels(pixels, 0, rotatedBitmap.getWidth(), 0, 0, rotatedBitmap.getWidth(), rotatedBitmap.getHeight());
//
//        Canvas canvas = new Canvas(mutableBitmap);
//        imageUtils.setListener(new ImageUtils.OnTextExtractedListener() {
//            @Override
//            public void OnTextExtracted(Text finalTextObj, String tex) {
//                extractedText = tex;
//                List<Text.TextBlock> blocks = finalTextObj.getTextBlocks();
//
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            for (Text.TextBlock block : blocks) {
//                                // Iterate over each word in the block
//                                List<Text.Line> lines = block.getLines();
//                                for (Text.Line line : lines) {
//                                    List<Text.Element> elements = line.getElements();
//                                    for (Text.Element element : elements) {
//                                        Rect rect = element.getBoundingBox();
//                                        // Draw a rectangle around the word
//                                        Paint paint = new Paint();
//                                        paint.setColor(Color.RED);
//                                        paint.setStyle(Paint.Style.STROKE);
//                                        paint.setStrokeWidth(5f);
//                                        if (getActivity() != null) {
//                                            getActivity().runOnUiThread(new Runnable() {
//                                                @Override
//                                                public void run() {
//                                                    canvas.drawRect(rect, paint);
//                                                }
//                                            });
//                                        }
//                                    }
//                                }
//                            }
//                        } catch (Exception e) {
//                        }
//                    }
//                }).start();
//
//                cropImageView.setImageBitmap(mutableBitmap);
//                dismissDialog();
//
//            }
//
//            @Override
//            public void OnTextNotFount() {
//                extractedText = "";
//                try {
//                    Toast.makeText(getContext(), "Text not found", Toast.LENGTH_SHORT).show();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                dismissDialog();
//            }
//        });
//        showDialog();
//        imageUtils.getImageBitmapToTextV2(getActivity(), mutableBitmap);
//        btnScan.setText("Translate Now");
//    } catch (OutOfMemoryError error) {
//    }
//}