package yapl.interfaces;

public interface ExtendedBackendBinSM extends BackendBinSM {

    /**
     * Emit code for comparing the two top-most operands on the expression stack for inequality.
     * The boolean result value is pushed onto the expression stack.
     */
    void isNotEqual();

    /**
     * Emit code for logical NOT operation on expression stack.
     * Assumes a numerical representation of boolean values.
     */
    void not();

}
