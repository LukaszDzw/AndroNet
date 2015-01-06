package pl.umk.andronetandroidclient.activities;

import android.os.Build;
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

        //if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            // Load the legacy preferences headers
            addPreferencesFromResource(R.xml.preferences);
       // }
    }

}