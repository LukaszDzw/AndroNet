package main;

import interfaces.IListener;

import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
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
    public abstract void listen(Selector selector);
    public abstract void accept(Selector selector, SocketChannel socketChannel);

    public void addListener(IListener listener)
    {

    }
}
