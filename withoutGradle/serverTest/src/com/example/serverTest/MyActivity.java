package com.example.serverTest;

import android.app.Activity;
import android.os.Bundle;
import interfaces.IListener;
import main.Connection;
import main.Server;

import java.util.HashMap;

public class MyActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    HashMap<Integer, Color> users;

    public DrawModule()
    {
        this.users=new HashMap<>();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        final Server server=new Server(5555);
        server.addListener("hello", new IListener() {
            @Override
            public void received(Connection connection, Object o) {
                server.sendTo(connection, "hello", o);
            }
        });
        server.start();
    }
}
