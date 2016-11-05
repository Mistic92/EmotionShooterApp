package pl.lukaszbyjos.emotionshooter.di;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import pl.lukaszbyjos.emotionshooter.AppClass;
import pl.lukaszbyjos.emotionshooter.HostSelectionInterceptor;
import pl.lukaszbyjos.emotionshooter.model.MainActivityModel;
import pl.lukaszbyjos.emotionshooter.model.impl.MainActivityModelImpl;
import pl.lukaszbyjos.emotionshooter.network.PhotoUploadApi;
import pl.lukaszbyjos.emotionshooter.presenter.MainActivityPresenter;
import retrofit2.Retrofit;

@Module
public class AppModule {

    private AppClass mAppClass;

    public AppModule(AppClass appClass) {
        mAppClass = appClass;
    }

    @Singleton
    @Provides
    public Context provideContext() {
        return mAppClass.getApplicationContext();
    }

    @Singleton
    @Provides
    public MainActivityPresenter provideMainActivityPresenter(MainActivityModel mainActivityModel) {
        return new MainActivityPresenter(mainActivityModel);
    }

    @Singleton
    @Provides
    public MainActivityModel provideMainActivityModel(PhotoUploadApi photoUploadApi,
                                                      HostSelectionInterceptor hostSelectionInterceptor,
                                                      SharedPreferences sharedPreferences) {
        return new MainActivityModelImpl(photoUploadApi,
                hostSelectionInterceptor,
                sharedPreferences);
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
    public HostSelectionInterceptor provideHostSelectionInterceptor() {
        return new HostSelectionInterceptor();
    }

    @Provides
    @Singleton
    public OkHttpClient provideOkHttpClient(HostSelectionInterceptor hostSelectionInterceptor) {
        return new OkHttpClient.Builder()
                .connectTimeout(3, TimeUnit.SECONDS)
                .readTimeout(3, TimeUnit.SECONDS)
//                .addInterceptor(new StethoInterceptor())
                .addInterceptor(hostSelectionInterceptor)
                .build();
    }


    @Singleton
    @Provides
    public PhotoUploadApi providePhotoUploadApi(Retrofit retrofit) {
        return retrofit.create(PhotoUploadApi.class);
    }

    @Singleton
    @Provides
    public SharedPreferences provideSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }
}
