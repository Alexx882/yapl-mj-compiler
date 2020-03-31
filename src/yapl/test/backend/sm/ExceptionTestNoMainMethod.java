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

        System.out.println("wrote object file to " + args[0]);
    }

}
