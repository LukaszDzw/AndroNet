package main;

import interfaces.IListener;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Socket;
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
						connect(selector, socketChannel);
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
			this.clientConnection.send(tag, object);
		}
		catch (UnsupportedEncodingException ex)
		{
			System.out.println(ex.toString());
		}
	}

	private void connect(Selector selector, SocketChannel socketChannel) throws IOException
	{
		Socket socket=socketChannel.socket();
		socket.setTcpNoDelay(true); // TODO przeczytać więcej
		socket.connect(new InetSocketAddress(ip, port), 5000); // connecting in blocking mode

		socketChannel.configureBlocking(false);
		SelectionKey selectionKey=socketChannel.register(selector, SelectionKey.OP_READ);
		this.clientConnection=new ClientConnection(selectionKey);
	}
	
	protected void listen(Selector selector) throws IOException
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
						Packet packet = this.clientConnection.read();
						this.notifyReceived(packet, clientConnection);
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
