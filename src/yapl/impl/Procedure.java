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
    private final int backPatchLocationForFrameSize;

    /**
     * stores the sizes and order of all local variables allocated with
     * allocStack
     */
    private final List<Integer> localVariableSizes = new LinkedList<>();

    public Procedure(String name, int nParams, int backPatchLocationForFrameSize) {
        this.name = name;
        this.nParams = nParams;
        this.backPatchLocationForFrameSize = backPatchLocationForFrameSize;
    }

    /**
     * @return frameSize = nParams + nLocalVariables (in words)
     */
    public byte calculateFrameSize() {
        // todo check if *wordsize is needed here
        return (byte) (nParams *4+ localVariableSizes.stream().mapToInt(e -> e).sum()*4);
    }

    /**
     * @param nWords - number of words to be allocated on the stack
     * @return adress of the beginning of the newly allocated memory section
     * (like malloc)
     */
    public int allocStackVariable(int nWords) {
        // todo check if *wordsize is needed here
        int address = localVariableSizes.size()*4 + nParams*4;

        localVariableSizes.add(nWords);

        return address;
    }

    public String getName() {
        return name;
    }

    public int getnParams() {
        return nParams;
    }

    public int getBackPatchLocation() {
        return backPatchLocationForFrameSize;
    }
}
