package pl.lukaszbyjos.emotionshooter.model.impl;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import pl.lukaszbyjos.emotionshooter.PhotoModel;
import pl.lukaszbyjos.emotionshooter.model.MainActivityModel;


public class MainActivityModelImpl implements MainActivityModel {

    private String converToBase64() {
        return "";
    }

    @Override
    @Deprecated
    public PhotoModel createPhotoFile(File storageDir) throws IOException {
        // Create an image file name
        String photoPath;
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
//        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        photoPath = image.getAbsolutePath();
        return new PhotoModel(photoPath, image);
    }

    @Override
    public void sendPhoto(final String photoPath) {
        Log.d("em", "sendPhoto: " + photoPath);
    }

    @Override
    public String savePhotoFile(byte[] cameraData, int cameraDataLength, Matrix rotateMatrix) {
        String timeStamp = new SimpleDateFormat("dd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        String path = Environment.getExternalStorageDirectory() + "/GDGPhotos/";
        File storageDir = new File(path);
        if (!storageDir.exists())
            storageDir.mkdirs();
        Bitmap bitmap = BitmapFactory.decodeByteArray(cameraData, 0, cameraDataLength);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(),
                rotateMatrix, false);
        File file = null;
        try {

            file = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );
            path = file.getPath();
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("D", "Save file on " + path);
        return path;
    }
}
