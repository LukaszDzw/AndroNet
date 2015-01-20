package pl.umk.andronetandroidclient.fragments;

import android.app.Fragment;
import android.os.Bundle;
import main.Client;
import pl.umk.andronetandroidclient.AndroNetApplication;

/**
 * Created by Lukasz on 2015-01-02.
 */
public abstract class BaseFragment extends Fragment {

    protected Client mClient;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        AndroNetApplication app=(AndroNetApplication)getActivity().getApplication();
        mClient=app.getClient();
    }
}
