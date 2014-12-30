package pl.umk.andronetandroidclient.widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import main.Packet;
import pl.umk.andronetandroidclient.network.DrawPoint;

/**
 * Created by Lukasz on 2014-12-26.
 */
public class DrawingView extends View {

    //drawing path
    private Path mDrawPath;
    //drawing and canvas paint
    private Paint mDrawPaint, mCanvasPaint;
    //initial color
    private int mPaintColor = 0xFF660000;
    //canvas
    private Canvas mDrawCanvas;
    //canvas bitmap
    private Bitmap mCanvasBitmap;

    private boolean isdown;

    public DrawingView(Context context) {
        super(context);
        setupDrawing();
    }

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupDrawing();
    }

    public DrawingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setupDrawing();
    }


    /*
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDrawPath.moveTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_MOVE:
                mDrawPath.lineTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_UP:
                mDrawCanvas.drawPath(mDrawPath, mDrawPaint);
                mDrawPath.reset();
                break;
            default:
                return false;
        }

        invalidate();
        return true;
    }*/

    public void draw(DrawPoint point)
    {
        float touchX = point.x;
        float touchY = point.y;


        switch (point.action) {
            case ACTION_DOWN:
                mDrawPath.moveTo(touchX, touchY);
                isdown=true;
                break;
            case ACTION_MOVE:
                if(!isdown) {
                    mDrawPath.moveTo(touchX, touchY);
                    isdown=true;
                }
                mDrawPath.lineTo(touchX, touchY);
                break;
            case ACTION_UP:
                mDrawCanvas.drawPath(mDrawPath, mDrawPaint);
                mDrawPath.reset();
                isdown=false;
                break;
            default:

        }
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);
        mCanvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mDrawCanvas = new Canvas(mCanvasBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(mCanvasBitmap, 0, 0, mCanvasPaint);
        canvas.drawPath(mDrawPath, mDrawPaint);
    }

    private void setupDrawing()
    {
        mDrawPath = new Path();
        mDrawPaint = new Paint();

        mDrawPaint.setColor(mPaintColor);
        mDrawPaint.setAntiAlias(true);
        mDrawPaint.setStrokeWidth(20);
        mDrawPaint.setStyle(Paint.Style.STROKE);
        mDrawPaint.setStrokeJoin(Paint.Join.ROUND);
        mDrawPaint.setStrokeCap(Paint.Cap.ROUND);

        mCanvasPaint = new Paint(Paint.DITHER_FLAG);
    }
}
