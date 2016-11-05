package pl.lukaszbyjos.emotionshooter.view;

import pl.lukaszbyjos.emotionshooter.events.PhotoSendEvent;
import pl.lukaszbyjos.emotionshooter.events.PhotoSendingEvent;

public interface MainActivityView {

    public void sendingPhotoAction(PhotoSendingEvent photoSendingEvent);

    public void photoSendAction(PhotoSendEvent photoSendEvent);
}
