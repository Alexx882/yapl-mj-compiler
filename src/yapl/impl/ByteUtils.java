package yapl.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ByteUtils {

    private ByteUtils() {
    }

    /**
     * mask to mask a single byte
     */
    private static final byte mask = (byte) 0b11111111;

    /**
     * @param i - input integer
     * @return list comprising of 4 bytes
     */
    public static List<Byte> numberAsBytes(int i) {
        byte b1 = (byte) ((i >> 24) & mask);
        byte b2 = (byte) ((i >> 16) & mask);
        byte b3 = (byte) ((i >> 8) & mask);
        byte b4 = (byte) (i & mask);

        return Arrays.asList(b1, b2, b3, b4);
    }

    /**
     * @param i - input short
     * @return list comprising of 2 bytes
     */
    public static List<Byte> numberAsBytes(short i) {
        byte b1 = (byte) ((i >> 8) & mask);
        byte b2 = (byte) (i & mask);

        return Arrays.asList(b1, b2);
    }

    /**
     * @param i - input byte
     * @return list comprising of 1 bytes
     */
    public static List<Byte> numberAsBytes(byte i) {
        return Collections.singletonList(i);
    }
}
