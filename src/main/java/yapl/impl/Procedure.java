package yapl.impl;

public class Procedure {
    private final String name;
    private final int nParams;
    private final int backPatchLocation;

    public Procedure(String name, int nParams, int backPatchLocation) {
        this.name = name;
        this.nParams = nParams;
        this.backPatchLocation = backPatchLocation;
    }

    public String getName() {
        return name;
    }

    public int getnParams() {
        return nParams;
    }

    public int getBackPatchLocation() {
        return backPatchLocation;
    }
}
