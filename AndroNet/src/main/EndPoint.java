package main;

import interfaces.IListener;

import java.io.IOException;
import java.nio.channels.Selector;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Lukasz on 2014-12-13.
 */
public abstract class EndPoint {
    protected final Map<String, IListener> listeners;
    protected Thread updateThread;
    private ExecutorService executorService;

    public EndPoint()
    {
        this.listeners=new HashMap<>();
        this.executorService = Executors.newSingleThreadExecutor();
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

    public void close()
    {
        this.executorService.shutdownNow();
        if(this.updateThread!=null)
        {
            this.updateThread.interrupt();
        }
    }

    protected void startListeningThread(Runnable runnable)
    {
        this.updateThread=new Thread(runnable);
        this.updateThread.start();
    }

    protected void notifyReceived(final Packet packet, final Connection connection)
    {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                IListener listener = listeners.get(packet.tag);
                if(listener!=null) {
                    listener.received(connection, packet.object);
                }
            }
        };
        this.executorService.execute(runnable);
    }
}
