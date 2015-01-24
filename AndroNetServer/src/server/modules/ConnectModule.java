package server.modules;

import interfaces.IDisconnected;
import interfaces.IListener;
import main.Connection;
import main.Server;
import pl.umk.andronetandroidclient.network.enums.Tags;
import server.interfaces.IModule;

/**
 * Created by Lukasz on 2015-01-06.
 */
public class ConnectModule implements IModule {
    @Override
    public void setup(final Server server) {
        server.addListener(Tags.hello.name(), new IListener() {
            @Override
            public void received(Connection connection, Object o) {
                server.sendTo(connection, Tags.hello.name(), o);
            }
        });
        server.setDisconnectedAction(new IDisconnected() {
            @Override
            public void disconnected(Connection connection) {
                System.out.println("bye connection " + connection.getId());
            }
        });
    }
}
