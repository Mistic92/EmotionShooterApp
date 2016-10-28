package pl.lukaszbyjos.emotionshooter.model;

import android.graphics.Matrix;

import java.io.File;
import java.io.IOException;

import pl.lukaszbyjos.emotionshooter.PhotoModel;

public interface MainActivityModel {

    public PhotoModel createPhotoFile(File storageDir) throws IOException;

    public void sendPhoto(final String imagePath);

    public String savePhotoFile(byte[] cameraData, int cameraDataLength, Matrix rotateMatrix);
}
