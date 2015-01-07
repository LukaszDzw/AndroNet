package pl.umk.andronetandroidclient.widgets;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import pl.umk.andronetandroidclient.network.enums.Rgb;
import pl.umk.andronetandroidclient.network.packets.RgbColor;

/**
 * Created by Lukasz on 2015-01-07.
 */
public class ColorView extends View {

    private RgbColor mColor;

    public ColorView(Context context) {
        super(context);
        setupColor();
    }

    public ColorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupColor();
    }

    public ColorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setupColor();
    }

    public void setColor(Rgb type, int color)
    {
        switch (type)
        {
            case RED:
                mColor.r=color;
                break;
            case GREEN:
                mColor.g=color;
                break;
            case BLUE:
                mColor.b=color;
                break;
        }
        this.post(new Runnable() {
            @Override
            public void run() {
                refreshColor();
            }
        });
    }

    public void refreshColor()
    {
        int colorHex= Color.rgb(mColor.r, mColor.g, mColor.b);
        setBackgroundColor(colorHex);
    }

    private void setupColor()
    {
        mColor=new RgbColor();
    }
}
