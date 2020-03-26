package yapl.test.backend.sm;

import java.io.FileOutputStream;
import java.io.IOException;

import yapl.impl.BackendMJ;
import yapl.interfaces.BackendBinSM;

/**
 * BackendMJ test: printing a string constant.
 * @author Mario Taschwer
 * @version $Id$
 */
public class Test1
{
    /**
     * Usage: java yapl.test.backend.sm.Test1 object_file
     */
    public static void main(String[] args) throws IOException
    {
        BackendBinSM backend = new BackendMJ();
        backend.enterProc("main", 0, true);
        int addr = backend.allocStringConstant("Hello world!");
        backend.writeString(addr);
        backend.exitProc("main_end");
        backend.writeObjectFile(new FileOutputStream(args[0]));
        System.out.println("wrote object file to " + args[0]);
    }
}
