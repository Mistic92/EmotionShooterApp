package pl.lukaszbyjos.emotionshooter.model;

import java.io.IOException;

import pl.lukaszbyjos.emotionshooter.PhotoModel;

public interface MainActivityModel {

    /**
     * Create photo file for camera intent
     * @return PhotoModel with File and filepath
     * @throws IOException
     */
    public PhotoModel createPhotoFile() throws IOException;

    /**
     * Send photo from given path
     * @param imagePath
     */
    public void sendPhoto(final String imagePath);

}
