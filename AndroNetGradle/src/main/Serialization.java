package main;

import com.esotericsoftware.jsonbeans.Json;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

/**
 * Created by Lukasz on 2014-11-24.
 */
public class Serialization {
    private final int ObjectLengthLength=4;

    private final Json json;
    //private final Kryo kryo;

    public Serialization()
    {
        this.json=new Json();
        //this.kryo=new Kryo();
    }

    public String getJsonFromObject(Object object)
    {
        return this.json.toJson(object, Object.class);
        //Output output=new Output();
        //kryo.writeObject(output, object);
        //return output.toString();
    }

    public Object getObjectFromBuffer(ByteBuffer byteBuffer, int length)
    {
        byte[] bufferBytes=new byte[length];
        byteBuffer.get(bufferBytes, 0, length);

        //Input input=new Input(bufferBytes);
        //return kryo.readObject(input, Object.class);

        return this.json.fromJson(Object.class, new String(bufferBytes));

    }

    public Integer getObjectLength(ByteBuffer buffer)
    {
        return buffer.getInt();
    }

    public Integer getObjectLengthLength()
    {
        return this.ObjectLengthLength;
    }
}
