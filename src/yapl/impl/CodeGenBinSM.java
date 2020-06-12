package yapl.impl;

import yapl.compiler.Token;
import yapl.interfaces.Attrib;
import yapl.interfaces.CodeGen;
import yapl.interfaces.ExtendedBackendBinSM;
import yapl.interfaces.Symbol;
import yapl.lib.*;

import java.util.ArrayList;
import java.util.List;

import static yapl.compiler.YaplConstants.*;
import static yapl.impl.ErrorType.*;
import static yapl.interfaces.MemoryRegion.*;

/**
 * Implementation of CodeGen interface generating binary code for stack machines.
 */
public class CodeGenBinSM implements CodeGen {
    private final ExtendedBackendBinSM backend;

    private int labelCounter = 0;

    private List<YaplAttrib> globalDeclarations = new ArrayList<>();

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

            default:
                throw new IllegalStateException("Not implemented.");
        }

        attr.setKind(Attrib.RegValue);
        return 0; // return register number not needed for stack machine
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
        switch (sym.getKind()) {
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
        if (arr.getKind() != Attrib.RegAddress)
            loadAddress(arr);

        backend.arrayLength();
        return new YaplAttrib(Attrib.RegValue, Type.INT);
    }

    @Override
    public void assign(Attrib lvalue, Attrib expr) throws YaplException {
        if (!lvalue.getType().equals(expr.getType()))
            throw new IllegalStateException("Assignment type mismatch");

        if (lvalue.isGlobal() && lvalue.isConstant()) {
            var constDecl = new YaplAttrib(lvalue);
            constDecl.setValue(((YaplAttrib) expr).getValue());
            constDecl.setOffset(backend.allocStaticData(1));

            globalDeclarations.add(constDecl);
            return;
        }

        switch (lvalue.getKind()) {
            case Attrib.MemoryOperand:
                int offset;
                if (lvalue.isGlobal()) {
                    offset = backend.allocStaticData(1);
                    backend.storeWord(STATIC, offset);
                } else {
                    offset = backend.allocStack(1);
                    backend.storeWord(STACK, offset);
                }
                lvalue.setOffset(offset);
                break;
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
            Type type = proc.getType();
            if (!type.isProcedure())
                throw new YaplException(Internal, -1, -1, "Procedure symbol type is not procedure.");

            ProcedureType procType = (ProcedureType) type;

            nParams = procType.getParams().size();
        }

        // add hashcode to avoid errors with duplicate names (see /testfiles/symbolcheck/test13.yapl)
        // use spaces as delimiter as they are not allowed in YAPL identifiers
        String label = proc.getName() + " " + proc.hashCode();

        backend.enterProc(label, nParams, isMain);

        if (isMain) {
            for (var decl : globalDeclarations) {
                backend.loadConst(decl.getValue());
                backend.storeWord(STATIC, decl.getOffset());
            }
        }
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
        for (PredefinedFunction predefFunc : PredefinedFunction.values())
            if (predefFunc.procedureType == proc.getType())
                return callPredefinedFunction(predefFunc, args);

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
