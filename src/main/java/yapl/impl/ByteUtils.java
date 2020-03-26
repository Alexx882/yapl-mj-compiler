package yapl.impl;

import java.nio.ByteBuffer;

public class ByteUtils {

    private ByteUtils() {
    }

    // todo test
    public static byte[] intToBytes(int i) {
        ByteBuffer bb = ByteBuffer.allocate(4);
        bb.putInt(i);
        return bb.array();
    }

}
