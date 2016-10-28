package pl.lukaszbyjos.emotionshooter;

import android.app.Application;

import pl.lukaszbyjos.emotionshooter.di.AppComponent;
import pl.lukaszbyjos.emotionshooter.di.AppModule;
import pl.lukaszbyjos.emotionshooter.di.DaggerAppComponent;

public class AppClass extends Application {

    private  AppComponent mAppComponent;

    public AppClass() {
        super();
    }

    public  AppComponent getAppComponent() {
        return mAppComponent;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mAppComponent = DaggerAppComponent.builder()
                .appModule(new AppModule())
                .build();
    }
}
