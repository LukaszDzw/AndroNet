package server.modules;

import interfaces.IListener;
import main.Connection;
import main.Server;
import pl.umk.andronetandroidclient.network.enums.Tags;
import pl.umk.andronetandroidclient.network.packets.ChangedRgbColor;
import pl.umk.andronetandroidclient.network.packets.RgbColor;
import server.interfaces.IModule;

import java.util.HashMap;

/**
 * Created by Lukasz on 2015-01-06.
 */
public class ColorModule implements IModule {
    private HashMap<Integer, RgbColor> channelColorMap;

    public ColorModule() {
        this.channelColorMap=new HashMap<>();
    }

    @Override
    public void setup(final Server server) {

        //getColor
        server.addListener(Tags.getColor.name(), new IListener() {
            @Override
            public void received(Connection connection, Object o) {
                int channel=(int)o;
                RgbColor color=getColor(channel);

                server.sendTo(connection, Tags.getColor.name(), color);
            }
        });

        //changedColor
        server.addListener((Tags.changeColor.name()), new IListener() {
            @Override
            public void received(Connection connection, Object o) {
                ChangedRgbColor changedColor=(ChangedRgbColor)o;
                setColor(changedColor);
                server.sendToAll(Tags.changeColor.name(), changedColor);
            }
        });
    }

    private void setColor(ChangedRgbColor changed)
    {
        RgbColor color=getColor(changed.channel);

        switch (changed.type)
        {
            case RED:
                color.r=changed.value;
                break;
            case GREEN:
                color.g=changed.value;
                break;
            case BLUE:
                color.b=changed.value;
                break;
        }
    }

    private RgbColor getColor(int channel)
    {
        RgbColor color = channelColorMap.get(channel);

        if(color==null)
        {
            color=new RgbColor();
            channelColorMap.put(channel, color);
        }

        return color;
    }
}
