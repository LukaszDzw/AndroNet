package pl.umk.andronetandroidclient.widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import pl.umk.andronetandroidclient.network.packets.DrawPoint;

/**
 * Created by Lukasz on 2014-12-26.
 */
public class DrawingView extends View {

    //drawing path
    private Path mDrawPath;
    private Path mDrawPath2;
    //drawing and canvas paint
    private Paint mDrawPaint, mCanvasPaint;
    private Paint mDrawPaint2, mCanvasPaint2;

    //initial color
    private int mPaintColor = 0xFF660000;
    //canvas
    private Canvas mDrawCanvas;
    private Canvas mDrawCanvas2;

    private Canvas canvas2;

    //canvas bitmap
    private Bitmap mCanvasBitmap;

    private boolean isdown;
    private boolean isdown2;

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

    public void draw(DrawPoint point)
    {

        float touchX = point.x;
        float touchY = point.y;

        if(point.id==1)
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

        if(point.id==2)
            switch (point.action) {
                case ACTION_DOWN:
                    mDrawPath2.moveTo(touchX, touchY);
                    isdown2=true;
                    break;
                case ACTION_MOVE:
                    if(!isdown2) {
                        mDrawPath2.moveTo(touchX, touchY);
                        isdown2=true;
                    }
                    mDrawPath2.lineTo(touchX, touchY);
                    break;
                case ACTION_UP:
                    mDrawCanvas2.drawPath(mDrawPath2, mDrawPaint2);
                    mDrawPath2.lineTo(touchX, touchY); //test
                    mDrawPath2.reset();
                    isdown2=false;
                    break;
                default:
            }
        invalidate();

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mCanvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mDrawCanvas = new Canvas(mCanvasBitmap);
        mDrawCanvas2 = new Canvas(mCanvasBitmap);
        canvas2=new Canvas(mCanvasBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(mCanvasBitmap, 0, 0, mCanvasPaint);
        //canvas.drawPath(mDrawPath, mDrawPaint);

        //canvas2.drawBitmap(mCanvasBitmap, 0, 0, mCanvasPaint2);
        //canvas2.drawPath(mDrawPath2, mDrawPaint2);
    }

    private void setupDrawing()
    {
        mDrawPath = new Path();
        mDrawPaint = new Paint();

        mDrawPath2 = new Path();
        mDrawPaint2 = new Paint();

        mDrawPaint.setColor(mPaintColor);
        mDrawPaint.setAntiAlias(true);
        mDrawPaint.setStrokeWidth(20);
        mDrawPaint.setStyle(Paint.Style.STROKE);
        mDrawPaint.setStrokeJoin(Paint.Join.ROUND);
        mDrawPaint.setStrokeCap(Paint.Cap.ROUND);

        mDrawPaint2.setColor(mPaintColor);
        mDrawPaint2.setAntiAlias(true);
        mDrawPaint2.setStrokeWidth(20);
        mDrawPaint2.setStyle(Paint.Style.STROKE);
        mDrawPaint2.setStrokeJoin(Paint.Join.ROUND);
        mDrawPaint2.setStrokeCap(Paint.Cap.ROUND);


        mCanvasPaint = new Paint(Paint.DITHER_FLAG);
        mCanvasPaint2 = new Paint(Paint.DITHER_FLAG);
    }
}
