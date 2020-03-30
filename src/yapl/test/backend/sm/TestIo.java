package yapl.test.backend.sm;

import java.io.FileOutputStream;
import java.io.IOException;

import yapl.impl.BackendMJ;

/**
 * BackendMJ test: reading from and writing to stdin.
 */
public class TestIo {
    /**
     * Usage: java yapl.test.backend.sm.Test2 object_file
     */
    public static void main(String[] args) throws IOException {
        System.out.println(args[0]);

        // impl specific methods, so direct access
        BackendMJ backend = new BackendMJ();

//        int promptInt = backend.allocStringConstant("hey, pls enter a number");
//        int promptBool = backend.allocStringConstant("is this your number?");
//        int newline = backend.allocStringConstant("\n");

        backend.enterProc("main", 0, true);

//        backend.writeString(promptInt);
//        backend.writeString(newline);
//        backend.readInteger();
//
//        backend.writeInteger();
//        backend.writeString(newline);
//
//        backend.writeString(promptBool);
//        backend.writeString(newline);
//
//        backend.readByte();
//        backend.readByte();
//        backend.writeByte();
//        backend.writeInteger();
//        backend.writeString(newline);

        backend.loadConst(1);
        backend.writeInteger();

        backend.exitProc("main_end");

        backend.writeObjectFile(new FileOutputStream(args[0]));
        System.out.println("wrote object file to " + args[0]);
    }
}
