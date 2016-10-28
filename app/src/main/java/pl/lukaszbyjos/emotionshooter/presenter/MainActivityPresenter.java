package pl.lukaszbyjos.emotionshooter.presenter;

import android.graphics.Matrix;

import javax.inject.Inject;

import pl.lukaszbyjos.emotionshooter.model.MainActivityModel;
import pl.lukaszbyjos.emotionshooter.view.MainActivityView;

public class MainActivityPresenter implements BasePresenter<MainActivityView> {

    private MainActivityModel model;
    private MainActivityView view;
    private String lastPhotoPath;


    @Inject
    public MainActivityPresenter(MainActivityModel mainActivityModel) {
        model = mainActivityModel;
    }

    @Override
    public void bind(MainActivityView view) {
        this.view = view;
    }

    @Override
    public void unbind() {
        view = null;
    }

    public void saveAndSendPhotoFile(byte[] cameraData, int cameraDataLength, Matrix rotateMatrix) {
        lastPhotoPath = model.savePhotoFile(cameraData, cameraDataLength, rotateMatrix);
    }

    public void sendLastPhoto() {
        model.sendPhoto(lastPhotoPath);
    }


}
