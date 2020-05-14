package yapl.lib;

import java.util.Objects;

public class ArrayType extends Type {

    private final int dim;
    private final Type ilimType;

    public ArrayType(int dim, Type ilimType) {
        this.dim = dim;
        this.ilimType = ilimType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        ArrayType arrayType = (ArrayType) o;

        if (dim != arrayType.dim) return false;
        return Objects.equals(ilimType, arrayType.ilimType);
    }

    @Override
    public int hashCode() {
        int result = dim;
        result = 31 * result + (ilimType != null ? ilimType.hashCode() : 0);
        return result;
    }
}
