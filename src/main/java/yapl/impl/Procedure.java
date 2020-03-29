package yapl.impl;

import java.util.LinkedList;
import java.util.List;

public class Procedure {
    /**
     * label which gets jumped to later with call [name]
     */
    private final String name;

    private final int nParams;

    /**
     * location in the bytecode where the framesize is defined. will be
     * overwritten later when the procedure definition is finished and
     * the size can derived
     */
    private final int backPatchLocation;

    /**
     * stores the sizes and order of all local variables allocated with
     * allocStack
     */
    private final List<Integer> localVariableSizes = new LinkedList<>();

    public Procedure(String name, int nParams, int backPatchLocation) {
        this.name = name;
        this.nParams = nParams;
        this.backPatchLocation = backPatchLocation;
    }

    /**
     * @return frameSize = nParams + nLocalVariables (in words)
     */
    public byte frameSize() {
        return (byte) (nParams + localVariableSizes.stream().mapToInt(e -> e).sum());
    }

    /**
     * @param nWords - number of words to be allocated on the stack
     * @return adress of the beginning of the newly allocated memory section
     * (like malloc)
     */
    public int allocStackVariable(int nWords) {
        int address = localVariableSizes.size();

        localVariableSizes.add(nWords);

        return address;
    }

    public String getName() {
        return name;
    }

    public int getnParams() {
        return nParams;
    }

    public int backPatchLocation() {
        return backPatchLocation;
    }
}
