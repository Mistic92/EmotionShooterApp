package pl.lukaszbyjos.emotionshooter.di;

import javax.inject.Singleton;

import dagger.Component;
import pl.lukaszbyjos.emotionshooter.view.MainActivity;

@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {
    void inject(MainActivity mainActivity);
}
