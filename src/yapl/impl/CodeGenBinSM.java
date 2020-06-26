package yapl.impl;

import yapl.compiler.Token;
import yapl.interfaces.*;
import yapl.lib.*;

import java.util.HashMap;
import java.util.Map;

import static yapl.compiler.YaplConstants.*;
import static yapl.impl.ErrorType.*;
import static yapl.interfaces.MemoryRegion.*;

/**
 * Implementation of CodeGen interface generating binary code for stack machines.
 */
public class CodeGenBinSM implements CodeGen {
    private final ExtendedBackendBinSM backend;

    private int labelCounter = 0;

    private final Map<Attrib, Integer> globalConstantAssignments = new HashMap<>();

    private int newlineOffset = -1;

    public CodeGenBinSM(ExtendedBackendBinSM backend) {
        this.backend = backend;
    }

    @Override
    public String newLabel() {
        return "label " + labelCounter++;
    }

    @Override
    public void assignLabel(String label) {
        backend.assignLabel(label);
    }

    @Override
    public byte loadValue(Attrib attr) throws YaplException {
        switch (attr.getKind()) {
            case Attrib.Constant:
                if (!(attr instanceof YaplAttrib))
                    throw new IllegalStateException("Need value for constant load.");
                backend.loadConst(((YaplAttrib) attr).getValue());
                break;

            case Attrib.MemoryOperand:
                backend.loadWord(attr.isGlobal() ? STATIC : STACK, attr.getOffset());
                break;

            case Attrib.ArrayElement:
                backend.loadArrayElement();
                break;

            case Attrib.RecordField:
                backend.loadWord(HEAP, attr.getOffset());
                break;

            default:
                throw new IllegalStateException("Loading " + attr.getKind() + " not implemented.");
        }

        attr.setKind(Attrib.RegValue);
        return 0; // return register number not needed for stack machine
    }

    @Override
    public byte loadAddress(Attrib attr) throws YaplException {
        // as of now, this would have no difference in runtime effect to loadValue()
        return 0;
    }

    @Override
    public void freeReg(Attrib attr) {

    }

    @Override
    public void allocVariable(Symbol sym) throws YaplException {
        switch (sym.getKind()) {
            case Symbol.Variable:
            case Symbol.Constant:
                int offset;

                if (sym.isGlobal())
                    // global variables and constants are stored in STATIC
                    offset = backend.allocStaticData(1);
                else
                    // local variables and constants are stored on the STACK
                    offset = backend.allocStack(1);

                sym.setOffset(offset);
                break;
        }
    }

    @Override
    public void setFieldOffsets(RecordType record) {
        for (int i = 0; i < record.nFields(); i++)
            record.getField(i).setOffset(i);
    }

    @Override
    public void storeArrayDim(int dim, Attrib length) throws YaplException {

    }

    /**
     * This implementation assumes that the array lengths (nr == arrayTime.dim) is already on the stack.
     *
     * @param arrayType array type.
     * @return
     * @throws YaplException
     */
    @Override
    public Attrib allocArray(ArrayType arrayType) throws YaplException {
        if (arrayType.getDim() != 1)
            throw new YaplException(Internal, -1, -1, "Multidim array allocation not implemented yet");
        backend.storeArrayDim(arrayType.getDim() - 1);

        // requires the array length to be on top of the stack
        backend.allocArray();

        // address is located on exp stack
        return new YaplAttrib(Attrib.RegAddress, arrayType);
    }

    @Override
    public Attrib allocRecord(RecordType recordType) throws YaplException {
        backend.allocHeap(recordType.nFields());
        return new YaplAttrib(Attrib.RegAddress, recordType);
    }

    @Override
    public void setParamOffset(Symbol sym, int pos) {
        // handled differently
    }

    @Override
    public void arrayOffset(Attrib arr, Attrib index) throws YaplException {
        // no code since the array load and store ops consume address AND index
        arr.setKind(Attrib.ArrayElement);
    }

    @Override
    public void recordOffset(Attrib record, Symbol field) throws YaplException {
        if (!record.getType().isRecord())
            throw new YaplException(Internal, -1, -1, "Cannot calculate record offset for non-record type");

        record.setKind(Attrib.RecordField);
        record.setType(field.getType());
        record.setOffset(field.getOffset());
    }

    @Override
    public Attrib arrayLength(Attrib arr) throws YaplException {
        if (arr.getKind() != Attrib.RegAddress)
            loadValue(arr);

        backend.arrayLength();
        return new YaplAttrib(Attrib.RegValue, Type.INT);
    }

    @Override
    public void assign(Attrib lvalue, Attrib expr) throws YaplException {
        if (!lvalue.getType().equals(expr.getType()))
            throw new IllegalStateException("Assignment type mismatch");

        // special treatment for global constants, since they need code to assign values,
        // but code is only executed after the startPC
        // solution: move value assignment to start of main procedure
        // also, there can be no global constants after the startPC
        if (lvalue.isGlobal() && lvalue.isConstant()) {
            globalConstantAssignments.put(lvalue, ((YaplAttrib) expr).getValue());
            return;
        }

        switch (lvalue.getKind()) {
            case Attrib.MemoryOperand:
                backend.storeWord(lvalue.isGlobal() ? STATIC : STACK, lvalue.getOffset());
                break;

            case Attrib.ArrayElement:
                backend.storeArrayElement();
                break;

            case Attrib.RecordField:
                backend.storeWord(HEAP, lvalue.getOffset());
                break;

            // TODO assignments for array elements, record fields

            default:
                throw new YaplException(Internal, -1, -1, "Assignment for kind " + lvalue.getKind() + " implemented yet");
        }
    }

    @Override
    public Attrib op1(Token op, Attrib x) throws YaplException {
        if (op == null)
            return x;

        if (!x.getType().isInt())
            throw new YaplException(IllegalOp1Type, op, op);

        switch (op.kind) {
            case ADD:
                return x;

            case SUB:
                backend.neg();
                break;

            default:
                throw new YaplException(Internal, -1, -1, "Illegal Op1 operation.");
        }

        return x;
    }

    @Override
    public Attrib op2(Attrib x, Token op, Attrib y) throws YaplException {
        if (!x.getType().equals(y.getType()))
            throw new YaplException(IllegalOp2Type, op, op);

        boolean intOp = true;

        switch (op.kind) {
            case ADD:
                backend.add();
                break;

            case SUB:
                backend.sub();
                break;

            case MUL:
                backend.mul();
                break;

            case DIV:
                backend.div();
                break;

            case MOD:
                backend.mod();
                break;

            case AND:
                backend.and();
                intOp = false;
                break;

            case OR:
                backend.or();
                intOp = false;
                break;

            default:
                throw new YaplException(Internal, -1, -1, "Illegal Op2 operation.");
        }

        if (intOp && !x.getType().isInt() || !intOp && !x.getType().isBool())
            throw new YaplException(IllegalOp2Type, op, op);

        return x;
    }

    @Override
    public Attrib relOp(Attrib x, Token op, Attrib y) throws YaplException {
        if (!(x.getType().isInt() && y.getType().isInt()))
            throw new YaplException(IllegalRelOpType, op, op);

        switch (op.kind) {
            case LT:
                backend.isLess();
                break;

            case LE:
                backend.isLessOrEqual();
                break;

            case GE:
                backend.isGreaterOrEqual();
                break;

            case GT:
                backend.isGreater();
                break;

            default:
                throw new YaplException(Internal, -1, -1, "Illegal RelOp operation.");
        }

        x.setType(Type.BOOL);
        return x;
    }

    @Override
    public Attrib equalOp(Attrib x, Token op, Attrib y) throws YaplException {
        Type xType = x.getType(), yType = y.getType();
        if (!(xType.isInt() && yType.isInt()
                || xType.isBool() && yType.isBool()))
            throw new YaplException(IllegalEqualOpType, op, op);

        switch (op.kind) {
            case EQ:
                backend.isEqual();
                break;

            case NE:
                backend.isNotEqual();
                break;

            default:
                throw new YaplException(Internal, -1, -1, "Illegal EqualOp operation.");
        }

        x.setType(Type.BOOL);
        return x;
    }

    @Override
    public void enterProc(Symbol proc) throws YaplException {
        boolean isMain = proc == null || proc.getKind() == Symbol.Program;
        int nParams = 0;
        String label;

        if (!isMain) {
            // load nParams
            Type type = proc.getType();
            if (!type.isProcedure())
                throw new YaplException(Internal, -1, -1, "Procedure symbol type is not procedure.");

            nParams = ((ProcedureType) type).getParams().size();
            label = Procedure.getLabel(proc, false);

        } else {
            label = newLabel();
        }

        backend.enterProc(label, nParams, isMain);

        // insert global constant assignments here
        if (isMain) {
            for (var decl : globalConstantAssignments.entrySet()) {
                // globalConstantAssignments maps: <Attrib representing a const var> to <integer value of the const var>
                backend.loadConst(decl.getValue());
                backend.storeWord(STATIC, decl.getKey().getOffset());
            }
        }
    }

    @Override
    public void exitProc(Symbol proc) throws YaplException {
        backend.exitProc(Procedure.getLabel(proc, true));
    }

    @Override
    public void returnFromProc(Symbol proc, Attrib returnVal) throws YaplException {
        // if this is the main procedure, just exit
        if (proc == null)
            backend.exitProc(newLabel());

        else
            // return value will be pushed on stack immediately after encountered in grammar
            backend.jump(Procedure.getLabel(proc, true));
    }

    @Override
    public Attrib callProc(Symbol proc, Attrib[] args) throws YaplException {
//        System.out.println(proc.getName() + ": " + args.length);

        for (PredefinedFunction predefFunc : PredefinedFunction.values())
            if (predefFunc.procedureType == proc.getType())
                return callPredefinedFunction(predefFunc, args);

        /*
         * Info: currently all arguments for a procedure call are calculated immediately.
         * eg. f(1+1, a or b) will immediately: const1, const1, add, <a or b ie const1/0),
         * so the values are already on the stack.
         * This is probably fine, but must not be forgotten.
         */

        backend.callProc(Procedure.getLabel(proc, false));

        return new YaplAttrib(((ProcedureType) proc.getType()).getReturnType());
    }

    public Attrib callPredefinedFunction(PredefinedFunction f, Attrib[] args) throws YaplException {
        switch (f) {
            case writeint:
                backend.writeInteger();
                break;

            case writebool:
                String elseLabel = newLabel(), endIfLabel = newLabel();

                // if
                branchIfFalse(args[0], elseLabel);
                // then
                writeStringNoQuotes("True");
                jump(endIfLabel);
                // else
                assignLabel(elseLabel);
                writeStringNoQuotes("False");
                // endif
                assignLabel(endIfLabel);
                break;

            case writeln:
                writeStringNoQuotes(System.lineSeparator());
                break;

            case readint:
                // option 1: mock behavior for testing purposes
                backend.loadConst(0);

                // option 2: real implementation
//                ((BackendMJ) backend).readInteger();
                break;

            default:
                throw new IllegalStateException("Predefined function not implemented.");
        }

        return new YaplAttrib(f.procedureType.getReturnType());
    }

    @Override
    public void writeString(String string) throws YaplException {
        // strip quotes
        writeStringNoQuotes(string.substring(1, string.length() - 1));
    }

    private void writeStringNoQuotes(String string) {
        int addr = backend.allocStringConstant(string);
        backend.writeString(addr);
    }

    @Override
    public void branchIfFalse(Attrib condition, String label) throws YaplException {
        backend.branchIf(false, label);
    }

    @Override
    public void jump(String label) {
        backend.jump(label);
    }
}
