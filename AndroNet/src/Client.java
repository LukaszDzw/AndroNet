import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;


public class Client{

	private TcpConnection tcpConnection;
	private SocketChannel socketChannel;
	private Selector selector;
	private final ByteBuffer readBuffer, writeBuffer;
	
	private final int BUFFERCAPACITY = 1000;
	
	public Client(String ip, int port)
	{
		 this.tcpConnection=new TcpConnection(ip, port);
		 this.readBuffer=ByteBuffer.allocate(BUFFERCAPACITY);
		 this.writeBuffer=ByteBuffer.allocate(BUFFERCAPACITY);
	}
	
	public void start()
	{
		try
		{
			socketChannel=SocketChannel.open();
			selector=Selector.open();
					
			if(socketChannel.isOpen() && selector.isOpen())
			{
				tcpConnection.accept(selector, socketChannel);
				tcpConnection.connect();
						
				//puszczamy p�tl� na osobnym w�tku
				new Thread(new Runnable() 
				{
							
					@Override
					public void run() 
					{
						try 
						{
							listen();
						} 
						catch (IOException e) 
						{
							e.printStackTrace();
						}
					}
				}).start();
						
			}
					
		}
		catch(IOException ex)
		{
			System.err.print(ex);
		}
	}
	
	public void send(String message)
	{
		while(!socketChannel.isConnected())
		{
			try {
				TimeUnit.MILLISECONDS.sleep(50);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		SelectionKey key=tcpConnection.getSelectionKey();
		key.attach(message);
		
        int start=this.writeBuffer.position();
        

        try 
        {
			this.write(key);
		} 
        catch (IOException e) {
			e.printStackTrace();
		}
        /*
        if(start==0) //jeśli pusty to zapisuję dane
        {
        	key.interestOps(SelectionKey.OP_WRITE | SelectionKey.OP_READ);
        }
        else
        {
        	key.selector().wakeup();
        }*/
	}
	
	
	
	private void listen() throws IOException
	{
        while (!Thread.interrupted()){
        	 
            selector.select();
            
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
        this.readBuffer.clear();
        int length;
        try{
        length = channel.read(this.readBuffer);
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
        this.readBuffer.flip();
        byte[] buff = new byte[1024];
        this.readBuffer.get(buff, 0, length);
        System.out.println("Server said: "+new String(buff));
    }

    private void write(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();

        if(key.attachment()!=null)
        try{
        	 String messageString=(String)key.attachment();
        	 this.writeBuffer.put(messageString.getBytes("UTF-8"));
        	 writeBuffer.flip();
        	 channel.write(writeBuffer);
        	 writeBuffer.compact();
        	 //writeBuffer.flip();
        }
        catch(Exception ex)
        {
        	System.err.println("Wysłany pakiet nie można przerobić na stringa");
        }
        
        key.interestOps(SelectionKey.OP_READ);
    }


}
