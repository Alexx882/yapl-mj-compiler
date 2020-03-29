package yapl.impl;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class ByteUtils {

    private ByteUtils() {
    }

    public static List<Byte> primitiveArrayAsList(byte[] bytes) {
        ArrayList<Byte> boxedList = new ArrayList<>(bytes.length);
        for (byte b : bytes) boxedList.add(b);
        return boxedList;
    }

    public static byte[] numberAsBytesArray(int i) {
        ByteBuffer bb = ByteBuffer.allocate(4);
        bb.putInt(i);
        return bb.array();
    }

    public static byte[] numberAsBytesArray(short i) {
        ByteBuffer bb = ByteBuffer.allocate(2);
        bb.putShort(i);
        return bb.array();
    }

    public static byte[] numberAsBytesArray(byte i) {
        return new byte[]{i};
    }

    public static List<Byte> numberAsBytes(int number, OperandType type) {
        List<Byte> bytes = null;
        switch (type) {
            case s32:
                bytes = ByteUtils.numberAsBytes(/*(int)*/ number);
                break;
            case s16:
                bytes = ByteUtils.numberAsBytes((short) number);
                break;
            case s8:
                bytes = ByteUtils.numberAsBytes((byte) number);
                break;
        }

        return bytes;
    }

    public static List<Byte> numberAsBytes(int i) {
        return primitiveArrayAsList(numberAsBytesArray(i));
    }

    public static List<Byte> numberAsBytes(short i) {
        return primitiveArrayAsList(numberAsBytesArray(i));
    }

    public static List<Byte> numberAsBytes(byte i) {
        return primitiveArrayAsList(numberAsBytesArray(i));
    }

    enum OperandType {
        s8(1),
        s16(2),
        s32(4);

        int size;

        OperandType(int size) {
            this.size = size;
        }
    }
}
