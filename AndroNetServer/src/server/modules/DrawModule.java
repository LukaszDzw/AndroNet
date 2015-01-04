package server.modules;

import interfaces.IListener;
import main.Connection;
import main.Server;
import server.interfaces.IModule;

/**
 * Created by Lukasz on 2015-01-03.
 */
public class DrawModule implements IModule {
    @Override
    public void setup(final Server server) {
        server.addListener("drawPosition", new IListener() {
            @Override
            public void received(Connection connection, Object object) {
                server.sendToAll("drawPosition", object);
            }
        });
    }
}
