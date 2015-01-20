package pl.umk.andronetandroidclient.widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import pl.umk.andronetandroidclient.network.enums.Color;
import pl.umk.andronetandroidclient.network.packets.DrawPoint;
import pl.umk.andronetandroidclient.utils.Drawer;

/**
 * Created by Lukasz on 2014-12-26.
 */
public class DrawingView extends View {
    private SparseArray<Drawer> mDrawers;
    private Drawer mMyDrawer;

    private Paint mCanvasPaint;
    private Bitmap mCanvasBitmap;

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

    public void setMyDrawerColor(Color color)
    {
        mMyDrawer.setColor(color);
    }

    public void drawFromDevice(DrawPoint point)
    {
        mMyDrawer.draw(point);
        invalidate();
    }

    public void drawFromSocket(DrawPoint point)
    {
        Drawer drawer=mDrawers.get(point.id);
        if(drawer==null)
        {
            drawer=this.addDrawer(point.id);
        }

        drawer.draw(point);

        this.post(new Runnable() {
            @Override
            public void run() {
                invalidate();
            }
        });
    }

    public Drawer addDrawer(int id)
    {
        Drawer drawer=new Drawer(mCanvasBitmap);
        mDrawers.put(id, drawer);
        return drawer;
    }

    public void removeDrawer(int id)
    {
        mDrawers.remove(id);
    }

    public void setColor(int id, Color color)
    {
        Drawer drawer = mDrawers.get(id);
        if(drawer==null)
        {
            drawer=addDrawer(id);
        }
        drawer.setColor(color);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mCanvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);

        for(int i=0; i<mDrawers.size(); i++)
        {
            int key = mDrawers.keyAt(i);
            Drawer drawer = mDrawers.get(key);
            drawer.onSizeChanged(mCanvasBitmap);
        }
        mMyDrawer=new Drawer(mCanvasBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(mCanvasBitmap, 0, 0, mCanvasPaint);
        for(int i=0; i<mDrawers.size(); i++)
        {
            int key = mDrawers.keyAt(i);
            Drawer drawer = mDrawers.get(key);
            drawer.drawPath();
        }
        mMyDrawer.drawPath();
    }

    private void setupDrawing()
    {
        mDrawers=new SparseArray<>();
        mCanvasPaint = new Paint(Paint.DITHER_FLAG);
    }
}
