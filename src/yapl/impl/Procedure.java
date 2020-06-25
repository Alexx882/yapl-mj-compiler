package yapl.impl;

import yapl.interfaces.Symbol;

import java.util.LinkedList;
import java.util.List;

public class Procedure {
    /**
     * label which gets jumped to later with call [name]
     */
    public final String name;

    public final int nParams;

    /**
     * location in the bytecode where the framesize is defined. will be
     * overwritten later when the procedure definition is finished and
     * the size can derived
     */
    public final int backPatchLocationForFrameSize;

    /**
     * stores the sizes and order of all local variables allocated with
     * allocStack
     */
    public final List<Integer> localVariableSizes = new LinkedList<>();

    public Procedure(String name, int nParams, int backPatchLocationForFrameSize) {
        this.name = name;
        this.nParams = nParams;
        this.backPatchLocationForFrameSize = backPatchLocationForFrameSize;
    }

    /**
     * @return frameSize = nParams + nLocalVariables (in words)
     */
    public byte calculateFrameSize() {
        return (byte) (nParams + localVariableSizes.stream().mapToInt(e -> e).sum());
    }

    /**
     * @param nWords - number of words to be allocated on the stack
     * @return adress of the beginning of the newly allocated memory section
     * (like malloc)
     */
    public int allocStackVariable(int nWords) {
        int address = localVariableSizes.size() + nParams;

        localVariableSizes.add(nWords);

        return address;
    }

    /**
     * Create a label for the procedure symbol.
     * <br />
     * Add hashcode to avoid errors with duplicate names (see /testfiles/symbolcheck/test13.yapl).
     * Use spaces as delimiter as they are not allowed in YAPL identifiers.
     * @param end
     */
    public static String getLabel(Symbol proc, boolean end){
        return proc.getName() + " " + proc.hashCode()
                // add ' end' to avoid overwriting the start label
                + (end ? "end" : "");
    }
}
