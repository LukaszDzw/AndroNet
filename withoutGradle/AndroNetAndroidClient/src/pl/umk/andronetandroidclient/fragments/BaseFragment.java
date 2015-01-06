package pl.umk.andronetandroidclient.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
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
