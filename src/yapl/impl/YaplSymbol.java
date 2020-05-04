package yapl.impl;

import yapl.lib.Type;

public class YaplSymbol implements yapl.interfaces.Symbol {

    private SymbolKind kind;
    private Type type;
    private boolean isReference;
    private boolean isReadonly;
    private boolean isGlobal;
    private int offset;
    private final String name;
    private yapl.interfaces.Symbol nextSymbol;
    private boolean returnSeen;

    @Override
    public int getKind() {
        return kind.kind;
    }

    @Override
    public void setKind(int kind) {
        this.kind = SymbolKind.find(kind);
    }

    @Override
    public String getKindString() {
        return kind.name();
    }

    @Override
    public String getName() {
        return name;
    }

    public YaplSymbol(String name) {
        this.name = name;
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
    public boolean isReference() {
        return isReference;
    }

    @Override
    public void setReference(boolean isReference) {
        this.isReference = isReference;
    }

    @Override
    public boolean isReadonly() {
        return isReadonly;
    }

    @Override
    public void setReadonly(boolean isReadonly) {
        this.isReadonly = isReadonly;
    }

    @Override
    public boolean isGlobal() {
        return isGlobal;
    }

    @Override
    public void setGlobal(boolean isGlobal) {
        this.isGlobal = isGlobal;
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
    public yapl.interfaces.Symbol getNextSymbol() {
        return nextSymbol;
    }

    @Override
    public void setNextSymbol(yapl.interfaces.Symbol symbol) {
        this.nextSymbol = symbol;
    }

    @Override
    public boolean getReturnSeen() {
        return returnSeen;
    }

    @Override
    public void setReturnSeen(boolean seen) {
        this.returnSeen = seen;
    }
}
