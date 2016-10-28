package pl.lukaszbyjos.emotionshooter;

import java.io.File;

public class PhotoModel {
    private String photoPath;
    private File photo;

    public PhotoModel() {

    }

    public PhotoModel(String photoPath, File photo) {

        this.photoPath = photoPath;
        this.photo = photo;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public File getPhoto() {
        return photo;
    }

    public void setPhoto(File photo) {
        this.photo = photo;
    }
}
