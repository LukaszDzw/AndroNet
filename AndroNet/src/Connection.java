import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * Created by Lukasz on 2014-12-08.
 */
public abstract class Connection {

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

    private Object read(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        int objectLengthLength=this.serialization.getObjectLengthLength();

        //odczytaj wielkość obiektu z bufora
        if(this.objectLength==0)
        {
            if(this.readBuffer.remaining()<objectLengthLength)
            {
                this.readBuffer.compact();
                channel.read(readBuffer);
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
            channel.read(readBuffer);
            this.readBuffer.flip();
            if(readBuffer.remaining()<this.objectLength) return null; //jeżeli bufor się jeszcze odpowiednio nie zapełnił
        }

        Object object = this.serialization.getObjectFromBuffer(this.readBuffer, this.objectLength);
        this.objectLength=0;
        this.readBuffer.compact();

        System.out.println(object.toString()); //temp
        return object;
    }
}
