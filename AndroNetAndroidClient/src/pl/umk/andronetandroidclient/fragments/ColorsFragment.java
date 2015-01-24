package pl.umk.andronetandroidclient.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import interfaces.IListener;
import main.Connection;
import pl.umk.andronetandroidclient.R;
import pl.umk.andronetandroidclient.listeners.RGBBarChangeListener;
import pl.umk.andronetandroidclient.network.enums.Rgb;
import pl.umk.andronetandroidclient.network.enums.Tags;
import pl.umk.andronetandroidclient.network.packets.ChangedRgbColor;
import pl.umk.andronetandroidclient.network.packets.RgbColor;
import pl.umk.andronetandroidclient.utils.Channel;
import pl.umk.andronetandroidclient.widgets.ColorView;

/**
 * Created by Lukasz on 2015-01-05.
 */
public class ColorsFragment extends BaseFragment {
    private Channel mMyChannel;
    private SeekBar mRedSeek, mGreenSeek, mBlueSeek;
    private TextView mRedText, mGreenText, mBlueText;
    private ColorView mColorView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMyChannel=new Channel();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_colors, container, false);
        mRedSeek=(SeekBar)v.findViewById(R.id.color_red_seeker);
        mGreenSeek=(SeekBar)v.findViewById(R.id.color_green_seeker);
        mBlueSeek=(SeekBar)v.findViewById(R.id.color_blue_seeker);
        mColorView=(ColorView)v.findViewById(R.id.color_view);
        mRedText=(TextView)v.findViewById(R.id.color_red_value);
        mGreenText=(TextView)v.findViewById(R.id.color_green_value);
        mBlueText=(TextView)v.findViewById(R.id.color_blue_value);

        initialize();
        setupNetworking();
        getUserSettings();
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        getUserSettings();
        mClient.send(Tags.getColor.name(), mMyChannel.getmKey());
    }

    private void initialize()
    {
        RGBBarChangeListener mSeekBarsChangeListenerRed=new RGBBarChangeListener(
                mClient,
                Rgb.RED,
                mMyChannel,
                mColorView,
                mRedText);
        RGBBarChangeListener mSeekBarsChangeListenerGreen=new RGBBarChangeListener(
                mClient,
                Rgb.GREEN,
                mMyChannel,
                mColorView,
                mGreenText);
        RGBBarChangeListener mSeekBarsChangeListenerBlue=new RGBBarChangeListener(
                mClient,
                Rgb.BLUE,
                mMyChannel,
                mColorView,
                mBlueText);
        mRedSeek.setOnSeekBarChangeListener(mSeekBarsChangeListenerRed);
        mGreenSeek.setOnSeekBarChangeListener(mSeekBarsChangeListenerGreen);
        mBlueSeek.setOnSeekBarChangeListener(mSeekBarsChangeListenerBlue);
    }

    private void getUserSettings()
    {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        String settings = "";
        settings=sharedPrefs.getString("prefColorChannel", "1");
        int key=Integer.parseInt(settings);
        mMyChannel.setmKey(key);
    }

    private void setupNetworking()
    {
        //changedColor
        mClient.addListener(Tags.changeColor.name(), new IListener() {
            @Override
            public void received(Connection connection, Object o) {
                final ChangedRgbColor color = (ChangedRgbColor) o;
                if(mMyChannel.getmKey() ==color.channel)
                {
                    switch (color.type)
                    {
                        case RED:
                            mRedSeek.setProgress(color.value);
                            break;
                        case GREEN:
                            mGreenSeek.setProgress(color.value);
                            break;
                        case BLUE:
                            mBlueSeek.setProgress(color.value);
                            break;
                    }
                }
            }
        });

        //getColor
        mClient.addListener(Tags.getColor.name(), new IListener() {
            @Override
            public void received(Connection connection, Object o) {
                RgbColor color = (RgbColor) o;
                mRedSeek.setProgress(color.r);
                mGreenSeek.setProgress(color.g);
                mBlueSeek.setProgress(color.b);
            }
        });

        mClient.send(Tags.getColor.name(), mMyChannel.getmKey());
    }
}