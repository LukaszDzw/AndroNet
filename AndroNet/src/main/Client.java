package main;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;


public class Client extends EndPoint{

	private Connection clientConnection;
	private String ip;
	private int port;
	private boolean isConnected;

	public void start(String ip, int port)
	{
		super.start();
		this.ip=ip;
		this.port=port;


		if(this.updateThread!=null) {
			if(this.updateThread.isAlive()) {
				return; //jeżeli klient nasłuchuje
			}
		}

		Runnable listeningTask = new Runnable()
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
		};
		this.startListeningThread(listeningTask);

		//block thread for connection
		try {
			while (!this.isConnected) Thread.sleep(50);
		}
		catch (InterruptedException ex)
		{
			System.err.print(ex.toString());
		}
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
		catch (CancelledKeyException ex)
		{
			System.out.println(ex.toString());
		}
	}

	@Override
	public void close()
	{
		this.isConnected=false;
		SocketChannel channel=(SocketChannel)this.clientConnection.getSelectionKey().channel();
		try {
			channel.close();
		}
		catch (IOException e)
		{
			System.err.println(e.toString());
		}
		super.close();
	}

	public boolean isConnected()
	{
		return this.isConnected;
	}
	
	protected void listen(Selector selector) throws IOException
	{
        while (!Thread.interrupted()){
        	 
            selector.select();
            Iterator<SelectionKey> keys = selector.selectedKeys().iterator();

			synchronized (keys) {
				while (keys.hasNext()) {
					SelectionKey key = keys.next();
					keys.remove();

					if (!key.isValid()) continue;

					try {
						if (key.isWritable()) {
							this.clientConnection.write();
						} else if (key.isReadable()) {
							Object object = this.clientConnection.read();
							if (object != null) {
								this.notifyReceived(object, clientConnection);
							}
						}
					} catch (IOException ex) {
						System.out.println("Connection closed by host");
						System.err.println(ex.toString());
						this.closeConnection(clientConnection);
					}
				}
			}
        }
	}

	@Override
	protected void closeConnection(final Connection connection) throws IOException
	{
		super.closeConnection(connection);
		this.close();
	}

	private void connect(Selector selector, SocketChannel socketChannel) throws IOException
	{
		Socket socket=socketChannel.socket();
		socket.setTcpNoDelay(true);
		socket.connect(new InetSocketAddress(ip, port), 5000); // connecting in blocking mode

		socketChannel.configureBlocking(false);
		SelectionKey selectionKey=socketChannel.register(selector, SelectionKey.OP_READ);
		this.clientConnection=new Connection(selectionKey);
		this.isConnected=true;
	}
}
