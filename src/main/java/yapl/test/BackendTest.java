package yapl.test;

import yapl.impl.BackendMJ;

import java.io.*;

public class BackendTest {
    public static void main(String[] args) throws IOException {
        File file = new File("test.mj");
        if (file.exists()) file.delete();
        file.createNewFile();

        BackendMJ backendMJ = new BackendMJ();

        backendMJ.enterProc("func", 0, false);
        backendMJ.loadConst(35);
        backendMJ.exitProc("func_end");

        backendMJ.enterProc("main", 0, true);
        backendMJ.loadConst(16);
        backendMJ.exitProc("main_end");

        backendMJ.writeObjectFile(new FileOutputStream(file));
    }
}
