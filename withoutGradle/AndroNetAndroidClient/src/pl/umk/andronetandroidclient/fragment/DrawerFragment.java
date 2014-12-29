package pl.umk.andronetandroidclient.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import interfaces.IListener;
import main.Client;
import main.Connection;
import pl.umk.andronetandroidclient.R;
import pl.umk.andronetandroidclient.network.Action;
import pl.umk.andronetandroidclient.network.DrawPoint;
import pl.umk.andronetandroidclient.widgets.DrawingView;

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
        //setupDrawing();
        setupNetworking();
        //mClient.send("lol", 15);
        setupDrawing();
    }

    private void setupDrawing(){
        mDrawingView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                DrawPoint point=getDrawPointPacket(motionEvent);
                mClient.send("drawPosition", point);
                return true;
            }
        });
    }

    private DrawPoint getDrawPointPacket(MotionEvent motionEvent)
    {
        DrawPoint point=new DrawPoint();
        point.x=motionEvent.getX();
        point.y=motionEvent.getY();

        switch (motionEvent.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                point.action= Action.ACTION_DOWN;
                break;
            case MotionEvent.ACTION_MOVE:
                point.action=Action.ACTION_MOVE;
                break;
            case MotionEvent.ACTION_UP:
                point.action=Action.ACTION_DOWN;
                break;
        }

        return point;
    }

    private void setupNetworking()
    {
        mClient.addListener("drawPosition", new IListener() {
            @Override
            public void received(Connection connection, final Object o) {

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final DrawPoint kk=(DrawPoint)o;
                        mDrawingView.draw(kk);
                    }
                });
            }
        });
    }
}
