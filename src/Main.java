import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;




public class Main {

	private Map<SocketChannel, List<byte[]>> keepDataTrack = new HashMap<>();
	private ByteBuffer buffer = ByteBuffer.allocate(2*1024);
	
	private void startEchoServer()
	{
		final int DEFAULT_PORT=5555;
		
		//otwieram selector oraz socket przez metodê open
		try(Selector selector =Selector.open();
			ServerSocketChannel serverSocketChannel=ServerSocketChannel.open())
			{
				//sprawdzam czy oba siê otworzy³y
				if(serverSocketChannel.isOpen() && selector.isOpen())
				{
					//non-blocking mode
					serverSocketChannel.configureBlocking(false);
					
					//dodanie opcji
					serverSocketChannel.setOption(StandardSocketOptions.SO_RCVBUF, 256*1024);
					serverSocketChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
					
					//po³¹cz adres z portem
					serverSocketChannel.bind(new InetSocketAddress(DEFAULT_PORT));
					
					//rejestracja channelu do selectora
					serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
					
					//pokazanie wiadomoœci, gdy serwer startuje
					System.out.println("Waiting for connections...");
					
					while(true)
					{
						selector.select();
						
						Iterator keys=selector.selectedKeys().iterator();
						
						while(keys.hasNext())
						{
							SelectionKey key =(SelectionKey) keys.next();
							
							//usuwamy klucz, aby nie by³ obs³u¿ony ponownie
							keys.remove();
							if(!key.isValid())
							{
								continue;
							}
							if(key.isAcceptable()){
								acceptOP(key, selector);
							}
							else if(key.isReadable())
							{
								this.readOp(key);
							}
							else if(key.isWritable())
							{
								this.writeOp(key);
							}
						}
					}
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
	//isAcceptable zwróci³o true
	private void acceptOP(SelectionKey key, Selector selector) throws IOException
	{
		ServerSocketChannel serverChannel=(ServerSocketChannel) key.channel();
		SocketChannel socketChannel =serverChannel.accept();
		socketChannel.configureBlocking(false);
		
		System.out.println("Incoming connection from: " + socketChannel.getRemoteAddress());
		
		//welcome message
		socketChannel.write(ByteBuffer.wrap("Hello!\n".getBytes("UTF-8")));
		
		//register channel do selektora dla pozniejszego i/o
		
		keepDataTrack.put(socketChannel,new ArrayList<byte[]>());
		socketChannel.register(selector, SelectionKey.OP_READ);
	}
	
	//do zrobienia readOp

}

