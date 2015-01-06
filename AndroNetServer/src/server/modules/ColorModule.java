package server.modules;

import interfaces.IListener;
import main.Connection;
import main.Server;
import pl.umk.andronetandroidclient.network.enums.Tags;
import pl.umk.andronetandroidclient.network.packets.ChangedColor;
import pl.umk.andronetandroidclient.network.packets.ChangedRgbColor;
import pl.umk.andronetandroidclient.network.packets.RgbColor;
import server.interfaces.IModule;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Lukasz on 2015-01-06.
 */
public class ColorModule implements IModule {
    HashMap<Integer, RgbColor> channelColorMap;

    public ColorModule() {
    }
    @Override
    public void setup(final Server server) {

        //getColor
        server.addListener(Tags.getColor.name(), new IListener() {
            @Override
            public void received(Connection connection, Object o) {
                int channel=(int)o;
                int color=channelColorMap.get(channel);
                server.sendTo(connection, Tags.getColor.name(), color);
            }
        });

        //changedColor
        server.addListener((Tags.changeColor.name()), new IListener() {
            @Override
            public void received(Connection connection, Object o) {
                ChangedRgbColor changedColor=(ChangedRgbColor)o;

                RgbColor rgbColor = channelColorMap.get(changedColor.id);
                if(rgbColor==null){
                    rgbColor=new RgbColor();
                    rgbColor.
                    channelColorMap.put()
                }

                for(Map.Entry<Integer, Integer> user:userChannelMap.entrySet())
                {
                    if(user.getKey()==channel)
                    {
                        server.sendTo();
                    }
                }
            }
        });
    }

    public void changeColor(ChangedRgbColor changed, RgbColor color)
    {
        switch (changed.type)
        {
            case RED:
                color.r=changed.color;
                break;

        }
    }
}
