package pl.umk.andronetandroidclient.fragments;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import pl.umk.andronetandroidclient.R;

/**
 * Created by Lukasz on 2015-01-05.
 */
public class ColorsFragment extends BaseFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v=inflater.inflate(R.layout.fragment_colors, container, false);

        displayUserSettings();
        return v;
    }

    private void displayUserSettings()
    {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        String  settings = "";

        settings=settings+"Password: " + sharedPrefs.getString("prefColorChannel", "NOPASSWORD");
        Toast.makeText(this.getActivity(), settings, Toast.LENGTH_SHORT).show();
    }

    private void setupNetworking()
    {

    }
}