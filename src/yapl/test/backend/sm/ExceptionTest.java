package yapl.test.backend.sm;

import yapl.impl.BackendMJ;

import java.io.FileOutputStream;
import java.io.IOException;

public class ExceptionTest {

    public static void main(String[] args) throws IOException {
        BackendMJ backend = new BackendMJ();

        // test if exception for root-level alloc is thrown
        try {
            backend.allocStack(1);
        } catch (IllegalStateException e) {
            System.out.println("nice.");
        }

        backend.enterProc("main", 0, true);

        // test if exception for nested methods is thrown
        try {
            backend.enterProc("main", 0, false);
        } catch (IllegalStateException e) {
            System.out.println("nice.");
        }

        int addr = backend.allocStringConstant("Hello world!");
        backend.writeString(addr);
        backend.loadConst(4);
        backend.neg();
        backend.writeInteger();
        backend.loadConst(-1);
        backend.writeInteger();

        backend.loadConst(4);
        backend.loadConst(2);
        backend.div();

        backend.loadConst(2);
        backend.mod();

        backend.exitProc("main_end");

        // test if exception for second main-method is thrown
        try {
            backend.enterProc("main", 0, true);
        } catch (IllegalStateException e) {
            System.out.println("nice.");
        }

        backend.writeObjectFile(new FileOutputStream(args[0]));
        System.out.println("wrote object file to " + args[0]);
    }

}
