package yapl.test;

import yapl.impl.ByteUtils;

import java.util.Arrays;

public class ByteUtilsTest {

    public static void main(String[] args) {
        short value = 1234;

        System.out.println(Arrays.toString(ByteUtils.intToBytes(value)));
    }
}
