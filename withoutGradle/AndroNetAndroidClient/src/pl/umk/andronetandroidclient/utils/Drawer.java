package pl.umk.andronetandroidclient.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import pl.umk.andronetandroidclient.network.enums.Color;
import pl.umk.andronetandroidclient.network.packets.DrawPoint;

/**
 * Created by Lukasz on 2015-01-05.
 */
public class Drawer {
    private Path mDrawPath;
    private Paint mDrawPaint;

    private Canvas mDrawCanvas;
    private Canvas mCanvas;

    private Paint mCanvasPaint;//TODO temp

    private int mPaintColor = Color.RED.getColorValue();

    private boolean mIsdown;

    public Drawer(Bitmap canvasBitmap)
    {
        mDrawPath = new Path();
        mDrawPaint = new Paint();

        mDrawPaint.setColor(mPaintColor);
        mDrawPaint.setAntiAlias(true);
        mDrawPaint.setStrokeWidth(20);
        mDrawPaint.setStyle(Paint.Style.STROKE);
        mDrawPaint.setStrokeJoin(Paint.Join.ROUND);
        mDrawPaint.setStrokeCap(Paint.Cap.ROUND);

        mDrawCanvas = new Canvas(canvasBitmap);
        mCanvas = new Canvas(canvasBitmap);
        mCanvasPaint = new Paint(Paint.DITHER_FLAG);
    }

    public void draw(DrawPoint point)
    {
        float touchX = point.x;
        float touchY = point.y;

        switch (point.action) {
            case ACTION_DOWN:
                mDrawPath.moveTo(touchX, touchY);
                mIsdown = true;
                break;
            case ACTION_MOVE:
                if (!mIsdown) {
                    mDrawPath.moveTo(touchX, touchY);
                    mIsdown = true;
                }
                mDrawPath.lineTo(touchX, touchY);
                break;
            case ACTION_UP:
                mDrawPath.reset();
                mIsdown = false;
                break;
            default:
        }
    }

    public void drawPath()
    {
        mDrawCanvas.drawPath(mDrawPath, mDrawPaint);
    }

    public void onSizeChanged(Bitmap canvasBitmap)
    {
        mDrawCanvas = new Canvas(canvasBitmap);
        mCanvas = new Canvas(canvasBitmap);
    }

    public void setColor(Color color)
    {
        mDrawPaint.setColor(color.getColorValue());
    }
}
