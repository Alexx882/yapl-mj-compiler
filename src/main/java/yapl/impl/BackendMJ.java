package yapl.impl;

import yapl.interfaces.BackendBinSM;
import yapl.interfaces.MemoryRegion;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.*;

/**
 * Implementation of CA1
 *
 * @author Herold, Jakobitsch, Lercher
 */
public class BackendMJ implements BackendBinSM {

    enum Command {
        load(1),
        load0(2),
        load1(3),
        load2(4),
        load3(5),
        store(6),
        store0(7),
        store1(8),
        store2(9),
        store3(10),
        getstatic(11),
        putstatic(12),
        getfield(13),
        putfield(14),
        const0(15),
        const1(16),
        const2(17),
        const3(18),
        const4(19),
        const5(20),
        const_m1(21),
        const_(22),
        add(23),
        sub(24),
        mul(25),
        div(26),
        rem(27),
        neg(28),
        shl(29),
        shr(30),
        new_(31),
        newarray(32),
        aload(33),
        astore(34),
        baload(35),
        bastore(36),
        arraylength(37),
        pop(38),
        jmp(39),
        jeq(40),
        jne(41),
        jlt(42),
        jle(43),
        jgt(44),
        jge(45),
        call(46),
        return_(47),
        enter(48),
        exit(49),
        read(50),
        print(51),
        bread(52),
        bprint(53),
        trap(54),
        sprint(55),
        last(56);

        byte value;

        Command(int value) {
            this.value = (byte) value;
        }
    }

    private static final byte ZERO = 0;

    private List<Byte> codeBuffer = new LinkedList<>();
    private List<Byte> staticDataBuffer = new LinkedList<>();

    private Map<String, Integer> codeAddressForLabels = new HashMap<>();
    private Map<String, List<Integer>> backpatchingAddressesForLabels = new HashMap<>();

    @Override
    public int wordSize() {
        return 4;
    }

    @Override
    public int boolValue(boolean value) {
        return value ? 1 : 0;
    }

    @Override
    public void assignLabel(String label) {

    }

    @Override
    public void writeObjectFile(OutputStream outStream) throws IOException {

    }

    @Override
    public int allocStaticData(int words) {
        int sizeBeforeNewString = staticDataBuffer.size();

        for (int i = 0; i < words; ++i)
            staticDataBuffer.add(ZERO);

        return sizeBeforeNewString;
    }

    @Override
    public int allocStringConstant(String string) {
        int sizeBeforeNewString = staticDataBuffer.size();

        for (char c : string.toCharArray())
            staticDataBuffer.add((byte) c);

        staticDataBuffer.add((byte) '\0');

        return sizeBeforeNewString;
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
        codeBuffer.add(Command.const_.value);

        for (byte b : ByteUtils.intToBytes(value))
            codeBuffer.add(b);
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
        codeBuffer.add(Command.neg.value);
    }

    @Override
    public void add() {
        codeBuffer.add(Command.add.value);
    }

    @Override
    public void sub() {
        codeBuffer.add(Command.sub.value);
    }

    @Override
    public void mul() {
        codeBuffer.add(Command.mul.value);
    }

    @Override
    public void div() {
        codeBuffer.add(Command.div.value);
    }

    @Override
    public void mod() {
        codeBuffer.add(Command.rem.value);
    }

    @Override
    public void and() {
        codeBuffer.add(Command.mul.value);
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
