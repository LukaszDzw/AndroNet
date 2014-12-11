import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;


public class TcpConnection extends Connection {
	
	private final int PORT;
	private final String IP;
	
	public TcpConnection(String ip, int port)
	{
		this.PORT=port;
		this.IP=ip;
	}

	public void accept(Selector selector, SocketChannel socketChannel) throws IOException
	{
		try {
			//configure non-blocking mode
			socketChannel.configureBlocking(false);
			
			//set some options
			//socketChannel.setOption(StandardSocketOptions.SO_RCVBUF, 128*1024);
			//socketChannel.setOption(StandardSocketOptions.SO_SNDBUF, 128*1024);
			//socketChannel.setOption(StandardSocketOptions.SO_KEEPALIVE, true);

			this.selectionKey=socketChannel.register(selector, SelectionKey.OP_WRITE | SelectionKey.OP_READ);
		} catch (IOException ex) {
			ex.printStackTrace();
			throw ex;
		}
	}
	
	public void connect(SocketChannel socketChannel)
	{
		//connect to remote host
		try {
			socketChannel.connect(new InetSocketAddress(IP, PORT));
			System.out.println(IP + socketChannel.getLocalAddress());

			if (socketChannel.isConnectionPending()){
				socketChannel.finishConnect();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
