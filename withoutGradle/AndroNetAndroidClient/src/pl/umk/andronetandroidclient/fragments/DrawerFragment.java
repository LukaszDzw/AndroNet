package pl.umk.andronetandroidclient.fragments;

import android.os.Bundle;
import android.util.SparseArray;
import android.view.*;
import android.widget.ImageButton;
import interfaces.IListener;
import main.Connection;
import pl.umk.andronetandroidclient.R;
import pl.umk.andronetandroidclient.network.enums.Color;
import pl.umk.andronetandroidclient.network.packets.ChangedColor;
import pl.umk.andronetandroidclient.network.packets.DrawPoint;
import pl.umk.andronetandroidclient.network.enums.Tags;
import pl.umk.andronetandroidclient.utils.MotionEventConvert;
import pl.umk.andronetandroidclient.widgets.DrawingView;

import java.util.ArrayList;

public class DrawerFragment extends BaseFragment {

    private DrawingView mDrawingView;
    private ImageButton mRedColorButton;
    private ImageButton mGreenColorButton;
    private ImageButton mBlueColorButton;
    private SparseArray<Color> mUsers;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUsers=new SparseArray<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_drawer, container, false);
        mDrawingView=(DrawingView)v.findViewById(R.id.drawer_field);
        mRedColorButton =(ImageButton)v.findViewById(R.id.draw_red);
        mGreenColorButton = (ImageButton)v.findViewById(R.id.draw_green);
        mBlueColorButton = (ImageButton)v.findViewById(R.id.draw_blue);

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

        setColorButtonListener(mRedColorButton, Color.RED);
        setColorButtonListener(mGreenColorButton, Color.GREEN);
        setColorButtonListener(mBlueColorButton, Color.BLUE);
    }

    private void setupDrawing(){
        mDrawingView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                DrawPoint point= MotionEventConvert.getDrawPointPacket(motionEvent);
                mClient.send(Tags.drawPosition.name(), point);
                mDrawingView.drawFromDevice(point);
                return true;
            }
        });
    }

    private void setupNetworking()
    {
        mClient.removeListeners();

        //points
        mClient.addListener(Tags.drawPosition.name(), new IListener() {
            @Override
            public void received(Connection connection, final Object o) {
                final DrawPoint point=(DrawPoint)o;

                mDrawingView.drawFromSocket(point);
            }
        });

        //disconnected
        mClient.addListener(Tags.disconnected.name(), new IListener() {
            @Override
            public void received(Connection connection, Object o) {
                int id=(int) o;
                mDrawingView.removeDrawer(id);
            }
        });

        //changeColor
        mClient.addListener(Tags.drawChangeColor.name(), new IListener() {
            @Override
            public void received(Connection connection, Object o) {
                ChangedColor changedColor=(ChangedColor) o;
                mDrawingView.setColor(changedColor.id, changedColor.color);
            }
        });

        mClient.send(Tags.getDrawerUser.name(), new Object());
    }

    private void setColorButtonListener(ImageButton button, final Color color)
    {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawingView.setMyDrawerColor(color);
                mClient.send(Tags.drawChangeColor.name(), color);
            }
        });
    }
}
