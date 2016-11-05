package pl.lukaszbyjos.emotionshooter.view;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hugo.weaving.DebugLog;
import pl.lukaszbyjos.emotionshooter.AppClass;
import pl.lukaszbyjos.emotionshooter.PhotoModel;
import pl.lukaszbyjos.emotionshooter.R;
import pl.lukaszbyjos.emotionshooter.events.PhotoSendEvent;
import pl.lukaszbyjos.emotionshooter.events.PhotoSendingEvent;
import pl.lukaszbyjos.emotionshooter.presenter.MainActivityPresenter;

public class MainActivity
        extends AppCompatActivity
        implements MainActivityView, ControlButtonsBar.ControlButtonsListener {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final String TAG = "em";
    private static final String[] REQUEST_PERMISSIONS = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private static final int REQUEST_PERMISSION_CODE = 233;
    @Inject
    protected MainActivityPresenter presenter;

    @BindView(R.id.photoHolder)
    ImageView mImageHolder;
    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.settings)
    ImageView mSettings;
    @BindView(R.id.controlButtonsBar)
    ControlButtonsBar mControlButtonsBar;
    private PhotoModel lastPhoto;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.unbind();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Glide.with(this)
                    .load(lastPhoto.getPhoto())
                    .into(mImageHolder);
            mControlButtonsBar.photoTakenSetup();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((AppClass) getApplication()).getAppComponent().inject(this);
        ButterKnife.bind(this);
        mControlButtonsBar.setControlButtonsListener(this);
        presenter.bind(this);
        Glide.with(this)
                .load(R.drawable.gdg_rzeszow_logo)
                .crossFade()
                .into(mImageHolder);
        if (!checkPermission()) {
            requestPermission();
        }

    }

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            try {
                lastPhoto = presenter.savePhoto();
            } catch (IOException ex) {
                Log.e(TAG, "openCamera: ", ex);
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (lastPhoto != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        lastPhoto.getPhoto());
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    @Override
    @Subscribe()
    @DebugLog
    public void sendingPhotoAction(PhotoSendingEvent photoSendingEvent) {
        mControlButtonsBar.sendingPhotoSetup();
    }

    @Override
    @Subscribe(threadMode = ThreadMode.MAIN)
    @DebugLog
    public void photoSendAction(PhotoSendEvent photoSendEvent) {
        if (photoSendEvent.isSend())
            mControlButtonsBar.photoSendSetup();
        else {
            mControlButtonsBar.photoNotSendSetup();
            Snackbar.make(mCoordinatorLayout, photoSendEvent.getReason(), Snackbar.LENGTH_LONG).show();
        }
        new Handler().postDelayed(() -> {
            cancelPhoto();
            mControlButtonsBar.startSetup();
        }, 2000);

    }


    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        for (String permission : REQUEST_PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, REQUEST_PERMISSIONS, REQUEST_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

            }
        }
    }

    @OnClick({R.id.settings})
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.settings:
                openSettings();
                break;
        }
    }

    private void openSettings() {
        Intent intent = new Intent(this, PrefsFragment.class);
        startActivity(intent);
    }

    private void cancelPhoto() {
        lastPhoto = null;
        presenter.resetLastPhoto();
        Glide.with(this)
                .load(R.drawable.gdg_rzeszow_logo)
                .crossFade()
                .into(mImageHolder);
    }

    @Override
    @DebugLog
    public void onTakePhotoClicked() {
        if (checkPermission())
            openCamera();
        else {
            requestPermission();
        }
    }

    @Override
    @DebugLog
    public void onSendPhotoClicked() {
        presenter.sendLastPhoto();
    }

    @Override
    @DebugLog
    public void onCancelPhotoClicked() {
        cancelPhoto();
    }
}
