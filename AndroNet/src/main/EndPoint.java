package main;

import interfaces.IListener;

import java.io.IOException;
import java.nio.channels.Selector;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Lukasz on 2014-12-13.
 */
public abstract class EndPoint {
    protected Map<String, IListener> listeners;

    public EndPoint()
    {
        this.listeners=new HashMap<>();
    }

    public abstract void start();
    protected abstract void listen(Selector selector) throws IOException;

    public void addListener(String tag, IListener listener)
    {
        this.listeners.put(tag, listener);
    }

    public void removeListener(String tag)
    {
        this.listeners.remove(tag);
    }

    protected void notifyReceived(Packet packet, Connection connection)
    {
        IListener listener = this.listeners.get(packet.tag);
        if(listener!=null) {
            listener.received(connection, packet.object);
        }
    }
}
