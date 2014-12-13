package main;

import java.io.IOException;
import java.net.InetSocketAddress;
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
		try {
			socketChannel.connect(new InetSocketAddress(ip, port));
			System.out.println(ip + socketChannel.getLocalAddress());

			if (socketChannel.isConnectionPending()){
				socketChannel.finishConnect();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
