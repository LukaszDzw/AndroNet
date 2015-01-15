package pl.umk.andronetandroidclient.listeners;

import android.widget.SeekBar;
import android.widget.TextView;
import main.Client;
import pl.umk.andronetandroidclient.network.enums.Rgb;
import pl.umk.andronetandroidclient.network.enums.Tags;
import pl.umk.andronetandroidclient.network.packets.ChangedRgbColor;
import pl.umk.andronetandroidclient.utils.Channel;
import pl.umk.andronetandroidclient.widgets.ColorView;

/**
 * Created by Lukasz on 2015-01-07.
 */
public class RGBBarChangeListener implements SeekBar.OnSeekBarChangeListener {

    private Client mClient;
    private Rgb mType;
    private Channel mChannel;
    private ColorView mColorview;
    private TextView mValue;

    public RGBBarChangeListener(Client client, Rgb type, Channel channel, ColorView view, TextView text)
    {
        mClient=client;
        mType=type;
        mChannel=channel;
        mColorview=view;
        mValue=text;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        if(b) {
            ChangedRgbColor changed = new ChangedRgbColor();
            changed.type = mType;
            changed.channel = mChannel.getmKey();
            changed.value = i;
            mClient.send(Tags.changeColor.name(), changed);
        }
        mColorview.setColor(mType, i);
        mValue.setText(String.valueOf(i));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
