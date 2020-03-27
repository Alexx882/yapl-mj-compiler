package yapl.impl;

import yapl.interfaces.BackendBinSM;
import yapl.interfaces.MemoryRegion;

import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

import static yapl.impl.BackendMJ.Instruction.*;
import static yapl.impl.BackendMJ.OperandType.*;

/**
 * Implementation of CA1
 *
 * @author Herold, Jakobitsch, Lercher
 */
public class BackendMJ implements BackendBinSM {

    private static final byte ZERO = 0;

    private List<Byte> codeBuffer = new LinkedList<>();
    private List<Byte> staticDataBuffer = new LinkedList<>();

    private Map<String, Integer> codeAddressForLabels = new HashMap<>();
    private Map<String, List<Integer>> backpatchingAddressesForLabels = new HashMap<>();

    int getNextCodeBufferAdress() {
        return codeBuffer.size();
    }

    void addInstructionToCodeBufferLol(Instruction instruction) {
        codeBuffer.add(instruction.value);
    }

    void addToBackpatchingMap(String label, Integer address) {
        List<Integer> list = backpatchingAddressesForLabels.getOrDefault(label, new LinkedList<>());
        list.add(address);
        backpatchingAddressesForLabels.put(label, list);
    }

    /**
     * Writes an explicit operand to the code buffer.
     * <p>
     * Depending on the operand type, crops the operand into format.
     * Adding 0xF0F1F2F3 will result in: 0xF0 0xF1 0xF2 0xF3 for s32,
     * 0xF2, 0xF3 for s16 and 0xF3 for s8.
     *
     * @param operand the explicit operand
     * @param type    the type/size of the operand
     */
    void addExplicitOperandToCodeBuffer(int operand, OperandType type) {
        byte[] bytes = ByteUtils.intToBytes(operand);
        switch (type) {
            case s32:
                codeBuffer.add(bytes[0]);
                codeBuffer.add(bytes[1]);
            case s16:
                codeBuffer.add(bytes[2]);
            case s8:
                codeBuffer.add(bytes[3]);
        }
    }

    void addExplicitOperandToCodeBuffer(int operand) {
        addExplicitOperandToCodeBuffer(operand, s32);
    }

    void addExplicitOperandToCodeBuffer(short operand) {
        addExplicitOperandToCodeBuffer(operand, s16);
    }

    void addExplicitOperandToCodeBuffer(byte operand) {
        addExplicitOperandToCodeBuffer(operand, s8);
    }

    /**
     * adds nBytes null-bytes as placeholders to the code buffer (for later backpatching)
     *
     * @param nBytes - number of placeholder bytes needed
     * @return the position of the FIRST placeholder byte
     */
    int addPlaceholderByteToCodeBuffer(int nBytes) {
        int startOfArea = getNextCodeBufferAdress();

        for (int i = 0; i < nBytes; i++)
            codeBuffer.add(null);

        return startOfArea;
    }

    enum OperandType {
        s8(1),
        s16(2),
        s32(4);

        int size;

        OperandType(int size) {
            this.size = size;
        }
    }

    enum Instruction {
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

        Instruction(int value) {
            this.value = (byte) value;
        }
    }

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
        int currentAdress = codeBuffer.size();
        codeAddressForLabels.put(label, currentAdress);
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

    private Procedure currentlyDefinedProcedure;

    @Override
    public void enterProc(String label, int nParams, boolean main) {
        if (currentlyDefinedProcedure != null)
            throw new IllegalStateException("Sub-Procedures are not allowed.");

        // mark start of method with label provided
        assignLabel(label);

        // add opcode for 'enter' to the codebuffer
        this.addInstructionToCodeBufferLol(enter);

        // add number of arguments (s8 value) to the codebuffer
        this.addExplicitOperandToCodeBuffer((byte) nParams);

        int backPatchLocation = addPlaceholderByteToCodeBuffer(1);

        currentlyDefinedProcedure = new Procedure(label, nParams, backPatchLocation);
    }

    /**
     * called to allocate local variables inside procedures
     *
     * @param words    number of words to allocate.
     *
     * @return
     */
    @Override
    public int allocStack(int words) {
        return 0;
    }

    @Override
    public void exitProc(String label) {

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
        addInstructionToCodeBufferLol(const_);
        addExplicitOperandToCodeBuffer(value);
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
        addInstructionToCodeBufferLol(neg);
    }

    @Override
    public void add() {
        addInstructionToCodeBufferLol(add);
    }

    @Override
    public void sub() {
        addInstructionToCodeBufferLol(sub);
    }

    @Override
    public void mul() {
        addInstructionToCodeBufferLol(mul);
    }

    @Override
    public void div() {
        addInstructionToCodeBufferLol(div);
    }

    @Override
    public void mod() {
        addInstructionToCodeBufferLol(rem);
    }

    @Override
    public void and() {
        addInstructionToCodeBufferLol(mul);
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
        addInstructionToCodeBufferLol(jmp);
        addToBackpatchingMap(label, getNextCodeBufferAdress());
        addExplicitOperandToCodeBuffer(0, s16);
    }

    @Override
    public void callProc(String label) {

    }

    @Override
    public int paramOffset(int index) {
        return 0;
    }
}
