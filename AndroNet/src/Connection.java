import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Lukasz on 2014-12-08.
 */
public class Connection {

    protected SelectionKey selectionKey; //TODO accept method
    private Serialization serialization;
    private final ByteBuffer readBuffer, writeBuffer;

    private int objectLength;
    private final static int BUFFERCAPACITY = 1000;

    public Connection()
    {
        this.readBuffer=ByteBuffer.allocate(BUFFERCAPACITY);
        this.writeBuffer=ByteBuffer.allocate(BUFFERCAPACITY);
        this.serialization=new Serialization();
    }

    public void send(Object object) throws UnsupportedEncodingException
    {
        String json = this.serialization.getJsonFromObject(object);
        this.writeBuffer.putInt(json.getBytes().length);
        this.writeBuffer.put(json.getBytes("UTF-8"));
        this.selectionKey.interestOps(SelectionKey.OP_WRITE);

        Selector selector = this.selectionKey.selector();
        selector.wakeup();
    }

    private Object read(SelectionKey selectionKey) throws IOException {
        int objectLengthLength=this.serialization.getObjectLengthLength();
        SocketChannel socketChannel=(SocketChannel)selectionKey.channel();
        //odczytaj wielkość obiektu z bufora
        if(this.objectLength==0)
        {
            if(this.readBuffer.remaining()<objectLengthLength)
            {
                this.readBuffer.compact();
                socketChannel.read(readBuffer);
            }

            this.readBuffer.flip();
            if(readBuffer.remaining()<objectLengthLength) return null; //jeżeli bufor się jeszcze odpowiednio nie zapełnił
            this.objectLength=serialization.getObjectLength(readBuffer);
            System.out.println("dlugosc " + this.objectLength);
        }

        //dopełnij bufor, jeśli za mało wczytał
        if(this.readBuffer.remaining()<this.objectLength)
        {
            this.readBuffer.compact();
            socketChannel.read(readBuffer);
            this.readBuffer.flip();
            if(readBuffer.remaining()<this.objectLength) return null; //jeżeli bufor się jeszcze odpowiednio nie zapełnił
        }

        Object object = this.serialization.getObjectFromBuffer(this.readBuffer, this.objectLength);
        this.objectLength=0;
        this.readBuffer.compact();

        System.out.println(object.toString()); //temp
        return object;
    }

    private void writeOp(SelectionKey selectionKey) throws IOException
    {
        SocketChannel socketChannel=(SocketChannel)selectionKey.channel();

        synchronized (this.writeBuffer) {
            this.writeBuffer.flip();
            socketChannel.write(writeBuffer);
            writeBuffer.compact();
        }

        selectionKey.interestOps(SelectionKey.OP_READ);
    }
}
