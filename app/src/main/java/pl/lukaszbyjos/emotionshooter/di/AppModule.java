package pl.lukaszbyjos.emotionshooter.di;

import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import pl.lukaszbyjos.emotionshooter.model.MainActivityModel;
import pl.lukaszbyjos.emotionshooter.model.impl.MainActivityModelImpl;
import pl.lukaszbyjos.emotionshooter.network.PhotoUploadApi;
import pl.lukaszbyjos.emotionshooter.presenter.MainActivityPresenter;
import retrofit2.Retrofit;

@Module
public class AppModule {

    @Singleton
    @Provides
    public MainActivityPresenter provideMainActivityPresenter(MainActivityModel mainActivityModel) {
        return new MainActivityPresenter(mainActivityModel);
    }

    @Singleton
    @Provides
    public MainActivityModel provideMainActivityModel(PhotoUploadApi photoUploadApi) {
        return new MainActivityModelImpl(photoUploadApi);
    }

    @Singleton
    @Provides
    public Retrofit provideRetrofit(OkHttpClient client) {
        return new Retrofit.Builder()
                .baseUrl("http://192.168.1.106:8080/api/")
                .client(client)
                .build();
    }

    @Provides
    @Singleton
    public OkHttpClient provideOkHttpClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(3, TimeUnit.SECONDS)
                .readTimeout(3, TimeUnit.SECONDS)
//                .addInterceptor(new StethoInterceptor())
                .build();
    }


    @Singleton
    @Provides
    public PhotoUploadApi providePhotoUploadApi(Retrofit retrofit) {
        return retrofit.create(PhotoUploadApi.class);
    }
}
