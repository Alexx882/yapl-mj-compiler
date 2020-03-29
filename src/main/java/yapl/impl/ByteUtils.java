package yapl.impl;

import java.nio.ByteBuffer;

public class ByteUtils {

    private ByteUtils() {
    }


    public static byte[] numberAsBytes(int i) {
        ByteBuffer bb = ByteBuffer.allocate(4);
        bb.putInt(i);
        return bb.array();
    }

    public static byte[] numberAsBytes(short i) {
        ByteBuffer bb = ByteBuffer.allocate(2);
        bb.putInt(i);
        return bb.array();
    }

    // todo test
    public static byte[] intToBytes(int i) {
        ByteBuffer bb = ByteBuffer.allocate(4);
        bb.putInt(i);
        return bb.array();
    }

}
