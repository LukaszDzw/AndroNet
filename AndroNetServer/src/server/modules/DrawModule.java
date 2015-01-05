package server.modules;

import interfaces.IListener;
import main.Connection;
import main.Server;
import pl.umk.andronetandroidclient.network.enums.Color;
import pl.umk.andronetandroidclient.network.enums.Tags;
import pl.umk.andronetandroidclient.network.packets.ChangedColor;
import pl.umk.andronetandroidclient.network.packets.ChatUser;
import pl.umk.andronetandroidclient.network.packets.DrawPoint;
import pl.umk.andronetandroidclient.network.packets.DrawUser;
import server.interfaces.IModule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Lukasz on 2015-01-03.
 */
public class DrawModule implements IModule {

    HashMap<Integer, Color> users;

    public DrawModule()
    {
        this.users=new HashMap<>();
    }

    @Override
    public void setup(final Server server) {

        //points
        server.addListener(Tags.drawPosition.name(), new IListener() {
            @Override
            public void received(Connection connection, Object o) {
                DrawPoint point=(DrawPoint)o;
                point.id=connection.getId();
                server.sendToAllExcept(connection, Tags.drawPosition.name(), point);
            }
        });

        //disconnected
        server.addListener(Tags.disconnected.name(), new IListener() {
            @Override
            public void received(Connection connection, Object o) {
                users.remove(connection.getId());
                server.sendToAll(Tags.disconnected.name(), connection.getId());
            }
        });

        //changecolor
        server.addListener(Tags.drawChangeColor.name(), new IListener() {
            @Override
            public void received(Connection connection, Object o) {
                Color color=(Color)o;
                users.put(connection.getId(), color);
                ChangedColor changedColor=new ChangedColor();
                changedColor.id=connection.getId();
                changedColor.color=color;

                server.sendToAllExcept(connection, Tags.drawChangeColor.name(), changedColor);
            }
        });

        //getUsers
        server.addListener(Tags.getDrawerUser.name(), new IListener() {
            @Override
            public void received(Connection connection, Object o) {
                for(Map.Entry<Integer, Color> element:users.entrySet())
                {
                    ChangedColor drawUser = new ChangedColor();
                    int id=element.getKey();
                    if(id!=connection.getId()) {
                        drawUser.id = id;
                        drawUser.color = element.getValue();
                        server.sendTo(connection, Tags.getChatUser.name(), drawUser);
                    }
                }
            }
        });
    }
}
