package main;

import com.esotericsoftware.jsonbeans.Json;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;

/**
 * Created by Lukasz on 2014-11-24.
 */
class Serialization {
    private final int objectLengthLength=4;

    private final Json json;

    public Serialization()
    {
        this.json=new Json();
    }

    public String getJsonFromObject(Object object)
    {
        return this.json.toJson(object, Object.class);
    }

    public Object getObjectFromBuffer(ByteBuffer byteBuffer, int length)
    {
        byte[] bufferBytes=new byte[length];
        byteBuffer.get(bufferBytes, 0, length);

        return this.json.fromJson(Object.class, new String(bufferBytes));
    }

    public Integer getObjectLength(ByteBuffer byteBuffer)
    {
        int startPos=byteBuffer.position();
        int limit = byteBuffer.limit();
        byteBuffer.limit(startPos+objectLengthLength);
        int number = byteBuffer.getInt();
        byteBuffer.limit(limit);

        return number;
    }

    public Integer getObjectLengthLength()
    {
        return this.objectLengthLength;
    }
}
