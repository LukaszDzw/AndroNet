import interfaces.IListener;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;


public class Server {
	private final int PORT;
	//private Map<SocketChannel, List<byte[]>> keepDataTrack = new HashMap<>();
	//private Map<SelectionKey, Connection> connections; //TODO

	private ByteBuffer buffer = ByteBuffer.allocate(2*1024);
	private SelectionKey serverSelectionKey;

	private Map<String, IListener> listeners;

	public Server(int PORT)
	{
		this.PORT = PORT;
		this.listeners=new HashMap<>();
		//this.connections=new HashMap<>();
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

	//isAcceptable zwróciło true
	private void acceptOP(SelectionKey key, Selector selector) throws IOException
	{
		ServerSocketChannel serverChannel=(ServerSocketChannel) key.channel();
		SocketChannel socketChannel =serverChannel.accept();
		socketChannel.configureBlocking(false);
		
		System.out.println("Incoming connection from: " + socketChannel.getRemoteAddress());

		SelectionKey selectionKey = socketChannel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
		Connection connection=new Connection(selectionKey);
		selectionKey.attach(connection);
	}

	private void read(SelectionKey key)
	{
		Connection connection = (Connection) key.attachment();
		try {
			connection.read();
		}
		catch (IOException ex)
		{
			System.out.println(ex.toString());
		}
	}

	private void write(SelectionKey key)
	{
		Connection connection = (Connection) key.attachment();
		try {
			connection.write();
		}
		catch (IOException ex)
		{
			System.out.println(ex.toString());
		}
	}
	/*
	private void readOp(SelectionKey key)
	{
		try{
			SocketChannel socketChannel=(SocketChannel) key.channel();
			
			buffer.clear();
			
			int numRead=-1;
			try{
				numRead=socketChannel.read(buffer);
			} catch (IOException e){
				System.err.println("Cannot rad error!");
			}
			
			if(numRead==-1)
			{
				this.connections.remove(key);
				System.out.println("Connection closed by: " + socketChannel.getRemoteAddress());
				socketChannel.close();
				return;
			}

			byte[] data = new byte[numRead];
			System.arraycopy(buffer.array(), 0, data, 0, numRead);

			//write back to client
			//doEcho(key, data);
			
			this.sendToAll(key, data);
		}
		catch(IOException ex) {
			System.err.println(ex);
		}
	}*/

	/*
	private void writeOp(SelectionKey key) throws IOException
	{
		SocketChannel socketChannel = (SocketChannel) key.channel();
		
		List<byte[]> channelData=keepDataTrack.get(socketChannel);
		Iterator<byte[]> its=channelData.iterator();

		while(its.hasNext())
		{
			byte[] it=its.next();
			its.remove();
			socketChannel.write(ByteBuffer.wrap(it));
		}

		key.interestOps(SelectionKey.OP_READ);
	}*/

	//TODO
	/*
	public void sendToAll(SelectionKey key, byte[] data)
	{
		Selector selector=key.selector();
		Iterator<SelectionKey> keys=selector.keys().iterator();

		while(keys.hasNext())
		{
			SelectionKey selKey = keys.next();
			if(selKey.channel() instanceof ServerSocketChannel) continue; //omijamy serverSocketChannel, który także jest dodany do selektora

			SocketChannel channel = (SocketChannel) selKey.channel();
			List<byte[]> channelData=keepDataTrack.get(channel);
			channelData.add(data);
			selKey.interestOps(SelectionKey.OP_WRITE);
		}
	}*/

	//TODO
	/*
	public void doEcho(SelectionKey key, byte[] data)
	{
		SocketChannel socketChannel = (SocketChannel) key.channel();
		List<byte[]> channelData=keepDataTrack.get(socketChannel);
		channelData.add(data);
		
		key.interestOps(SelectionKey.OP_WRITE);
	}*/

	private void setServerOptions(ServerSocketChannel serverSocketChannel, Selector selector) throws IOException
	{
		//non-blocking mode
		serverSocketChannel.configureBlocking(false);

		//połącz adres z portem
		serverSocketChannel.bind(new InetSocketAddress("127.0.0.1", this.PORT));

		//rejestracja channelu do selectora
		this.serverSelectionKey=serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
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
				SelectionKey key =(SelectionKey) keys.next();

				//usuwamy klucz, aby nie był obsłużony ponownie
				keys.remove();
				if(!key.isValid()) continue;

				if(key.isAcceptable()){
					this.acceptOP(key, selector);
				}
				else if(key.isReadable())
				{
					this.read(key);
					//this.readOp(key);
				}
				else if(key.isWritable())
				{
					this.write(key);
					//this.writeOp(key);
				}
			}
		}
	}
}
