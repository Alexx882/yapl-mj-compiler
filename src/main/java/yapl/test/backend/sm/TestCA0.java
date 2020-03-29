package yapl.test.backend.sm;

import yapl.impl.BackendMJ;
import yapl.interfaces.BackendBinSM;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * BackendMJ test: printing a string constant.
 * @author Mario Taschwer
 * @version $Id$
 */
public class TestCA0
{
    /**
     * Usage: java yapl.test.backend.sm.Test1 object_file
     */
    public static void main(String[] args) throws IOException
    {
        BackendBinSM backend = new BackendMJ();

        backend.loadConst(16);
        backend.loadConst(0);

        backend.writeObjectFile(new FileOutputStream("_CA0.mj"));
        System.out.println("wrote object file to _CA0.mj");
    }
}
