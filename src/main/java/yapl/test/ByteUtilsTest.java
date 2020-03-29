package yapl.test;

import yapl.impl.ByteUtils;

import java.util.Arrays;

public class ByteUtilsTest {

    public static void main(String[] args) {
        byte b = (byte) 0xFF;
        short s = (short) 0x0102;
        int i = 0x01020304;


        System.out.println(ByteUtils.numberAsBytes(b).toString());
        System.out.println(ByteUtils.numberAsBytes(s).toString());
        System.out.println(ByteUtils.numberAsBytes(i).toString());
    }
}
