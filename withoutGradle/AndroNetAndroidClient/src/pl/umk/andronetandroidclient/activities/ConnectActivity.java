package pl.umk.andronetandroidclient.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import interfaces.IListener;
import main.Client;
import main.Connection;
import pl.umk.andronetandroidclient.AndroNetApplication;
import pl.umk.andronetandroidclient.R;
import pl.umk.andronetandroidclient.network.enums.Tags;
import pl.umk.andronetandroidclient.utils.KeyboardHider;

/**
 * Created by Lukasz on 2015-01-06.
 */
public class ConnectActivity extends Activity {

    private EditText mIpField, mPortField;
    private Button mConnectButton;
    private Client mClient;
    private LinearLayout mLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);

        AndroNetApplication application=(AndroNetApplication)getApplication();
        mClient=application.getClient();

        mIpField=(EditText)findViewById(R.id.connection_ip);
        mPortField=(EditText)findViewById(R.id.connection_port);
        mConnectButton=(Button)findViewById(R.id.connect_button);
        mLayout=(LinearLayout)findViewById(R.id.connection_name_layout);

        KeyboardHider.setupUI(mLayout, this);

        initialize();
    }

    private void initialize()
    {
        mIpField.setText("192.168.2.242");
        mPortField.setText("5555");

        mConnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ip=mIpField.getText().toString();
                String portString=mPortField.getText().toString();

                if(!(ip==null || portString.isEmpty()))
                {
                    int port=Integer.parseInt(portString);
                    mClient.start(ip, port);
                    mClient.send(Tags.hello.name(), new Object());
                }
                else
                {
                    Toast.makeText(ConnectActivity.this, R.string.typeAllFields, Toast.LENGTH_SHORT).show();
                }
            }
        });

        mClient.addListener(Tags.hello.name(), new IListener() {
            @Override
            public void received(Connection connection, Object o) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ConnectActivity.this, R.string.connected, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ConnectActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
            }
        });
    }
}