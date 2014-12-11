import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.StandardSocketOptions;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;


public class Client{

	private TcpConnection tcpConnection;
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
						accept(selector,socketChannel);
						tcpConnection.connect(ip, port);

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
			this.tcpConnection.send(object, tag);
		}
		catch (UnsupportedEncodingException ex)
		{
			System.out.println(ex.toString());
		}
	}
	
	private void listen(Selector selector) throws IOException
	{
        while (!Thread.interrupted()){
        	 
            selector.select();
            
            Iterator<SelectionKey> keys = selector.selectedKeys().iterator();

            while (keys.hasNext()){
                SelectionKey key = keys.next();
                keys.remove();

                if (!key.isValid()) continue;

                if (key.isWritable()){
                    //this.write(key);
					this.tcpConnection.write();
                }
                else if (key.isReadable()){
                    //this.read(key);
					this.tcpConnection.read();
                }
            }  
        }
	}

	public void accept(Selector selector, SocketChannel socketChannel) throws IOException
	{
		try {
			//configure non-blocking mode
			socketChannel.configureBlocking(false);

			//set some options
			socketChannel.setOption(StandardSocketOptions.SO_RCVBUF, 128*1024); //standard setting
			socketChannel.setOption(StandardSocketOptions.SO_SNDBUF, 128*1024); //standard setting
			socketChannel.setOption(StandardSocketOptions.SO_KEEPALIVE, true); //TODO

			SelectionKey selectionKey;
			selectionKey=socketChannel.register(selector, SelectionKey.OP_WRITE | SelectionKey.OP_READ);
			this.tcpConnection=new TcpConnection(selectionKey);

		} catch (IOException ex) {
			ex.printStackTrace();
			throw ex;
		}
	}
}
