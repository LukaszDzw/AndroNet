package main;

import interfaces.IDisconnected;
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
    protected ExecutorService executorService;

    private IDisconnected disconnectedAction;

    public EndPoint()
    {
        this.listeners=new HashMap<>();
        this.executorService = Executors.newSingleThreadExecutor();
    }

    public IDisconnected getDisconnectedAction()
    {
        return this.disconnectedAction;
    }

    public void setDisconnectedAction(IDisconnected disconnectedAction)
    {
        this.disconnectedAction=disconnectedAction;
    }


    protected abstract void listen(Selector selector) throws IOException;

    protected void start()
    {
        if(this.executorService.isShutdown())
        {
            this.executorService = Executors.newSingleThreadExecutor();
        }
    }

    public void addListener(String tag, IListener listener)
    {
        this.listeners.put(tag, listener);
    }

    public void removeListener(String tag)
    {
        this.listeners.remove(tag);
    }

    public void removeListeners()
    {
        this.listeners.clear();
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

    protected void notifyReceived(Object object, final Connection connection)
    {
        final Packet packet=(Packet)object;
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

    protected void closeConnection(final Connection connection) throws IOException
    {
        final IDisconnected disconnectedAction = this.getDisconnectedAction();
        if(disconnectedAction!=null)
        {
            Runnable task=new Runnable() {
                @Override
                public void run() {
                    disconnectedAction.disconnected(connection);
                }
            };

            this.executorService.execute(task);
            connection.close();
        }
    }
}
