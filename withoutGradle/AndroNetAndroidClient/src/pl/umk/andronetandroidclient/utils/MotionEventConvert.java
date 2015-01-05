package pl.umk.andronetandroidclient.utils;

import android.view.MotionEvent;
import pl.umk.andronetandroidclient.network.enums.Action;
import pl.umk.andronetandroidclient.network.packets.DrawPoint;

/**
 * Created by Lukasz on 2015-01-05.
 */
public class MotionEventConvert {

    public static DrawPoint getDrawPointPacket(MotionEvent motionEvent)
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
                point.action=Action.ACTION_UP;
                break;
            default:
                point.action=Action.ACTION_UP;
        }

        return point;
    }
}
