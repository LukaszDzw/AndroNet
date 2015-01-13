package pl.umk.andronetandroidclient;

import android.app.Application;
import main.Client;

/**
 * Created by Lukasz on 2014-12-26.
 */
public class AndroNetApplication extends Application {
    private static Client mClient;

    @Override
    public void onCreate()
    {
        mClient=new Client();
    }

    public Client getClient()
    {
        return mClient;
    }

}
