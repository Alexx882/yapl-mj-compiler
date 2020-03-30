package yapl.test.backend.sm;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.function.Consumer;

import yapl.impl.BackendMJ;
import yapl.interfaces.BackendBinSM;

/**
 * BackendMJ test: reading from and writing to stdin.
 */
public class TestComparisons {
    /**
     * Usage: java yapl.test.backend.sm.Test2 object_file
     */
    public static void main(String[] args) throws IOException {
        System.out.println(args[0]);

        // impl specific methods, so direct access
        BackendMJ backend = new BackendMJ();

        int newline = backend.allocStringConstant("\n");

        backend.enterProc("main", 0, true);

        testCase(backend, newline, BackendBinSM::isEqual);
        testCase(backend, newline, BackendBinSM::isGreater);
        testCase(backend, newline, BackendBinSM::isGreaterOrEqual);
        testCase(backend, newline, BackendBinSM::isLess);
        testCase(backend, newline, BackendBinSM::isLessOrEqual);

        backend.exitProc("main_end");

        backend.writeObjectFile(new FileOutputStream(args[0]));
        System.out.println("wrote object file to " + args[0]);
    }

    static void testCase(BackendBinSM backend, int newlineAddress, Consumer<BackendBinSM> comparison) {
        backend.loadConst(1);
        backend.loadConst(2);
        comparison.accept(backend);
        backend.writeInteger();
        backend.writeString(newlineAddress);

        backend.loadConst(1);
        backend.loadConst(1);
        comparison.accept(backend);
        backend.writeInteger();
        backend.writeString(newlineAddress);

        backend.loadConst(1);
        backend.loadConst(0);
        comparison.accept(backend);
        backend.writeInteger();
        backend.writeString(newlineAddress);
    }
}
