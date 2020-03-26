package yapl.test.backend.sm;

import java.io.FileOutputStream;
import java.io.IOException;

import yapl.impl.BackendMJ;
import yapl.interfaces.BackendBinSM;
import yapl.interfaces.MemoryRegion;

/**
 * BackendMJ test: records.
 * @author Mario Taschwer
 * @version $Id$
 */

public class Test6 
{

	/**
	 * Usage: java yapl.test.backend.sm.Test6 object_file
	 */
	public static void main(String[] args) throws IOException
	{
        BackendBinSM backend = new BackendMJ();
        int addrNewline = backend.allocStringConstant("\n");

        // main program
        backend.enterProc("main", 0, true);
        // allocate local variable r for record reference
        int recordAddr = backend.allocStack(1);
        // allocate record 'List' for linked list (2 words):
        // field 'value' at offset 0: integer value
        // field 'next'  at offset 1: address of next list element
        // r = new List
        backend.allocHeap(2);
        backend.storeWord(MemoryRegion.STACK, recordAddr);
        // r.value = 5
        backend.loadWord(MemoryRegion.STACK, recordAddr);
        backend.loadConst(5);
        backend.storeWord(MemoryRegion.HEAP, 0);
        // r.next = new List
        backend.loadWord(MemoryRegion.STACK, recordAddr);
        backend.allocHeap(2);
        backend.storeWord(MemoryRegion.HEAP, 1);
        // r.next.value = 10
        backend.loadWord(MemoryRegion.STACK, recordAddr);
        backend.loadWord(MemoryRegion.HEAP, 1);
        backend.loadConst(10);
        backend.storeWord(MemoryRegion.HEAP, 0);
        // print r.value
        backend.loadWord(MemoryRegion.STACK, recordAddr);
        backend.loadWord(MemoryRegion.HEAP, 0);
        backend.writeInteger();
        backend.writeString(addrNewline);
        // print r.next.value
        backend.loadWord(MemoryRegion.STACK, recordAddr);
        backend.loadWord(MemoryRegion.HEAP, 1);
        backend.loadWord(MemoryRegion.HEAP, 0);
        backend.writeInteger();
        backend.writeString(addrNewline);
        backend.exitProc("main_end");

        backend.writeObjectFile(new FileOutputStream(args[0]));
        System.out.println("wrote object file to " + args[0]);
	}

}
