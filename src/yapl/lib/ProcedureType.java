package yapl.lib;

import java.util.LinkedHashMap;
import java.util.Objects;

public class ProcedureType extends Type {

    private String name;

    private Type returnType;
    private LinkedHashMap<String, Type> parameters;

    public String getName() {
        return name;
    }

    public Type getReturnType() {
        return returnType;
    }

    public ProcedureType(String name, Type returnType) {
        this.name = name;
        this.returnType = returnType;

        parameters = new LinkedHashMap<>();
    }

    public void addParam(String name, Type type) {
        parameters.put(name, type);
    }

    public LinkedHashMap<String, Type> getParams() {
        return new LinkedHashMap<>(parameters);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProcedureType that = (ProcedureType) o;

        //if (!Objects.equals(name, that.name)) return false;
        if (!Objects.equals(returnType, that.returnType)) return false;
        return Objects.equals(parameters.values(), that.parameters.values());
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (returnType != null ? returnType.hashCode() : 0);
        result = 31 * result + (parameters != null ? parameters.values().hashCode() : 0);
        return result;
    }
}
