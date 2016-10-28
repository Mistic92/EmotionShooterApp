package pl.lukaszbyjos.emotionshooter.presenter;

public interface BasePresenter<T> {
    public void bind(T view);

    public void unbind();
}
