package yapl.test.backend.sm;

import yapl.impl.BackendMJ;

import java.io.FileOutputStream;
import java.io.IOException;

public class ExceptionTestNoMainMethod {

    public static void main(String[] args) throws IOException {
        BackendMJ backend = new BackendMJ();
        try {
            backend.writeObjectFile(new FileOutputStream(args[0]));
        } catch (IllegalStateException e) {
            System.out.println("nice.");
        }

        backend.enterProc("main", 0, true);
        backend.loadConst(1);
        backend.writeInteger();
        backend.exitProc("main_end");

        backend.writeObjectFile(new FileOutputStream(args[0]));

        System.out.println("wrote object file to " + args[0]);
    }

}
