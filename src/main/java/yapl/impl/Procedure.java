package yapl.impl;

import java.util.LinkedList;
import java.util.List;

public class Procedure {
    private final String name;
    private final int nParams;
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

    public byte frameSize() {
        return (byte) (nParams + localVariableSizes.stream().mapToInt(e -> e).sum());
    }

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
