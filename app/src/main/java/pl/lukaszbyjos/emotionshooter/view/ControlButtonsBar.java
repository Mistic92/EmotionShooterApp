package pl.lukaszbyjos.emotionshooter.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hugo.weaving.DebugLog;
import pl.lukaszbyjos.emotionshooter.R;

public class ControlButtonsBar extends LinearLayout {
    private static final String TAG = "CBB";
    @BindView(R.id.buttonTakePhoto)
    ImageButton mButtonTakePhoto;
    @BindView(R.id.buttonCancelPhoto)
    ImageButton mButtonCancelPhoto;
    @BindView(R.id.buttonSendPhoto)
    ImageButton mButtonSendPhoto;
    private View rootView;
    private ControlButtonsListener mControlButtonsListener;
    private Context mContext;

    public ControlButtonsBar(Context context) {
        super(context);
        init(context);
    }

    public ControlButtonsBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ControlButtonsBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void setControlButtonsListener(ControlButtonsListener controlButtonsListener) {
        mControlButtonsListener = controlButtonsListener;
    }

    private void init(Context context) {
        rootView = inflate(context, R.layout.buttons_bar, this);
        mContext = context;
        ButterKnife.bind(rootView);
    }

    @OnClick({R.id.buttonTakePhoto,
            R.id.buttonCancelPhoto,
            R.id.buttonSendPhoto})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonTakePhoto:
                requestTakePicture();
                break;
            case R.id.buttonSendPhoto:
                sendLastPhoto();
                break;
            case R.id.buttonCancelPhoto:
                cancelPhoto();
                break;
        }
    }

    @DebugLog
    private void cancelPhoto() {
        disableClearButton();
        mButtonTakePhoto.setEnabled(true);
        setSendButtonInactive();
        mControlButtonsListener.onCancelPhotoClicked();
    }

    @DebugLog
    private void sendLastPhoto() {
        disableClearButton();
        mButtonSendPhoto.setEnabled(false);
        mButtonTakePhoto.setEnabled(true);
        mControlButtonsListener.onSendPhotoClicked();
    }

    private void disableClearButton() {
        mButtonCancelPhoto.setEnabled(false);
        loadDrawableIntoImageB(R.drawable.ic_clear_inactive24dp, mButtonCancelPhoto);
    }

    private void setSendButtonInactive() {
        loadDrawableIntoImageB(R.drawable.ic_cloud_black_inactive_24dp, mButtonSendPhoto);
        mButtonSendPhoto.setEnabled(false);
    }

    @DebugLog
    private void requestTakePicture() {
        mControlButtonsListener.onTakePhotoClicked();
    }

    private void loadDrawableIntoImageB(final int drawable, ImageButton imageButton) {
        Glide.with(mContext)
                .load(drawable)
                .error(drawable)
                .into(imageButton);
    }


    public void sendingPhotoSetup() {
        loadDrawableIntoImageB(R.drawable.ic_cloud_upload_black_24dp, mButtonSendPhoto);
        mButtonTakePhoto.setEnabled(false);
        loadDrawableIntoImageB(R.drawable.ic_add_a_photo_inactive_24dp, mButtonTakePhoto);
    }

    public void photoSendSetup() {
        loadDrawableIntoImageB(R.drawable.ic_cloud_done_green_24dp, mButtonSendPhoto);
    }

    public void photoNotSendSetup() {
        loadDrawableIntoImageB(R.drawable.ic_cloud_off_red_24dp, mButtonSendPhoto);
    }

    public void startSetup() {
        setSendButtonInactive();
        mButtonTakePhoto.setEnabled(true);
        loadDrawableIntoImageB(R.drawable.ic_add_a_photo_black_24dp, mButtonTakePhoto);
    }

    public void photoTakenSetup() {
        loadDrawableIntoImageB(R.drawable.ic_cloud_black_24dp, mButtonSendPhoto);
        loadDrawableIntoImageB(R.drawable.ic_clear_red24dp, mButtonCancelPhoto);
        mButtonSendPhoto.setEnabled(true);
        mButtonCancelPhoto.setEnabled(true);
        mButtonTakePhoto.setEnabled(true);
    }

    interface ControlButtonsListener {
        void onTakePhotoClicked();

        void onSendPhotoClicked();

        void onCancelPhotoClicked();
    }
}
