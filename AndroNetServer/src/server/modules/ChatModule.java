package server.modules;

import interfaces.IDisconnected;
import interfaces.IListener;
import main.Connection;
import main.Server;
import pl.umk.andronetandroidclient.network.enums.Tags;
import pl.umk.andronetandroidclient.network.packets.ChatMessage;
import pl.umk.andronetandroidclient.network.packets.ChatUser;
import server.interfaces.IModule;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Lukasz on 2015-01-03.
 */
public class ChatModule implements IModule {
    private HashMap<Integer, String> userMap;
    private SimpleDateFormat sdf;

    public ChatModule()
    {
        this.userMap=new HashMap<>();
        this.sdf = new SimpleDateFormat("HH:mm dd.MM.yyyy");
    }

    @Override
    public void setup(final Server server) {

        //new message
        server.addListener(Tags.chatMessage.name(), new IListener() {
            @Override
            public void received(Connection connection, Object o) {
                String text = (String)o;

                ChatMessage message=new ChatMessage();
                message.id=connection.getId();
                message.message=text;
                message.time= sdf.format(new Date());
                server.sendToAll(Tags.chatMessage.name(), message);
            }
        });

        //new user
        server.addListener(Tags.registerChat.name(), new IListener() {
            @Override
            public void received(Connection connection, Object o) {
                String name=(String) o;

                userMap.put(connection.getId(), name);
                server.sendTo(connection, Tags.registerChat.name(), connection.getId());

                ChatUser newUser=new ChatUser();
                newUser.id=connection.getId();
                newUser.name=name;
                server.sendToAllExcept(connection, Tags.getChatUser.name(), newUser);
            }
        });

        //getusers
        server.addListener(Tags.getChatUser.name(), new IListener() {
            @Override
            public void received(Connection connection, Object o) {

                for(Map.Entry<Integer, String> element:userMap.entrySet())
                {
                    ChatUser chatUser = new ChatUser();
                    chatUser.id=element.getKey();
                    chatUser.name=element.getValue();
                    server.sendTo(connection, Tags.getChatUser.name(), chatUser);
                }
            }
        });

        //disconnected
        server.addListener(Tags.disconnected.name(), new IListener() {
            @Override
            public void received(Connection connection, Object o) {
                userMap.remove(connection.getId());
                server.sendToAll(Tags.disconnected.name(), connection.getId());
            }
        });

        server.setDisconnectedAction(new IDisconnected() {
            @Override
            public void disconnected(Connection connection) {
                System.out.println("bye connection " + connection.getId());
                userMap.remove(connection.getId());
            }
        });
    }
}
