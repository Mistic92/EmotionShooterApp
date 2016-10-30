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

import javax.inject.Inject;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import pl.lukaszbyjos.emotionshooter.PhotoModel;
import pl.lukaszbyjos.emotionshooter.model.MainActivityModel;
import pl.lukaszbyjos.emotionshooter.network.PhotoUploadApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivityModelImpl implements MainActivityModel {

    private static final String TAG = "MAMI";
    @Inject
    protected PhotoUploadApi mPhotoUploadApi;

    public MainActivityModelImpl(PhotoUploadApi photoUploadApi) {
        mPhotoUploadApi = photoUploadApi;
    }

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
        File file = new File(photoPath);
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

        Call<ResponseBody> call = mPhotoUploadApi.upload(body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d(TAG, "onResponse: ");
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG, "onFailure: ");
            }
        });
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
//        bitmap = scaleDown(bitmap, 2000, false);
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

    private Bitmap scaleDown(Bitmap realImage, float maxImageSize,
                             boolean filter) {
        float ratio = Math.min(
                (float) maxImageSize / realImage.getWidth(),
                (float) maxImageSize / realImage.getHeight());
        int width = Math.round((float) ratio * realImage.getWidth());
        int height = Math.round((float) ratio * realImage.getHeight());

        Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width,
                height, filter);
        return newBitmap;
    }
}
