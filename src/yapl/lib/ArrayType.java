package yapl.lib;

import java.util.Objects;

public class ArrayType extends Type {

    /**
     * dim(this): the number of dimensions of the array (dim(this) >= 1).
     */
    private final int dim;

    /**
     * elem(this): the element data type of the array.
     */
    private final Type baseType;

    public ArrayType(int dim, Type baseType) {
        this.dim = dim;
        this.baseType = baseType;
    }

    public int getDim(){
        return dim;
    }

    /**
     * subarray(this): the array type this' derived from this by omitting the first dimension,
     * @return subarray(this)
     */
    public Type getElemType() {
        return dim == 1 ? baseType : new ArrayType(dim - 1, baseType);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

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
