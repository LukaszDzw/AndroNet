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
	private Serialization serialization;
	
	private final int BUFFERCAPACITY = 1000;
	private int objectLength;
	
	public Client(String ip, int port)
	{
		this.tcpConnection=new TcpConnection(ip, port);
		this.readBuffer=ByteBuffer.allocate(BUFFERCAPACITY);

		this.writeBuffer=ByteBuffer.allocate(BUFFERCAPACITY);
		this.serialization=new Serialization();
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
	
	public void send(Object object)
	{
		while(!socketChannel.isConnected())
		{
			try {
				TimeUnit.MILLISECONDS.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		SelectionKey key=tcpConnection.getSelectionKey();
		key.attach(object);
		
        int start=this.writeBuffer.position();

        try 
        {
			this.write(key);
		} 
        catch (IOException e) {
			e.printStackTrace();
		}
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
    
    private Object read (SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
		int objectLengthLength=this.serialization.getObjectLengthLength();

		//odczytaj wielkość obiektu z bufora
		if(this.objectLength==0)
		{
			if(readBuffer.remaining()<objectLengthLength)
			{
				this.readBuffer.compact();
				this.socketChannel.read(readBuffer);
			}

			this.readBuffer.flip();
			if(readBuffer.remaining()<objectLengthLength) return null; //jeżeli bufor się jeszcze odpowiednio nie zapełnił
			this.objectLength=serialization.getObjectLength(readBuffer);
			System.out.println("dlugosc " + this.objectLength);
		}

		//dopełnij bufor, jeśli za mało wczytał
		if(this.readBuffer.remaining()<this.objectLength)
		{
			readBuffer.compact();
			this.socketChannel.read(readBuffer);
			this.readBuffer.flip();
			if(readBuffer.remaining()<this.objectLength) return null; //jeżeli bufor się jeszcze odpowiednio nie zapełnił
		}

		Object object = this.serialization.getObjectFromBuffer(readBuffer, objectLength);
		this.objectLength=0;
		this.readBuffer.compact();

		System.out.println(object.toString()); //temp
		return object;
    }

    private void write(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();

        if(key.attachment()!=null) {
			try {
				Object attachment = key.attachment();
				String json = this.serialization.getJsonFromObject(attachment);

				this.writeBuffer.putInt(json.getBytes().length);
				this.writeBuffer.put(json.getBytes("UTF-8"));

				writeBuffer.flip();
				channel.write(writeBuffer);

				writeBuffer.compact();
			} catch (Exception ex) {
				System.err.println("Wysłany pakiet nie można przerobić na stringa");
			}
		}
        
        key.interestOps(SelectionKey.OP_READ);
    }


}
