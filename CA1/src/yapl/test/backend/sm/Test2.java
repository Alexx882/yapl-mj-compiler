package yapl.test.backend.sm;

import java.io.FileOutputStream;
import java.io.IOException;

import yapl.impl.BackendMJ;
import yapl.interfaces.BackendBinSM;
import yapl.interfaces.MemoryRegion;

/**
 * BackendMJ test: printing an integer, procedure call.
 * @author Mario Taschwer
 * @version $Id$
 */
public class Test2
{
    /**
     * Usage: java yapl.test.backend.sm.Test2 object_file
     */
    public static void main(String[] args) throws IOException
    {
        BackendBinSM backend = new BackendMJ();
        // procedure writeint(int i): print integer
        backend.enterProc("writeint", 1, false);
        backend.loadWord(MemoryRegion.STACK, backend.paramOffset(0));  // load parameter 0 from stack frame
        backend.writeInteger();
        backend.exitProc("writeint_end");
        // main program
        backend.enterProc("main", 0, true);
        // call writeint(7)
        backend.loadConst(7);
        backend.callProc("writeint");
        backend.exitProc("main_end");
        
        backend.writeObjectFile(new FileOutputStream(args[0]));
        System.out.println("wrote object file to " + args[0]);
    }
}
