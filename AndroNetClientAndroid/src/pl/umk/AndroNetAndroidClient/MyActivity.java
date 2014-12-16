package pl.umk.AndroNetAndroidClient;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import interfaces.IListener;
import main.Client;
import main.Connection;

public class MyActivity extends Activity {

    private Button button;
    private EditText text;
    private Client client;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        this.client=new Client("192.168.2.242", 5555);
        this.client.start();
        this.initialize();
        this.addListeners();

    }

    private void initialize()
    {
        this.button=(Button)this.findViewById(R.id.button);
        this.text=(EditText)this.findViewById(R.id.text);

        this.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String data = text.getText().toString();
                client.send("test", data);
            }
        });
    }

    private void addListeners()
    {
        client.addListener("test", new IListener() {
            @Override
            public void received(Connection connection, Object object) {
                Toast.makeText(MyActivity.this, "dupa", Toast.LENGTH_SHORT).show();
            }
        });


    }
}
