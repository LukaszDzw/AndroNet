package server.modules;

import interfaces.IListener;
import main.Connection;
import main.Server;
import pl.umk.andronetandroidclient.network.enums.Tags;
import pl.umk.andronetandroidclient.utils.ChatMessage;
import server.interfaces.IModule;

import java.util.TreeMap;

/**
 * Created by Lukasz on 2015-01-03.
 */
public class ChatModule implements IModule {
    private TreeMap<Integer, String> userMap;

    public ChatModule()
    {
        this.userMap=new TreeMap<>();
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
            }
        });

        //sendAllUsers
        server.addListener(Tags.getChatUsers.name(), new IListener() {
            @Override
            public void received(Connection connection, Object o) {
                server.sendTo(connection, Tags.getChatUsers.name(), userMap);
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
    }
}
