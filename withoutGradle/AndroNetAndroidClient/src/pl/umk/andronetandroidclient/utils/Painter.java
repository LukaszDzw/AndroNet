package pl.umk.andronetandroidclient.utils;

import android.graphics.Paint;

/**
 * Created by Lukasz on 2015-01-15.
 */
public class Painter extends Paint {

    public Painter(int color)
    {
        setColor(color);
        setAntiAlias(true);
        setStrokeWidth(20);
        setStyle(Paint.Style.STROKE);
        setStrokeJoin(Paint.Join.ROUND);
        setStrokeCap(Paint.Cap.ROUND);
    }
}
