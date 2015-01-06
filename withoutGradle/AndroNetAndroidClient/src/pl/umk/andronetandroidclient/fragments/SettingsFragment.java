package pl.umk.andronetandroidclient.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import pl.umk.andronetandroidclient.R;

/**
 * Created by Lukasz on 2015-01-06.
 */
public class SettingsFragment extends PreferenceFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        //addPreferencesFromResource(R.xml.preferences);
    }
}