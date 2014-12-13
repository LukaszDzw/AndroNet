package main;

import interfaces.IListener;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.StandardSocketOptions;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Map;


public class Client extends EndPoint{

	private ClientConnection clientConnection;
	private final String ip;
	private final int port;

	public Client(String ip, int port)
	{
		super();
		this.ip=ip;
		this.port=port;
	}

	public void start()
	{
		new Thread(new Runnable()
		{
			@Override
			public void run() {
				try (final Selector selector = Selector.open();
					 final SocketChannel socketChannel = SocketChannel.open()) {
					if (socketChannel.isOpen() && selector.isOpen()) {
						accept(selector,socketChannel);
						clientConnection.connect(ip, port);

						listen(selector);
					}
				}
				catch(IOException ex)
				{
					System.err.print(ex);
				}
			}
		}).start();
	}

	public void send(String tag, Object object)
	{
		try {
			this.clientConnection.send(object, tag);
		}
		catch (UnsupportedEncodingException ex)
		{
			System.out.println(ex.toString());
		}
	}

	public void accept(Selector selector, SocketChannel socketChannel) throws IOException
	{
		//configure non-blocking mode
		socketChannel.configureBlocking(false);

		//set some options
		socketChannel.setOption(StandardSocketOptions.SO_RCVBUF, 128*1024); //standard setting
		socketChannel.setOption(StandardSocketOptions.SO_SNDBUF, 128*1024); //standard setting
		socketChannel.setOption(StandardSocketOptions.SO_KEEPALIVE, true);

		SelectionKey selectionKey;
		selectionKey=socketChannel.register(selector, SelectionKey.OP_WRITE | SelectionKey.OP_READ);
		this.clientConnection =new ClientConnection(selectionKey);
	}
	
	private void listen(Selector selector) throws IOException
	{
        while (!Thread.interrupted()){
        	 
            selector.select();
            Iterator<SelectionKey> keys = selector.selectedKeys().iterator();

            while (keys.hasNext()){
                SelectionKey key = keys.next();
                keys.remove();

                if (!key.isValid()) continue;

				try {
					if (key.isWritable()) {
						this.clientConnection.write();
					} else if (key.isReadable()) {
						this.clientConnection.read();
					}
				}
				catch (IOException ex)
				{
					this.clientConnection.close();
					System.out.println("Connection closed by host");
				}
            }  
        }
	}
}
