package yapl.impl;

import yapl.interfaces.BackendBinSM;
import yapl.interfaces.MemoryRegion;

import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

import static yapl.impl.Instruction.*;

/**
 * Implementation of CA1
 *
 * @author Herold, Jakobitsch, Lercher
 */
public class BackendMJ implements BackendBinSM {

    private static final byte ZERO = 0;

    private List<Byte> codeBuffer = new ArrayList<>();
    private List<Byte> staticDataBuffer = new LinkedList<>();

    private Map<String, Integer> codeAddressForLabels = new HashMap<>();
    private Map<String, List<Integer>> backpatchingAddressesForLabels = new HashMap<>();
    
    private Procedure mainProcedure;
    private Procedure currentlyDefinedProcedure;

    /**
     * @param number - input operand comprising of >= 1 bytes
     * @param type   - input type conducting the length of the output list
     * @return byte representation of number
     */
    private static List<Byte> numberAsBytes(int number, OperandType type) {
        List<Byte> bytes = null;
        switch (type) {
            case s32:
                bytes = ByteUtils.numberAsBytes(/*(int)*/ number);
                break;
            case s16:
                bytes = ByteUtils.numberAsBytes((short) number);
                break;
            case s8:
                bytes = ByteUtils.numberAsBytes((byte) number);
                break;
        }

        return bytes;
    }

    /**
     * @return the adress of the next byte in the codebuffer (not there yet)
     */
    private int getNextCodeBufferAdress() {
        return codeBuffer.size();
    }

    /**
     * adds the opcode of a instruction to the codebuffer
     *
     * @param instruction - holds opcode value
     */
    private void addInstructionToCodeBuffer(Instruction instruction) {
        codeBuffer.add(instruction.value);
    }

    /**
     * sets the address of a label
     *
     * @param label
     * @param address
     */
    private void addToBackpatchingMap(String label, Integer address) {
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
    private void addExplicitOperandToCodeBuffer(int operand, OperandType type) {
        codeBuffer.addAll(numberAsBytes(operand, type));
    }

    /**
     * adds nBytes null-bytes as placeholders to the code buffer (for later backpatching)
     *
     * @param nBytes - number of placeholder bytes needed
     * @return the position of the FIRST placeholder byte
     */
    private int addPlaceholderBytesToCodeBuffer(int nBytes) {
        int startOfArea = getNextCodeBufferAdress();

        for (int i = 0; i < nBytes; i++)
            codeBuffer.add(null);

        return startOfArea;
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
        codeAddressForLabels.put(label, getNextCodeBufferAdress());
    }

    /**
     * iterates over all defined labels and inserts their address to all location the label is referenced
     */
    private void backPatchAllLocations() {
        for (String label : codeAddressForLabels.keySet()) {

            // address the label points to
            Integer address = codeAddressForLabels.get(label);

            // references in the code
            List<Integer> references = backpatchingAddressesForLabels.get(label);

            if (references != null)
                for (Integer reference : references)
                    backpatch(reference, address.shortValue());
        }
    }

    @Override
    public void writeObjectFile(OutputStream outStream) throws IOException {
        LinkedList<Byte> header = new LinkedList<>();

        // magic bytes 'MJ'
        header.add((byte) 0x4D);
        header.add((byte) 0x4A);

        // codeSize: number of bytes in code area
        header.addAll(ByteUtils.numberAsBytes(codeBuffer.size()));
        // (static) dataSize: number of words (32 bits) in static data area
        header.addAll(ByteUtils.numberAsBytes(staticDataBuffer.size() / wordSize()));
        // startPC: main() or start of code area if there is no main

        Integer startPc = codeAddressForLabels.get(mainProcedure.getName());

        if (startPc == null)
            throw new IllegalStateException("No address for main procedure found!");

        header.addAll(ByteUtils.numberAsBytes(codeAddressForLabels.getOrDefault("main", startPc)));

        backPatchAllLocations();

        for (Byte b : header)
            outStream.write(b);

//        int idx = 0;
        for (Byte b : codeBuffer) {
//            if(b == null)
//                throw new RuntimeException("Error for element: " + idx + ":" + codeBuffer.size());
//            idx++;

            outStream.write(b);
        }

        for (Byte b : staticDataBuffer)
            outStream.write(b);
    }

    @Override
    public int allocStaticData(int words) {
        int sizeBeforeNewString = staticDataBuffer.size();

        for (int i = 0; i < words * wordSize(); ++i)
            staticDataBuffer.add(ZERO);

        return sizeBeforeNewString;
    }

    @Override
    public int allocStringConstant(String string) {
        int sizeBeforeNewString = staticDataBuffer.size() / 4;

        for (char c : string.toCharArray())
            staticDataBuffer.add((byte) c);
        staticDataBuffer.add((byte) '\0');

        int paddingSize = (4 - (staticDataBuffer.size() % 4)) % 4;
        for (int i = 0; i < paddingSize; i++)
            staticDataBuffer.add((byte) 0);

        return sizeBeforeNewString;
    }

    @Override
    public void enterProc(String label, int nParams, boolean main) {
        if (currentlyDefinedProcedure != null)
            throw new IllegalStateException("Sub-Procedures are not allowed.");

        // mark start of method with label provided
        assignLabel(label);

        // add opcode for 'enter' to the codebuffer
        this.addInstructionToCodeBuffer(enter);

        // add number of arguments (s8 value) to the codebuffer
        this.addExplicitOperandToCodeBuffer(nParams, OperandType.s8);

        int backPatchLocation = addPlaceholderBytesToCodeBuffer(1);

        currentlyDefinedProcedure = new Procedure(label, nParams, backPatchLocation);

        if (main) {
            if (mainProcedure != null)
                throw new IllegalStateException("There can only be one main procedure.");

            mainProcedure = currentlyDefinedProcedure;
        }
    }

    /**
     * called to allocate local variables inside procedures
     *
     * @param words number of words to allocate.
     * @return
     */
    @Override
    public int allocStack(int words) {
        if (currentlyDefinedProcedure == null)
            throw new IllegalStateException("Cannot allocate variables without a procedure.");

        return currentlyDefinedProcedure.allocStackVariable(words);
    }

    private void backpatch(int location, List<Byte> bytes) {
        for (int i = 0; i < bytes.size(); i++) {
            if (codeBuffer.get(location + i) != null)
                throw new IllegalStateException(String.format("Codebuffer has no placeholder at location %d", location + i));

            codeBuffer.set(location + i, bytes.get(i));
        }
    }

    private void backpatch(int location, byte value) {
        backpatch(location, ByteUtils.numberAsBytes(value));
    }

    private void backpatch(int location, short value) {
        backpatch(location, ByteUtils.numberAsBytes(value));
    }

    private void backpatch(int location, int value) {
        backpatch(location, ByteUtils.numberAsBytes(value));
    }

    @Override
    public void exitProc(String label) {
        // backpatch framesize (which we now know because all variables were declared and we know their size)
        backpatch(currentlyDefinedProcedure.getBackPatchLocation(), currentlyDefinedProcedure.calculateFrameSize());

        // mark teardown with provided label
        assignLabel(label);

        // write opcode for EXIT to codebuffer
        addInstructionToCodeBuffer(exit);
        addInstructionToCodeBuffer(return_);

        currentlyDefinedProcedure = null;
    }

    @Override
    public void allocHeap(int words) {
        addInstructionToCodeBuffer(new_);
        addExplicitOperandToCodeBuffer(words, OperandType.s16);
    }

    @Override
    public void storeArrayDim(int dim) {
        // todo
//        loadConst(dim);
    }

    @Override
    public void allocArray() {
        addInstructionToCodeBuffer(newarray);
        addExplicitOperandToCodeBuffer(1, OperandType.s8);
    }

    @Override
    public void loadConst(int value) {
        switch (value) {
            case 0:
                addInstructionToCodeBuffer(const0);
                break;
            case 1:
                addInstructionToCodeBuffer(const1);
                break;
            case 2:
                addInstructionToCodeBuffer(const2);
                break;
            case 3:
                addInstructionToCodeBuffer(const3);
                break;
            case 4:
                addInstructionToCodeBuffer(const4);
                break;
            case 5:
                addInstructionToCodeBuffer(const5);
                break;
            case -1:
                addInstructionToCodeBuffer(const_m1);
                break;
            default:
                addInstructionToCodeBuffer(const_);
                addExplicitOperandToCodeBuffer(value, OperandType.s32);
        }
    }

    @Override
    public void loadWord(MemoryRegion region, int offset) {
        switch (region) {
            case STACK:
                addInstructionToCodeBuffer(load);
                addExplicitOperandToCodeBuffer(offset, OperandType.s8);
                break;
            case STATIC:
                addInstructionToCodeBuffer(getstatic);
                addExplicitOperandToCodeBuffer(offset, OperandType.s16);
                break;
            case HEAP:
                addInstructionToCodeBuffer(getfield);
                addExplicitOperandToCodeBuffer(offset, OperandType.s16);
                break;
        }
    }

    @Override
    public void storeWord(MemoryRegion region, int offset) {
        switch (region) {
            case STACK:
                addInstructionToCodeBuffer(store);
                addExplicitOperandToCodeBuffer(offset, OperandType.s8);
                break;
            case STATIC:
                addInstructionToCodeBuffer(putstatic);
                addExplicitOperandToCodeBuffer(offset, OperandType.s16);
                break;
            case HEAP:
                // assumption: address is already pushed on the stack (at least its the case in his tests...)
                addInstructionToCodeBuffer(putfield);
                addExplicitOperandToCodeBuffer(offset, OperandType.s16);
                break;
        }
    }

    @Override
    public void loadArrayElement() {
        addInstructionToCodeBuffer(aload);
    }

    @Override
    public void storeArrayElement() {
        addInstructionToCodeBuffer(astore);
    }

    @Override
    public void arrayLength() {
        addInstructionToCodeBuffer(arraylength);
    }

    @Override
    public void writeInteger() {
        // print: print integer t1 to standard output stream, right-adjusted in a field of t0 blank characters
        // insert offset of print
        loadConst(0);
        addInstructionToCodeBuffer(print);
    }

    /**
     * Seems to print a single ASCII character.
     * <p>
     * Description as per spec: Print boolean t1 (single character) to standard output stream,
     * right-adjusted in a field of t0 blank characters
     */
    public void writeByte() {
        loadConst(0);
        addInstructionToCodeBuffer(bprint);
    }

    @Override
    public void writeString(int addr) {
        addInstructionToCodeBuffer(sprint);
        addExplicitOperandToCodeBuffer(addr, OperandType.s16);
    }

    /**
     * Read integer from standard input stream onto expression stack.
     * <p>
     * Note: consumes "[^-0-9]*(-?\d+)[\n]*" and converts the part in brackets into an integer.
     * Note: when entered over console, this will not consume the newline character(s).
     */
    public void readInteger() {
        addInstructionToCodeBuffer(read);
    }

    /**
     * Read a byte value (single character) from standard input stream.
     */
    public void readByte() {
        addInstructionToCodeBuffer(bread);
    }

    @Override
    public void neg() {
        addInstructionToCodeBuffer(neg);
    }

    @Override
    public void add() {
        addInstructionToCodeBuffer(add);
    }

    @Override
    public void sub() {
        addInstructionToCodeBuffer(sub);
    }

    @Override
    public void mul() {
        addInstructionToCodeBuffer(mul);
    }

    @Override
    public void div() {
        addInstructionToCodeBuffer(div);
    }

    @Override
    public void mod() {
        addInstructionToCodeBuffer(rem);
    }

    @Override
    public void and() {
        addInstructionToCodeBuffer(mul);
    }

    @Override
    public void or() {
        // a + b >= 1
        add();
        loadConst(1);
        isGreaterOrEqual();
    }

    /**
     * Emit code for logical NOT operation on expression stack.
     * Assumes a numerical representation of boolean values.
     */
    public void not() {
        // 1 - a
        addInstructionToCodeBuffer(neg);
        loadConst(1);
        add();
    }

    @Override
    public void isEqual() {
        compare(jeq);
    }

    @Override
    public void isLess() {
        compare(jlt);
    }

    @Override
    public void isLessOrEqual() {
        compare(jle);
    }

    @Override
    public void isGreater() {
        compare(jgt);
    }

    @Override
    public void isGreaterOrEqual() {
        compare(jge);
    }

    /**
     * Build comparison evaluation from conditional jump instruction.
     * <p>
     * b = pop(); a = pop()
     * push(a ~ b)
     *
     * @param operator the conditional jump opcode
     */
    void compare(Instruction operator) {
        int ifAddress = getNextCodeBufferAdress();

        // if condition is true, jump ifLabel
        addInstructionToCodeBuffer(operator);
        addExplicitOperandToCodeBuffer(ifAddress + 7, OperandType.s16);

        // push false
        addInstructionToCodeBuffer(const0);

        // jump endIfLabel
        jmp(ifAddress + 8);

        // ifLabel

        // push true
        addInstructionToCodeBuffer(const1);

        // endIfLabel
    }

    @Override
    public void branchIf(boolean value, String label) {
        loadConst(boolValue(value));
        addInstructionToCodeBuffer(jeq);
        addToBackpatchingMap(label, getNextCodeBufferAdress());
        addPlaceholderBytesToCodeBuffer(2);
    }

    @Override
    public void jump(String label) {
        int backPatchingTarget = jmp(null);
        addToBackpatchingMap(label, backPatchingTarget);
    }

    /**
     * Unconditional jump to absolute address.
     * <p>
     * If address is 0, the operand will be filled with placeholders.
     *
     * @param address the jump target
     * @return address of the FIRST byte of the jump target
     */
    public int jmp(Integer address) {
        addInstructionToCodeBuffer(jmp);
        int backPatchingTarget = getNextCodeBufferAdress();

        if (address == null) {
            addPlaceholderBytesToCodeBuffer(2);
        } else {
            addExplicitOperandToCodeBuffer(address, OperandType.s16);
        }

        return backPatchingTarget;
    }

    @Override
    public void callProc(String label) {
        // FIXME parameters from caller should be available in Stack of callee
        addInstructionToCodeBuffer(call);
        addToBackpatchingMap(label, addPlaceholderBytesToCodeBuffer(2));
    }

    @Override
    public int paramOffset(int index) {
        // todo cleanup
        int res= wordSize() * index;
        System.out.println(res);
        return index;
    }
}
