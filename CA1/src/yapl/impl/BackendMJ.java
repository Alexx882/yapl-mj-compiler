package yapl.impl;

import yapl.interfaces.BackendBinSM;
import yapl.interfaces.MemoryRegion;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Implementation of CA1
 * @author Herold, Jakobitsch, Lercher
 */
public class BackendMJ implements BackendBinSM {
    @Override
    public int wordSize() {
        return 0;
    }

    @Override
    public int boolValue(boolean value) {
        return 0;
    }

    @Override
    public void assignLabel(String label) {

    }

    @Override
    public void writeObjectFile(OutputStream outStream) throws IOException {

    }

    @Override
    public int allocStaticData(int words) {
        return 0;
    }

    @Override
    public int allocStringConstant(String string) {
        return 0;
    }

    @Override
    public int allocStack(int words) {
        return 0;
    }

    @Override
    public void allocHeap(int words) {

    }

    @Override
    public void storeArrayDim(int dim) {

    }

    @Override
    public void allocArray() {

    }

    @Override
    public void loadConst(int value) {

    }

    @Override
    public void loadWord(MemoryRegion region, int offset) {

    }

    @Override
    public void storeWord(MemoryRegion region, int offset) {

    }

    @Override
    public void loadArrayElement() {

    }

    @Override
    public void storeArrayElement() {

    }

    @Override
    public void arrayLength() {

    }

    @Override
    public void writeInteger() {

    }

    @Override
    public void writeString(int addr) {

    }

    @Override
    public void neg() {

    }

    @Override
    public void add() {

    }

    @Override
    public void sub() {

    }

    @Override
    public void mul() {

    }

    @Override
    public void div() {

    }

    @Override
    public void mod() {

    }

    @Override
    public void and() {

    }

    @Override
    public void or() {

    }

    @Override
    public void isEqual() {

    }

    @Override
    public void isLess() {

    }

    @Override
    public void isLessOrEqual() {

    }

    @Override
    public void isGreater() {

    }

    @Override
    public void isGreaterOrEqual() {

    }

    @Override
    public void branchIf(boolean value, String label) {

    }

    @Override
    public void jump(String label) {

    }

    @Override
    public void callProc(String label) {

    }

    @Override
    public void enterProc(String label, int nParams, boolean main) {

    }

    @Override
    public void exitProc(String label) {

    }

    @Override
    public int paramOffset(int index) {
        return 0;
    }
}
