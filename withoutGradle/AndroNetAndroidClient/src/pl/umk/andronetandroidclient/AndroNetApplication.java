package pl.umk.andronetandroidclient;

import android.app.Application;
import main.Client;

import java.util.UUID;

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

    public void onTerminate()
    {
        mClient.close();
    }

    public Client getClient()
    {
        return mClient;
    }

}
