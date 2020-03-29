package yapl.test;

import yapl.impl.ByteUtils;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

public class ByteUtilsTest {

    public static void main(String[] args) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(4);

        int test = 1025;

        byteBuffer.putInt(test);

        byte[] dataReference = byteBuffer.array();
        List<Byte> data = ByteUtils.numberAsBytes(test);

        System.out.println(Arrays.toString(dataReference));
        System.out.println(data);
    }
}
