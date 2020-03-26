package yapl.test.backend.sm;

import java.io.FileOutputStream;
import java.io.IOException;

import yapl.impl.BackendMJ;
import yapl.interfaces.BackendBinSM;
import yapl.interfaces.MemoryRegion;

/**
 * BackendMJ test: reference parameters, 1-dimensional arrays.
 * @author Mario Taschwer
 * @version $Id$
 */
public class Test5
{
    /**
     * Usage: java yapl.test.backend.sm.Test5 object_file
     */
    public static void main(String[] args) throws IOException
    {
        BackendBinSM backend = new BackendMJ();
        // procedure swap(int[] a, int i, int j): swap contents of a[i] and a[j] 
        backend.enterProc("swap", 3, false);
        int addrTmp = backend.allocStack(1);                           // local variable tmp
        backend.loadWord(MemoryRegion.STACK, backend.paramOffset(0));  // load start address of a
        backend.loadWord(MemoryRegion.STACK, backend.paramOffset(1));  // load i
        backend.loadArrayElement();                                    // load a[i]
        backend.storeWord(MemoryRegion.STACK, addrTmp);                // tmp = a[i]
        backend.loadWord(MemoryRegion.STACK, backend.paramOffset(0));  // load start address of a
        backend.loadWord(MemoryRegion.STACK, backend.paramOffset(1));  // load i
        backend.loadWord(MemoryRegion.STACK, backend.paramOffset(0));  // load start address of a
        backend.loadWord(MemoryRegion.STACK, backend.paramOffset(2));  // load j
        backend.loadArrayElement();                                    // load a[j]
        backend.storeArrayElement();                                   // a[i] = a[j]
        backend.loadWord(MemoryRegion.STACK, backend.paramOffset(0));  // load start address of a
        backend.loadWord(MemoryRegion.STACK, backend.paramOffset(2));  // load j
        backend.loadWord(MemoryRegion.STACK, addrTmp);                 // load tmp
        backend.storeArrayElement();                                   // a[j] = tmp
        backend.exitProc("swap_end");

        int addrA = backend.allocStaticData(1);           // allocate global variable a
        int addrNewline = backend.allocStringConstant("\n");
        
        // main program
        backend.enterProc("main", 0, true);
        // allocate 1-dimensional array of length 3
        backend.loadConst(3);
        backend.storeArrayDim(0);
        backend.allocArray();
        backend.storeWord(MemoryRegion.STATIC, addrA);    // store array start address in a
        // a[0] = 1
        backend.loadWord(MemoryRegion.STATIC, addrA);     // load start address of a
        backend.loadConst(0);                             // load index
        backend.loadConst(1);                             // load element value
        backend.storeArrayElement();
        // a[1] = 2
        backend.loadWord(MemoryRegion.STATIC, addrA);     // load start address of a
        backend.loadConst(1);                             // load index
        backend.loadConst(2);                             // load element value
        backend.storeArrayElement();
        // a[2] = 3
        backend.loadWord(MemoryRegion.STATIC, addrA);     // load start address of a
        backend.loadConst(2);                             // load index
        backend.loadConst(3);                             // load element value
        backend.storeArrayElement();
        // call swap(a, 1, 2)
        backend.loadWord(MemoryRegion.STATIC, addrA);     // load start address of a
        backend.loadConst(1);
        backend.loadConst(2);
        backend.callProc("swap");
        // print a[0]
        backend.loadWord(MemoryRegion.STATIC, addrA);     // load start address of a
        backend.loadConst(0);                             // load index
        backend.loadArrayElement();
        backend.writeInteger();
        backend.writeString(addrNewline);
        // print a[1]
        backend.loadWord(MemoryRegion.STATIC, addrA);     // load start address of a
        backend.loadConst(1);                             // load index
        backend.loadArrayElement();
        backend.writeInteger();
        backend.writeString(addrNewline);
        // print a[2]
        backend.loadWord(MemoryRegion.STATIC, addrA);     // load start address of a
        backend.loadConst(2);                             // load index
        backend.loadArrayElement();
        backend.writeInteger();
        backend.writeString(addrNewline);
        backend.exitProc("main_end");

        backend.writeObjectFile(new FileOutputStream(args[0]));
        System.out.println("wrote object file to " + args[0]);
    }
}
