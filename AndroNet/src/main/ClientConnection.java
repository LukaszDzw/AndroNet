package main;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;


public class ClientConnection extends Connection {
	private static boolean isConnected;

	public ClientConnection(SelectionKey selectionKey)
	{
		super(selectionKey);
	}

	public static void setConnected(boolean value)
	{
		isConnected=value;
	}

	public static boolean isConnected()
	{
		return isConnected;
	}
}
