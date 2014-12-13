package main;

import interfaces.IListener;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class Server extends EndPoint {
	private final int port;
	private SelectionKey serverSelectionKey;

	public Server(int port)
	{
		this.port = port;
		this.listeners=new HashMap<>();
	}
	
	public void start()
	{
		new Thread(new Runnable() {
			@Override
			public void run() {

				//otwieram selector oraz socket przez metodę open
				try(Selector selector =Selector.open();
					ServerSocketChannel serverSocketChannel=ServerSocketChannel.open())
				{
					//sprawdzam czy oba się otworzyły
					if(serverSocketChannel.isOpen() && selector.isOpen())
					{
						setServerOptions(serverSocketChannel, selector);

						System.out.println("Waiting for connections...");
						listen(selector);
					}
					else {
						System.out.println("The server socket channel or selector cannot be opened!");
					}
				}
				catch(IOException ex)
				{
					System.err.println(ex);
				}
			}
		}).start();
	}

	public void sendToAll(String tag, Object object)
	{
		Selector selector=this.serverSelectionKey.selector();
		Iterator<SelectionKey> keys=selector.keys().iterator();

		while(keys.hasNext())
		{
			SelectionKey selKey = keys.next();
			if(selKey.channel() instanceof ServerSocketChannel) continue; //omijamy serverSocketChannel, który także jest dodany do selektora

			Connection connection = (Connection) selKey.attachment();
			try {
				connection.send(object, tag);
				selKey.interestOps(SelectionKey.OP_WRITE);
			}
			catch (UnsupportedEncodingException ex)
			{
				System.err.println(ex.toString());
			}
		}
	}

	//isAcceptable zwróciło true
	private void accept(SelectionKey key, Selector selector) throws IOException
	{
		ServerSocketChannel serverChannel=(ServerSocketChannel) key.channel();
		SocketChannel socketChannel =serverChannel.accept();
		socketChannel.configureBlocking(false);

		System.out.println("Incoming connection from: " + socketChannel.getRemoteAddress());

		SelectionKey selectionKey = socketChannel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
		Connection connection=new Connection(selectionKey);
		selectionKey.attach(connection);
	}

	private void listen(Selector selector) throws IOException
	{
		while(true)
		{
			//czekamy na nadchodzące zdarzenia
			selector.select();
			Iterator keys=selector.selectedKeys().iterator();

			while(keys.hasNext())
			{
				SelectionKey key = (SelectionKey) keys.next();
				Connection connection = (Connection) key.attachment();

				//usuwamy klucz, aby nie był obsłużony ponownie
				keys.remove();
				try {
					if (!key.isValid()) continue;

					if (key.isAcceptable()) {
						this.accept(key, selector);
					} else if (key.isReadable()) {
						Object object=connection.read().object;
						this.sendToAll("test", object); // temp
					} else if (key.isWritable()) {
						connection.write();
					}
				}
				catch (IOException ex)
				{
					connection.close();
					System.out.println("Connection closed by host");
				}
			}
		}
	}

	private void setServerOptions(ServerSocketChannel serverSocketChannel, Selector selector) throws IOException
	{
		//non-blocking mode
		serverSocketChannel.configureBlocking(false);

		//połącz adres z portem
		serverSocketChannel.bind(new InetSocketAddress("127.0.0.1", this.port));

		//rejestracja channelu do selectora
		this.serverSelectionKey=serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
	}
}
