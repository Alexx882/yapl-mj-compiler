package yapl.impl;

import yapl.compiler.Token;
import yapl.interfaces.Attrib;
import yapl.interfaces.BackendBinSM;
import yapl.interfaces.CodeGen;
import yapl.interfaces.Symbol;
import yapl.lib.*;

/**
 * Implementation of CodeGen interface generating binary code for stack machines.
 */
public class CodeGenBinSM implements CodeGen {
    private BackendBinSM backend;

    public CodeGenBinSM(BackendBinSM backend) {
        this.backend = backend;
    }

    @Override
    public String newLabel() {
        return null;
    }

    @Override
    public void assignLabel(String label) {

    }

    @Override
    public byte loadValue(Attrib attr) throws YaplException {
        return 0;
    }

    @Override
    public byte loadAddress(Attrib attr) throws YaplException {
        return 0;
    }

    @Override
    public void freeReg(Attrib attr) {

    }

    @Override
    public void allocVariable(Symbol sym) throws YaplException {

    }

    @Override
    public void setFieldOffsets(RecordType record) {

    }

    @Override
    public void storeArrayDim(int dim, Attrib length) throws YaplException {

    }

    @Override
    public Attrib allocArray(ArrayType arrayType) throws YaplException {
        return null;
    }

    @Override
    public Attrib allocRecord(RecordType recordType) throws YaplException {
        return null;
    }

    @Override
    public void setParamOffset(Symbol sym, int pos) {

    }

    @Override
    public void arrayOffset(Attrib arr, Attrib index) throws YaplException {

    }

    @Override
    public void recordOffset(Attrib record, Symbol field) throws YaplException {

    }

    @Override
    public Attrib arrayLength(Attrib arr) throws YaplException {
        return null;
    }

    @Override
    public void assign(Attrib lvalue, Attrib expr) throws YaplException {

    }

    @Override
    public Attrib op1(Token op, Attrib x) throws YaplException {
        return null;
    }

    @Override
    public Attrib op2(Attrib x, Token op, Attrib y) throws YaplException {
        return null;
    }

    @Override
    public Attrib relOp(Attrib x, Token op, Attrib y) throws YaplException {
        return null;
    }

    @Override
    public Attrib equalOp(Attrib x, Token op, Attrib y) throws YaplException {
        return null;
    }

    @Override
    public void enterProc(Symbol proc) throws YaplException {
        boolean isMain = proc.getKind() == Symbol.Program;
        int nParams = 0;

        if (!isMain) {
            Type type = proc.getType();
            if (!type.isProcedure())
                throw new YaplException(ErrorType.Internal, -1, -1, "Procedure symbol type is not procedure.");

            ProcedureType procType = (ProcedureType) type;

            nParams = procType.getParams().size();
        }

        // add hashcode to avoid errors with duplicate names (see /testfiles/symbolcheck/test13.yapl)
        // use spaces as delimiter as they are not allowed in YAPL identifiers
        String label = proc.getName() + " " + proc.hashCode();

        backend.enterProc(label, nParams, isMain);
    }

    @Override
    public void exitProc(Symbol proc) throws YaplException {
        // add ' end' to avoid overwriting the start label
        backend.exitProc(proc.getName() + " " + proc.hashCode() + " end");
    }

    @Override
    public void returnFromProc(Symbol proc, Attrib returnVal) throws YaplException {

    }

    @Override
    public Attrib callProc(Symbol proc, Attrib[] args) throws YaplException {
        return null;
    }

    @Override
    public void writeString(String string) throws YaplException {

    }

    @Override
    public void branchIfFalse(Attrib condition, String label) throws YaplException {

    }

    @Override
    public void jump(String label) {

    }
}
