import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;


public class TcpConnection {
	
	private final int DEFAULT_PORT;
	private final String IP;
	
	private SocketChannel socketChannel;
	private SelectionKey selectionKey;
	
	public TcpConnection(String ip, int port)
	{
		DEFAULT_PORT=port;
		IP=ip;
	}
	
	
	public SelectionKey accept(Selector selector, SocketChannel socketChannel) throws IOException
	{
		
		this.socketChannel=socketChannel;
		try {
			//configure non-blocking mode
			socketChannel.configureBlocking(false);
			
			//set some options
			socketChannel.setOption(StandardSocketOptions.SO_RCVBUF, 128*1024);
			socketChannel.setOption(StandardSocketOptions.SO_SNDBUF, 128*1024);
			socketChannel.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
			
			//register the current channel with the given selector
			selectionKey= socketChannel.register(selector, SelectionKey.OP_CONNECT);
			

			return selectionKey;
		} catch (IOException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
			throw ex;
		}

		
	}
	
	public void connect()
	{
		//connect to remote host
		try {
			socketChannel.connect(new InetSocketAddress(IP, DEFAULT_PORT));
			System.out.println("Localhost: " + socketChannel.getLocalAddress());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
	
	public SocketChannel getSocketChannel()
	{
		return socketChannel;
	}
	
	public SelectionKey getSelectionKey()
	{
		return selectionKey;
	}
	
}
