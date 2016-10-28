package pl.lukaszbyjos.emotionshooter.di;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import pl.lukaszbyjos.emotionshooter.model.MainActivityModel;
import pl.lukaszbyjos.emotionshooter.model.impl.MainActivityModelImpl;
import pl.lukaszbyjos.emotionshooter.presenter.MainActivityPresenter;

@Module
public class AppModule {

    @Singleton
    @Provides
    public MainActivityPresenter provideMainActivityPresenter(MainActivityModel mainActivityModel) {
        return new MainActivityPresenter(mainActivityModel);
    }

    @Singleton
    @Provides
    public MainActivityModel provideMainActivityModel() {
        return new MainActivityModelImpl();
    }

}
