package pl.umk.andronetandroidclient.fragments;

import android.os.Bundle;
import android.os.SystemClock;
import android.view.*;
import interfaces.IListener;
import main.Connection;
import pl.umk.andronetandroidclient.R;
import pl.umk.andronetandroidclient.network.enums.Action;
import pl.umk.andronetandroidclient.network.packets.DrawPoint;
import pl.umk.andronetandroidclient.network.enums.Tags;
import pl.umk.andronetandroidclient.widgets.DrawingView;

public class DrawerFragment extends BaseFragment {

    private DrawingView mDrawingView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        super.onStop();
    }

    @Override
    public void onPause()
    {
        super.onPause();
    }

    private void initialize()
    {
        setupNetworking();
        setupDrawing();
    }

    private void setupDrawing(){
        mDrawingView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                DrawPoint point=getDrawPointPacket(motionEvent);
                mClient.send(Tags.drawPosition.name(), point);
                return true;
            }
        });
    }

    private DrawPoint getDrawPointPacket(MotionEvent motionEvent)
    {
        DrawPoint point=new DrawPoint();
        point.x=motionEvent.getX();
        point.y=motionEvent.getY();
        point.id=1;

        switch (motionEvent.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                point.action= Action.ACTION_DOWN;
                break;
            case MotionEvent.ACTION_MOVE:
                point.action=Action.ACTION_MOVE;
                break;
            case MotionEvent.ACTION_UP:
                point.action=Action.ACTION_UP;
                break;
            default:
                point.action=Action.ACTION_UP;
        }

        return point;
    }

    private void setupNetworking()
    {
        mClient.removeListeners();
        mClient.addListener(Tags.drawPosition.name(), new IListener() {
            @Override
            public void received(Connection connection, final Object o) {
                final DrawPoint point=(DrawPoint)o;

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mDrawingView.draw(point);
                    }
                });
            }
        });
    }
}
