package yapl.lib;

import yapl.impl.Procedure;

public class Type {

    public static final Type INT = new Type();
    public static final Type BOOL = new Type();
    public static final Type VOID = new Type();

    Type() {
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    public boolean isInt() {
        return this == INT;
    }

    public boolean isBool() {
        return this == BOOL;
    }

    public boolean isVoid() {
        return this == VOID;
    }

    public boolean isArray() {
        return this instanceof ArrayType;
    }

    public boolean isRecord() {
        return this instanceof RecordType;
    }

    public boolean isProcedure() {
        return this instanceof ProcedureType;
    }
}
