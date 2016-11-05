package pl.lukaszbyjos.emotionshooter.view;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import pl.lukaszbyjos.emotionshooter.R;

public class PrefsFragment extends PreferenceActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }
}
