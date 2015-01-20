package pl.umk.andronetandroidclient.activities;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import pl.umk.andronetandroidclient.R;

import java.util.List;

/**
 * Created by Lukasz on 2015-01-06.
 */
public class SettingsActivity extends PreferenceActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.preferences);
    }

}