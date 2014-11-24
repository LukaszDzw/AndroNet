import com.esotericsoftware.jsonbeans.Json;

import java.nio.ByteBuffer;

/**
 * Created by Lukasz on 2014-11-24.
 */
public class Serialization {
    private final int ObjectLengthLength=4;

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

    public Integer getObjectLength(ByteBuffer buffer)
    {
        return buffer.getInt();
    }

    public Integer getObjectLengthLength()
    {
        return this.ObjectLengthLength;
    }
}
