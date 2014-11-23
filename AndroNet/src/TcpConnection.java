import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;


public class TcpConnection {
	
	private final int PORT;
	private final String IP;
	
	private SocketChannel socketChannel;
	private SelectionKey selectionKey;
	
	public TcpConnection(String ip, int port)
	{
		this.PORT=port;
		this.IP=ip;
	}
	
	
	
	public SelectionKey accept(Selector selector, SocketChannel socketChannel) throws IOException
	{
		
		this.socketChannel=socketChannel;
		try {
			//configure non-blocking mode
			this.socketChannel.configureBlocking(false);
			
			//set some options
			this.socketChannel.setOption(StandardSocketOptions.SO_RCVBUF, 128*1024);
			this.socketChannel.setOption(StandardSocketOptions.SO_SNDBUF, 128*1024);
			this.socketChannel.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
			
			//register the current channel with the given selector
			this.selectionKey= this.socketChannel.register(selector, SelectionKey.OP_CONNECT);
			

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
			this.socketChannel.connect(new InetSocketAddress(IP, PORT));
			System.out.println(IP + socketChannel.getLocalAddress());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
	
	public SocketChannel getSocketChannel()
	{
		return this.socketChannel;
	}
	
	
	public SelectionKey getSelectionKey()
	{
		return this.selectionKey;
	}
	
}