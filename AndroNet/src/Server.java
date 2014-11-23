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


public class Server {

	private Map<SocketChannel, List<byte[]>> keepDataTrack = new HashMap<>();
	private ByteBuffer buffer = ByteBuffer.allocate(2*1024);
	
	public void startEchoServer()
	{
		final int DEFAULT_PORT=5555;
		//final String GROUP="225.4.5.6";
		
		//otwieram selector oraz socket przez metod� open
		try(Selector selector =Selector.open();
			ServerSocketChannel serverSocketChannel=ServerSocketChannel.open())
			{
				//sprawdzam czy oba si� otworzy�y
				if(serverSocketChannel.isOpen() && selector.isOpen())
				{
					//non-blocking mode
					serverSocketChannel.configureBlocking(false);
					
					//dodanie opcji
					serverSocketChannel.setOption(StandardSocketOptions.SO_RCVBUF, 256*1024);
					serverSocketChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
					//serverSocketChannel.setOption(StandardSocketOptions.IP_MULTICAST_IF, networkInterface);
					
					
					//po��cz adres z portem
					serverSocketChannel.bind(new InetSocketAddress("127.0.0.1", DEFAULT_PORT));
					
					//rejestracja channelu do selectora
					serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
					
					//pokazanie wiadomo�ci, gdy serwer startuje
					System.out.println("Waiting for connections...");
					
					while(true)
					{
						//czekamy na nadchodz�ce zdarzenia
						selector.select();
						
						Iterator keys=selector.selectedKeys().iterator();
						
						while(keys.hasNext())
						{
							SelectionKey key =(SelectionKey) keys.next();
							
							//usuwamy klucz, aby nie by� obs�u�ony ponownie
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
	//isAcceptable zwr�ci�o true
	private void acceptOP(SelectionKey key, Selector selector) throws IOException
	{
		ServerSocketChannel serverChannel=(ServerSocketChannel) key.channel();
		SocketChannel socketChannel =serverChannel.accept();
		socketChannel.configureBlocking(false);
		
		System.out.println("Incoming connection from: " + socketChannel.getRemoteAddress());
		
		//welcome message
		//socketChannel.write(ByteBuffer.wrap("Hello!\n".getBytes("UTF-8")));
		
		//register channel do selektora dla pozniejszego i/o
		
		keepDataTrack.put(socketChannel,new ArrayList<byte[]>());
		socketChannel.register(selector, SelectionKey.OP_READ);
		
	}
	
	//do zrobienia readOp
	private void readOp(SelectionKey key)
	{
		try{
			SocketChannel socketChannel=(SocketChannel) key.channel();
			
			buffer.clear();
			
			int numRead=-1;
			try{
				numRead=socketChannel.read(buffer);
			} catch (IOException e){
				System.err.println("Cannot rad error!");
			}
			
			if(numRead==-1)
			{
				this.keepDataTrack.remove(socketChannel);
				System.out.println("Connection closed by: " + socketChannel.getRemoteAddress());
				socketChannel.close();
				return;
			}
			byte[] data = new byte[numRead];


			System.arraycopy(buffer.array(), 0, data, 0, numRead);
			//System.out.println(new String(data,"UTF-8") + " from" + socketChannel.getRemoteAddress());


			//write back to client
			doEchoJob(key, data);
			
			//sendToOthers(key, data);
		}
		catch(IOException ex){
			System.err.println(ex);
		}
		
	}
	
	private void writeOp(SelectionKey key) throws IOException
	{
		SocketChannel socketChannel = (SocketChannel) key.channel();
		
		List<byte[]> channelData=keepDataTrack.get(socketChannel);
		Iterator<byte[]> its=channelData.iterator();
		
		while(its.hasNext())
		{
			byte[] it=its.next();
			its.remove();
			socketChannel.write(ByteBuffer.wrap(it));
		}

		key.interestOps(SelectionKey.OP_READ);
	}
	
	/*
	private void sendToOthers(SelectionKey key, byte[] data)
	{
		SocketChannel socketChannel = (SocketChannel) key.channel();
		
		for(SocketChannel sc:keepDataTrack.keySet())
		{
			List<byte[]> channelData=keepDataTrack.get(sc);
			channelData.add(data);
			//sc.getLocalAddress()
			
		}
		
		key.interestOps(SelectionKey.OP_WRITE);
	}*/
	
	private void doEchoJob(SelectionKey key, byte[] data)
	{
		SocketChannel socketChannel = (SocketChannel) key.channel();
		List<byte[]> channelData=keepDataTrack.get(socketChannel);
		channelData.add(data);
		
		key.interestOps(SelectionKey.OP_WRITE);
	}
}
