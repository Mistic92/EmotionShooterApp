package pl.lukaszbyjos.emotionshooter.view;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.ragnarok.rxcamera.RxCamera;
import com.ragnarok.rxcamera.config.RxCameraConfig;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.lukaszbyjos.emotionshooter.AppClass;
import pl.lukaszbyjos.emotionshooter.PhotoModel;
import pl.lukaszbyjos.emotionshooter.R;
import pl.lukaszbyjos.emotionshooter.presenter.MainActivityPresenter;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;

public class MainActivity extends AppCompatActivity implements MainActivityView {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final String TAG = "em";
    private static final String[] REQUEST_PERMISSIONS = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private static final int REQUEST_PERMISSION_CODE = 233;
    @Inject
    protected MainActivityPresenter presenter;
    @BindView(R.id.buttonTakePhoto)
    Button mButtonTakePhoto;
    @BindView(R.id.buttonSendPhoto)
    Button mButtonSendPhoto;
    @BindView(R.id.imageHolder)
    ImageView mImageHolder;
    @BindView(R.id.surfaceHolder)
    SurfaceView mSurfaceHolder;
    @BindView(R.id.progressBar)
    ContentLoadingProgressBar mProgressBar;
    private PhotoModel lastPhoto;
    private RxCamera camera;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.unbind();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Log.d(TAG, "onActivityResult: " + lastPhoto.getPhotoPath());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((AppClass) getApplication()).getAppComponent().inject(this);
        ButterKnife.bind(this);
        presenter.bind(this);
        mProgressBar.hide();

        if (!checkPermission()) {
            requestPermission();
        } else {
            openCamera();
        }

    }

    private void openCamera() {
        RxCameraConfig config = new RxCameraConfig.Builder()
                .useBackCamera()
                .setAutoFocus(true)
                .setPreferPreviewFrameRate(15, 30)
                .setPreferPreviewSize(new Point(640, 480), false)
                .setHandleSurfaceEvent(true)
                .build();
        RxCamera.open(this, config).flatMap(new Func1<RxCamera, Observable<RxCamera>>() {
            @Override
            public Observable<RxCamera> call(RxCamera rxCamera) {
                Log.d(TAG, "isopen: " + rxCamera.isOpenCamera() + ", thread: " + Thread.currentThread());
                camera = rxCamera;
                return rxCamera.bindSurface(mSurfaceHolder);
            }
        }).flatMap(new Func1<RxCamera, Observable<RxCamera>>() {
            @Override
            public Observable<RxCamera> call(RxCamera rxCamera) {
                Log.d(TAG, "isbindsurface: " + rxCamera.isBindSurface() + ", thread: " + Thread.currentThread());
                return rxCamera.startPreview();
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<RxCamera>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "open camera error: " + e.getMessage());
            }

            @Override
            public void onNext(final RxCamera rxCamera) {
                camera = rxCamera;
                Log.e(TAG, "open camera success: " + camera);
                Toast.makeText(MainActivity.this, "Now you can tap to focus", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void sendingPhotoAction() {

    }

    @Override
    public void photoSendAction() {
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            }
        }
    }

    @OnClick({R.id.buttonTakePhoto, R.id.buttonSendPhoto})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonTakePhoto:
//                takePhoto();
                requestTakePicture();
                break;
            case R.id.buttonSendPhoto:
                sendLastPhoto();
                break;
        }
    }

    private void requestTakePicture() {
        if (!checkCamera()) {
            return;
        }

        camera.request().takePictureRequest(false, () -> Log.d(TAG, "Captured!"),
                480, 640,
                ImageFormat.JPEG, true)
                .subscribe(rxCameraData -> {
                    final byte[] cameraData = rxCameraData.cameraData;
                    final int cameraDataLength = cameraData.length;
                    final Matrix rotateMatrix = rxCameraData.rotateMatrix;
                    presenter.saveAndSendPhotoFile(cameraData, cameraDataLength, rotateMatrix);
                    mButtonSendPhoto.setEnabled(true);

                });
    }

    private void sendLastPhoto() {
        presenter.sendLastPhoto();
        openCamera();
        mButtonSendPhoto.setEnabled(false);
    }

    private boolean checkCamera() {
        if (camera == null || !camera.isOpenCamera()) {
            return false;
        }
        return true;
    }
}
