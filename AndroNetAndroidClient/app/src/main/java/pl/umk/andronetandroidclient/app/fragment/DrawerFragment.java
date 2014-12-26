package pl.umk.andronetandroidclient.app.fragment;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import interfaces.IListener;
import main.Client;
import main.Connection;
import pl.umk.andronetandroidclient.app.R;
import pl.umk.andronetandroidclient.app.widgets.DrawingView;

public class DrawerFragment extends Fragment {

    private DrawingView mDrawingView;
    private Client mClient;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mClient=new Client("192.168.2.242",5555);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_drawer, container, false);

        mDrawingView=(DrawingView)v.findViewById(R.id.drawer_field);
        initialize();

        return v;
    }

    @Override
    public void onStop()
    {
        mClient.close();
        super.onStop();
    }

    @Override
    public void onPause()
    {
        super.onPause();
    }

    private void initialize()
    {
        mClient.start();
        setupDrawing();
        setupNetworking();
    }

    private void setupDrawing(){
        mDrawingView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                mClient.send("drawPosition", motionEvent);
                return true;
            }
        });
    }

    private void setupNetworking()
    {
        mClient.addListener("drawPosition", new IListener() {
            @Override
            public void received(Connection connection, Object o) {
                MotionEvent motionEvent = (MotionEvent) o;
                //mDrawingView.onTouchEvent(motionEvent);
            }
        });
    }
}
