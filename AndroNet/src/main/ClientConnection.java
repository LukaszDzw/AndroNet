package main;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;


public class ClientConnection extends Connection {


	public ClientConnection(SelectionKey selectionKey)
	{
		super(selectionKey);
	}
	
	public void connect(String ip, int port)
	{
		SocketChannel socketChannel = (SocketChannel) this.selectionKey.channel();
		//connect to remote host


	}
	
}
