package yapl.lib;

import java.util.LinkedHashMap;
import java.util.Objects;

public class ProcedureType extends Type {

    public final String name;

    private final Type returnType;
    private final LinkedHashMap<String, Type> parameters;

    public Type getReturnType() {
        return returnType;
    }

    public ProcedureType(String name, Type returnType) {
        this.name = name;
        this.returnType = returnType;

        parameters = new LinkedHashMap<>();
    }

    /**
     * Adds a new parameter to the procedure and returns its position in the parameter list.
     * @param name
     * @param type
     * @return
     */
    public int addParam(String name, Type type) {
        parameters.put(name, type);
        return parameters.size()-1;
    }

    public LinkedHashMap<String, Type> getParams() {
        return new LinkedHashMap<>(parameters);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProcedureType that = (ProcedureType) o;

        if (!Objects.equals(name, that.name)) return false;
        if (!Objects.equals(returnType, that.returnType)) return false;

        var thisIter = parameters.entrySet().iterator();
        var thatIter = that.parameters.entrySet().iterator();

        while (thisIter.hasNext())
            if (!thatIter.hasNext() || !Objects.equals(thisIter.next(), thatIter.next()))
                return false;

        return !thatIter.hasNext();
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (returnType != null ? returnType.hashCode() : 0);
        result = 31 * result + (parameters != null ? parameters.values().hashCode() : 0);
        return result;
    }
}
