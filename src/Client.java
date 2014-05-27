import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;


public class Client {

	private TcpConnection tcpConnection;
	private SocketChannel socketChannel;
	private Selector selector;
	
	public Client(String ip, int port)
	{
		 tcpConnection=new TcpConnection(ip, port);
	}
	
	public void start()
	{
		try
		{
			socketChannel=SocketChannel.open();
			selector=Selector.open();
			
			if(socketChannel.isOpen()&&selector.isOpen())
			{
				tcpConnection.accept(selector, socketChannel);
				tcpConnection.connect();
				run();
			}
			
		}
		catch(IOException ex)
		{
			System.err.print(ex);
		}
	}
	
	private void run() throws IOException
	{
        while (!Thread.interrupted()){
        	 
            selector.select(1000);
            
            Iterator<SelectionKey> keys = selector.selectedKeys().iterator();

            while (keys.hasNext()){
                SelectionKey key = keys.next();
                keys.remove();

                if (!key.isValid()) continue;

                if (key.isConnectable()){
                    System.out.println("I am connected to the server");
                    connect(key);
                }  
                if (key.isWritable()){
                    write(key);
                }
                if (key.isReadable()){
                    read(key);
                }
            }  
            
        }
	}
	
    private void connect(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        if (channel.isConnectionPending()){
            channel.finishConnect();
        }
        
        channel.configureBlocking(false);
        channel.register(selector, SelectionKey.OP_WRITE | SelectionKey.OP_READ);
        
    }
    
    private void read (SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        ByteBuffer readBuffer = ByteBuffer.allocate(1000);
        readBuffer.clear();
        int length;
        try{
        length = channel.read(readBuffer);
        } catch (IOException e){
            System.out.println("Reading problem, closing connection");
            key.cancel();
            channel.close();
            return;
        }
        if (length == -1){
            System.out.println("Nothing was read from server");
            channel.close();
            key.cancel();
            return;
        }
        readBuffer.flip();
        byte[] buff = new byte[1024];
        readBuffer.get(buff, 0, length);
        System.out.println("Server said: "+new String(buff));
    }

    private void write(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();

        //TODO
    }
}
