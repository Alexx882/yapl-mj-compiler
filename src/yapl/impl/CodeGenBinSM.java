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

            default:
                throw new IllegalStateException("Loading "+ attr.getKind() +" not implemented.");
        }

        // fixme why was the kind changed? const (3) will be changed to 1 and not working anymore
//        attr.setKind(Attrib.RegValue);
        return 0; // return register number not needed for stack machine
    }

    @Override
    public byte loadAddress(Attrib attr) throws YaplException {
        if (attr.getKind() == Attrib.MemoryOperand) {
            if (attr.getType().isArray()) {
                loadValue(attr);
                attr.setKind(Attrib.RegAddress);
            }
        }

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
                if (sym.isGlobal()) {
                    offset = backend.allocStaticData(1);
                } else {
                    offset = backend.allocStack(1);
                }
                sym.setOffset(offset);
                break;
        }
    }

    @Override
    public void setFieldOffsets(RecordType record) {

    }

    @Override
    public void storeArrayDim(int dim, Attrib length) throws YaplException {

    }

    /**
     * This implementation assumes that the array lengths (nr == arrayTime.dim) is already on the stack.
     * @param arrayType  array type.
     * @return
     * @throws YaplException
     */
    @Override
    public Attrib allocArray(ArrayType arrayType) throws YaplException {
        if (arrayType.getDim() != 1)
            throw new YaplException(Internal, -1, -1, "Multidim arrays not implemented yet");
        backend.storeArrayDim(arrayType.getDim()-1);

        // requires the array length to be on top of the stack
        backend.allocArray();

        // address is located on exp stack
        return new YaplAttrib(Attrib.RegAddress, arrayType);
    }

    @Override
    public Attrib allocRecord(RecordType recordType) throws YaplException {
        return null;
    }

    @Override
    public void setParamOffset(Symbol sym, int pos) {

    }

    /**
     * Allocate array at run time.
     * Array length is the top element on the stack.
     *
     * @param arr  array type.
     * @return           Attrib object representing a register operand
     *                   holding the array base address.
     * @throws YaplException
     */
    @Override
    public void arrayOffset(Attrib arr, Attrib index) throws YaplException {
        arr.setKind(Attrib.ArrayElement);
    }

    @Override
    public void recordOffset(Attrib record, Symbol field) throws YaplException {

    }

    @Override
    public Attrib arrayLength(Attrib arr) throws YaplException {
        if (arr.getKind() != Attrib.RegAddress)
            loadAddress(arr);

        backend.arrayLength();
        return new YaplAttrib(Attrib.RegValue, Type.INT);
    }

    @Override
    public void assign(Attrib lvalue, Attrib expr) throws YaplException {
        // TODO make sure that lvalue address is on stack if lvalue is array or record

        if (!lvalue.getType().equals(expr.getType()))
            throw new IllegalStateException("Assignment type mismatch");

        // special treatment for global constants, since they need code to assign values,
        // but code is only executed after the startPC
        // solution: move value assignment to start of main procedure
        if (lvalue.isGlobal() && lvalue.isConstant()) {
            globalConstantAssignments.put(lvalue,((YaplAttrib) expr).getValue());
            return;
        }

        switch (lvalue.getKind()) {
            case Attrib.MemoryOperand:
                backend.storeWord(lvalue.isGlobal() ? STATIC : STACK, lvalue.getOffset());
                break;

            case Attrib.ArrayElement:
                backend.storeArrayElement();
                break;

            // TODO assignments for array elements, record fields

            default:
                throw new YaplException(Internal, -1, -1, "Not implemented yet");
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

        return new YaplAttrib(Attrib.RegValue, Type.INT);
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

        return new YaplAttrib(Attrib.RegValue, x.getType());
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

        return new YaplAttrib(Attrib.RegValue, Type.BOOL);
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

        return new YaplAttrib(Attrib.RegValue, Type.BOOL);
    }

    @Override
    public void enterProc(Symbol proc) throws YaplException {
        boolean isMain = proc.getKind() == Symbol.Program;
        int nParams = 0;

        if (!isMain) {
            // load nParams
            Type type = proc.getType();
            if (!type.isProcedure())
                throw new YaplException(Internal, -1, -1, "Procedure symbol type is not procedure.");

            nParams = ((ProcedureType) type).getParams().size();
        }

        String label = Procedure.getLabel(proc, false);

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
        backend.exitProc(Procedure.getLabel(proc,true));
    }

    @Override
    public void returnFromProc(Symbol proc, Attrib returnVal) throws YaplException {

    }

    @Override
    public Attrib callProc(Symbol proc, Attrib[] args) throws YaplException {
        System.out.println(proc.getName() + ": " + args.length);

        for (PredefinedFunction predefFunc : PredefinedFunction.values())
            if (predefFunc.procedureType == proc.getType())
                return callPredefinedFunction(predefFunc, args);

        /*
        * Info: currently all arguments for a procedure call are calculated immediately.
        * eg. f(1+1, a or b) will immediately: const1, const1, add, <a or b ie const1/0),
        * so the values are already on the stack.
        * This is probably fine, but must not be forgotten.
        */

//        int cnt = 0;
//        for (Attrib arg : args) {
//            if (arg.getType().isPrimitive()) {
//                System.out.println("\t" + arg.getType().isPrimitive() + " " + arg.getKind() + " " + ((YaplAttrib)arg).getValue());
//                arg.setOffset(cnt++);
//                this.loadValue(arg);
//            } else {
//                // complex type
//                // todo
//            }
//        }

        backend.callProc(Procedure.getLabel(proc, false));

        return new YaplAttrib(((ProcedureType) proc.getType()).getReturnType());
    }

    public Attrib callPredefinedFunction(PredefinedFunction f, Attrib[] args) throws YaplException {
        Type returnType = Type.VOID;

        switch (f) {
            case writeint:
                backend.writeInteger();
                break;

            case writebool:
                String elseLabel = newLabel(), endIfLabel = newLabel();

                // if
                branchIfFalse(args[0], elseLabel);
                // then
                writeString("True");
                jump(endIfLabel);
                // else
                assignLabel(elseLabel);
                writeString("False");
                // endif
                assignLabel(endIfLabel);
                break;

            case writeln:
                writeString(System.lineSeparator());
                break;

            case readint:
//                backend.readInteger();
//                returnType = Type.INT;
//                break;
                throw new IllegalStateException("readint() is not implemented");

            default:
                throw new IllegalStateException("Predefined function not implemented.");
        }

        return new YaplAttrib(returnType);
    }

    @Override
    public void writeString(String string) throws YaplException {
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
