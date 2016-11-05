package pl.lukaszbyjos.emotionshooter.model.impl;

import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.inject.Inject;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import pl.lukaszbyjos.emotionshooter.HostSelectionInterceptor;
import pl.lukaszbyjos.emotionshooter.PhotoModel;
import pl.lukaszbyjos.emotionshooter.events.PhotoSendEvent;
import pl.lukaszbyjos.emotionshooter.events.PhotoSendingEvent;
import pl.lukaszbyjos.emotionshooter.model.MainActivityModel;
import pl.lukaszbyjos.emotionshooter.network.PhotoUploadApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivityModelImpl implements MainActivityModel {

    private static final String TAG = "ES";
    @Inject
    protected PhotoUploadApi mPhotoUploadApi;
    @Inject
    protected HostSelectionInterceptor mHostSelectionInterceptor;
    @Inject
    protected SharedPreferences mSharedPreferences;

    public MainActivityModelImpl(PhotoUploadApi photoUploadApi,
                                 HostSelectionInterceptor hostSelectionInterceptor,
                                 SharedPreferences sharedPreferences) {
        mPhotoUploadApi = photoUploadApi;
        mHostSelectionInterceptor = hostSelectionInterceptor;
        mSharedPreferences = sharedPreferences;
    }

    private void refreshServerIp(){
        final String url  = mSharedPreferences.getString("server_ip","192.168.1.106:8080/api/");
        mHostSelectionInterceptor.setParameters(url);
    }

    @Override
    public PhotoModel createPhotoFile() throws IOException {
        // Create an image file name
        String path = Environment.getExternalStorageDirectory() + "/GDGPhotos/";
        File storageDir = new File(path);
        if (!storageDir.exists())
            storageDir.mkdirs();
        String photoPath;
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
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
        refreshServerIp();
        Log.d("em", "sendPhoto: " + photoPath);
        EventBus.getDefault().post(new PhotoSendingEvent());
        File file = new File(photoPath);
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

        Call<ResponseBody> call = mPhotoUploadApi.upload(body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d(TAG, "onResponse: ");
                EventBus.getDefault().post(new PhotoSendEvent(true));
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG, "onFailure: ");
                EventBus.getDefault().post(new PhotoSendEvent(false, t.getLocalizedMessage()));
            }
        });
    }

}
