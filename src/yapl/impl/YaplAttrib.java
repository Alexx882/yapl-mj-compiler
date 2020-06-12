package yapl.impl;

import yapl.interfaces.Attrib;
import yapl.interfaces.Symbol;
import yapl.lib.Type;
import yapl.lib.YaplException;

public class YaplAttrib implements Attrib {

    private int kind;
    private Type type;
    private boolean constant;
    private boolean readonly;
    private boolean global;
    private int offset;

    private int value;

    /**
     * Shortcut for constructing a INT constant.
     *
     * @param value the constant value
     */
    public YaplAttrib(int value) {
        this(Attrib.Constant, Type.INT);
        setValue(value);
    }

    /**
     * Shortcut for constructing a BOOL constant.
     *
     * @param value the constant value
     */
    public YaplAttrib(boolean value) {
        this(Attrib.Constant, Type.BOOL);
        setValue(value);
    }

    /**
     * Shortcut for constructing a value on the expression stack.
     *
     * @param type the type of the value
     */
    public YaplAttrib(Type type) {
        this.type = type;

        if (type.isBool() || type.isInt())
            kind = Attrib.RegValue;
        else
            kind = Attrib.RegAddress;
    }

    public YaplAttrib(int kind, Type type) {
        this.kind = kind;
        this.type = type;
    }

    public YaplAttrib(Symbol symbol) throws YaplException {

        switch (symbol.getKind()) {
            case Symbol.Constant:
                constant = true;

            case Symbol.Variable:
            case Symbol.Parameter:
                kind = Attrib.MemoryOperand;
                break;

            default:
                throw new YaplException(ErrorType.Internal, -1, symbol.getKind(), "Illegal Attrib kind.");
        }

        global = symbol.isGlobal();
        readonly = symbol.isReadonly();
        type = symbol.getType();
        offset = symbol.getOffset();
    }

    public YaplAttrib(Attrib attr) throws YaplException {
        kind = attr.getKind();
        global = attr.isGlobal();
        readonly = attr.isReadonly();
        constant = attr.isConstant();
        type = attr.getType();
        offset = attr.getOffset();
    }

    public void setValue(int value) {
        this.value = value;
        this.type = Type.INT;
    }

    public void setValue(boolean value) {
        this.value = BackendMJ.staticBoolValue(value);
        this.type = Type.BOOL;
    }

    public int getValue() {
        return value;
    }

    @Override
    public boolean isGlobal() {
        return global;
    }

    @Override
    public void setGlobal(boolean global) {
        this.global = global;
    }

    @Override
    public byte getKind() {
        return (byte) kind;
    }

    @Override
    public void setKind(byte kind) {
        this.kind = kind;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public void setType(Type type) {
        this.type = type;
    }

    @Override
    public boolean isConstant() {
        return constant;
    }

    @Override
    public void setConstant(boolean constant) {
        this.constant = constant;
    }

    @Override
    public boolean isReadonly() {
        return readonly;
    }

    @Override
    public void setReadonly(boolean readonly) {
        this.readonly = readonly;
    }

    @Override
    public int getOffset() {
        return offset;
    }

    @Override
    public void setOffset(int offset) {
        this.offset = offset;
    }

    @Override
    public byte getRegister() {
        // not needed for stack machine
        return 0;
    }

    @Override
    public void setRegister(byte register) {
        // not needed for stack machine
    }
}
