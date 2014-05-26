import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;


public class Client 
{
	private int timeout;
	private TcpConnection tcpConnection;
	
	
	public Client(String ip, int port, int timeout)
	{
		this(ip,port);
		this.timeout=timeout;
	}
	
	public Client(String ip, int port)
	{
		tcpConnection=new TcpConnection(ip, port);
		timeout=1000;
	}
	
	public void start()
	{

			
			
			
			//open Selector and ServerSocketChannel by calling the open() method
			try(Selector selector =Selector.open();
				SocketChannel socketChannel=SocketChannel.open())
				{
					//check that both of them were succesfully opened
					if((socketChannel.isOpen() && (selector.isOpen())))
					{
						tcpConnection.accept(selector, socketChannel);
						tcpConnection.connect();
						
						SocketChannel keySocketChannel=waitForConnection();
						if(keySocketChannel==null) return;
						run(selector);
						
					}
					else
					{
						System.out.println("The socket channel or selector cannot be opened!");
					}
				}
				catch (IOException ex) {
					System.err.println(ex);
				}
	}
	
	private SocketChannel waitForConnection() throws IOException
	{
		SocketChannel socketChannel=tcpConnection.getSocketChannel();
						
						//close pending connections
						if(socketChannel.isConnectionPending())
						{
							socketChannel.finishConnect();
							return socketChannel;
						}
						
						return null; // TODO zrobiæ inaczej!
						

	}
	private void run(Selector selector) throws IOException
	{
		Charset charset=Charset.defaultCharset();
		CharsetDecoder decoder=charset.newDecoder();
		ByteBuffer buffer =ByteBuffer.allocateDirect(2*1024);
		ByteBuffer randomBuffer;
		CharBuffer charBuffer;
		
		
		/*
		keySocketChannel=tcpConnection.getSocketChannel();
		while(keySocketChannel.read(buffer)!=-1)
		{
			buffer.flip();
			charBuffer=decoder.decode(buffer);
			System.out.println(charBuffer.toString());
			
			if(buffer.hasRemaining()){
				buffer.compact();
			} else {
				buffer.clear();
			}
			
			int r=new Random().nextInt(100);
			if(r==50)
			{
				System.out.println("50 was generated! Close the socket channel");
				keySocketChannel.close();
				break;
			}
			else
			{
				randomBuffer=ByteBuffer.wrap("Random number:)".concat(String.valueOf(r)).getBytes("UTF-8"));
				keySocketChannel.write(randomBuffer);
				try{
					Thread.sleep(1500);
				}catch(InterruptedException ex){}
			}
		}*/
		
		
	}
}
