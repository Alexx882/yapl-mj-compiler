package yapl.test.backend.sm;

import java.io.FileOutputStream;
import java.io.IOException;

import yapl.impl.BackendMJ;
import yapl.interfaces.BackendBinSM;
import yapl.interfaces.MemoryRegion;

/**
 * BackendMJ test: printing a boolean value, labels, branches.
 * @author Mario Taschwer
 * @version $Id$
 */
public class Test3
{
    /**
     * Usage: java yapl.test.backend.sm.Test3 object_file
     */
    public static void main(String[] args) throws IOException
    {
        BackendBinSM backend = new BackendMJ();
        // string constants (static data)
        int addrTrue = backend.allocStringConstant("True");
        int addrFalse = backend.allocStringConstant("False");
        int addrNewline = backend.allocStringConstant("\n");
        
        // procedure writebool(boolean b): print boolean value
        backend.enterProc("writebool", 1, false);
        backend.loadWord(MemoryRegion.STACK, backend.paramOffset(0));  // load parameter 0 from stack frame
        backend.branchIf(false, "L1");                    // if (b == false) goto L1
        // print "True"
        backend.writeString(addrTrue);
        backend.jump("writebool_end");                    // jump to epilog
        // print "False"
        backend.assignLabel("L1");
        backend.writeString(addrFalse);
        backend.exitProc("writebool_end");                // procedure epilog
        
        // main program
        backend.enterProc("main", 0, true);
        // call writebool(true)
        backend.loadConst(backend.boolValue(true));
        backend.callProc("writebool");
        backend.writeString(addrNewline);
        // call writebool(false)
        backend.loadConst(backend.boolValue(false));
        backend.callProc("writebool");
        backend.exitProc("main_end");
        
        backend.writeObjectFile(new FileOutputStream(args[0]));
        System.out.println("wrote object file to " + args[0]);
    }
}
