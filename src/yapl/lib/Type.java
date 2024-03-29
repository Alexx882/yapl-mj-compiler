package yapl.lib;

public class Type {

    public static final Type INT = new Type();
    public static final Type BOOL = new Type();
    public static final Type VOID = new Type();

    Type() {
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

    public boolean isPrimitive() {
        return isInt() || isBool() ;
    }
}
