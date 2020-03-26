package yapl.test.backend.sm;

import java.io.FileOutputStream;
import java.io.IOException;

import yapl.impl.BackendMJ;
import yapl.interfaces.BackendBinSM;
import yapl.interfaces.MemoryRegion;

/**
 * BackendMJ test: functions, variables, arithmetic.
 * @author Mario Taschwer
 * @version $Id$
 */
public class Test4
{
    /**
     * Usage: java yapl.test.backend.sm.Test4 object_file
     */
    public static void main(String[] args) throws IOException
    {
        BackendBinSM backend = new BackendMJ();
        int global = backend.allocStaticData(1);          // global variable
        int addrNewline = backend.allocStringConstant("\n");
        
        // procedure int func(int x): returns global - x*x
        backend.enterProc("func", 1, false);
        backend.loadWord(MemoryRegion.STATIC, global);                 // load global variable
        backend.loadWord(MemoryRegion.STACK, backend.paramOffset(0));  // load parameter 0 from stack frame
        backend.loadWord(MemoryRegion.STACK, backend.paramOffset(0));  // load parameter 0 from stack frame
        backend.mul();
        backend.sub();
        backend.exitProc("func_end");                     // return value on expression stack
        
        // main program
        backend.enterProc("main", 0, true);
        int local = backend.allocStack(1);                // local variable (declared in a block)
        backend.loadConst(17);
        backend.storeWord(MemoryRegion.STATIC, global);   // global = 17
        backend.loadConst(3);
        backend.callProc("func");                         // func(3)
        backend.storeWord(MemoryRegion.STACK, local);     // store result
        backend.loadWord(MemoryRegion.STACK, local);
        backend.writeInteger();                           // print result: 8
        backend.writeString(addrNewline);
        backend.loadWord(MemoryRegion.STACK, local);
        backend.callProc("func");                         // func(8)
        backend.writeInteger();                           // print result: -47
        backend.writeString(addrNewline);
        backend.exitProc("main_end");
        
        backend.writeObjectFile(new FileOutputStream(args[0]));
        System.out.println("wrote object file to " + args[0]);
    }
}
