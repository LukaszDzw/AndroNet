package main;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

/**
 * Created by Lukasz on 2014-12-08.
 */
public class Connection {

    protected SelectionKey selectionKey;
    private Serialization serialization;
    private final ByteBuffer readBuffer, writeBuffer;

    private int objectLength;
    //private final static int BUFFERCAPACITY = 1024;
    private final static int BUFFERCAPACITY = 4096;

    public Connection(SelectionKey selectionKey)
    {
        this.selectionKey=selectionKey;
        this.readBuffer=ByteBuffer.allocate(BUFFERCAPACITY);
        this.writeBuffer=ByteBuffer.allocate(BUFFERCAPACITY);
        this.readBuffer.clear();
        this.writeBuffer.clear();
        this.serialization=new Serialization();
    }

    public void send(String tag, Object object) throws UnsupportedEncodingException
    {
        Packet packet=new Packet();
        packet.object=object;
        packet.tag=tag;

        String json = this.serialization.getJsonFromObject(packet);

        synchronized(writeBuffer) {
            int bufferStart = writeBuffer.position();
            this.writeBuffer.putInt(json.getBytes().length);
            writeBuffer.position(bufferStart + serialization.getObjectLengthLength());
            this.writeBuffer.put(json.getBytes("UTF-8"));
        }

        this.selectionKey.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
        Selector selector = this.selectionKey.selector();
        selector.wakeup();
    }

    public Object read() throws IOException {
        int objectLengthLength=this.serialization.getObjectLengthLength();
        int bytesRead=0;

        SocketChannel socketChannel=(SocketChannel)this.selectionKey.channel();

        //odczytaj wielkość obiektu z bufora
        if(this.objectLength==0)
        {
            this.readBuffer.flip();
            if(this.readBuffer.remaining()<objectLengthLength)
            {
                this.readBuffer.compact();
                bytesRead=socketChannel.read(readBuffer);
            }

            this.readBuffer.flip();
            if(readBuffer.remaining()<objectLengthLength) //jeżeli bufor się jeszcze odpowiednio nie zapełnił
            {
                if(bytesRead==-1) throw new SocketException("Channel is closed");
                return null;
            }

            this.objectLength=serialization.getObjectLength(readBuffer);
            if(this.objectLength>readBuffer.capacity()) throw new IOException("Object is bigger than buffer capacity. Closing connection");

            System.out.println("dlugosc " + this.objectLength);
        }

        //dopełnij bufor, jeśli za mało wczytał
        if(this.readBuffer.remaining()<this.objectLength)
        {
            this.readBuffer.compact();
            bytesRead=socketChannel.read(readBuffer);
            this.readBuffer.flip();
            if(readBuffer.remaining()<this.objectLength) //jeżeli bufor się jeszcze odpowiednio nie zapełnił
            {
                if(bytesRead==-1) throw new SocketException("Channel is closed");
                return null;
            }
        }

        Object object = this.serialization.getObjectFromBuffer(this.readBuffer, this.objectLength);
        this.objectLength=0;
        this.readBuffer.compact();

        return object;
    }

    public void write() throws IOException
    {
        SocketChannel socketChannel=(SocketChannel)this.selectionKey.channel();

        synchronized (this.writeBuffer) {
            this.writeBuffer.flip();
            while(writeBuffer.hasRemaining()) {
                socketChannel.write(writeBuffer);
            }
            writeBuffer.compact();
        }

        this.selectionKey.interestOps(SelectionKey.OP_READ);
    }

    public void close() throws IOException
    {
        SocketChannel channel = (SocketChannel) this.selectionKey.channel();
        if(channel.isOpen()) {
            channel.close();
        }
    }
}
