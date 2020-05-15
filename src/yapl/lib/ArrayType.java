package yapl.lib;

import java.util.Objects;

public class ArrayType extends Type {

    private final int dim;
    private final Type baseType;

    public ArrayType(int dim, Type baseType) {
        this.dim = dim;
        this.baseType = baseType;
    }

    public Type getIlimType() {
        return dim == 0 ? baseType : new ArrayType(dim - 1, baseType);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        ArrayType arrayType = (ArrayType) o;

        if (dim != arrayType.dim) return false;
        return Objects.equals(baseType, arrayType.baseType);
    }

    @Override
    public int hashCode() {
        int result = dim;
        result = 31 * result + (baseType != null ? baseType.hashCode() : 0);
        return result;
    }
}
