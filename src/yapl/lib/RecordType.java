package yapl.lib;

import yapl.interfaces.CompilerError;

import java.util.LinkedHashMap;

public class RecordType extends Type {

    // LinkedHashMap is insertion ordered and will retain the order in which elements were inserted
    private final LinkedHashMap<String, Type> fields;

    public RecordType() {
        fields = new LinkedHashMap<>();
    }

    public void addField(String name, Type type) {
        fields.put(name, type);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        RecordType that = (RecordType) o;

        return fields.equals(that.fields);
    }

    @Override
    public int hashCode() {
        return fields.hashCode();
    }
}
