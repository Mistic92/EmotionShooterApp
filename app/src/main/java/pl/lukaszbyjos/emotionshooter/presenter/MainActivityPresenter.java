package pl.lukaszbyjos.emotionshooter.presenter;

import java.io.IOException;

import javax.inject.Inject;

import pl.lukaszbyjos.emotionshooter.PhotoModel;
import pl.lukaszbyjos.emotionshooter.model.MainActivityModel;
import pl.lukaszbyjos.emotionshooter.view.MainActivityView;

public class MainActivityPresenter implements BasePresenter<MainActivityView> {

    private MainActivityModel model;
    private MainActivityView view;
    private PhotoModel mLastPhotoModel;


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
        mLastPhotoModel = null;
    }


    public void sendLastPhoto() {
        model.sendPhoto(mLastPhotoModel.getPhotoPath());
    }

    public PhotoModel savePhoto() throws IOException {
        return mLastPhotoModel = model.createPhotoFile();
    }

    public void resetLastPhoto() {
        mLastPhotoModel = null;
    }
}
