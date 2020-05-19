package yapl.lib;

import yapl.interfaces.CompilerError;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Objects;

public class RecordType extends Type {

    private final String name;

    // LinkedHashMap is insertion ordered and will retain the order in which elements were inserted
    private final LinkedHashMap<String, Type> fields;

    public RecordType(String name) {
        this.name = name;


        fields = new LinkedHashMap<>();
    }

    public void addField(String name, Type type) {
        fields.put(name, type);
    }

    public Type getFieldType(String name) {
        return fields.get(name);
    }

    public Type getFieldType(int index) throws YaplException {
        if (index >= fields.size())
            throw new YaplException(CompilerError.Internal, -1, -1, new YaplExceptionArgs(
                    "record field index out of bounds for type " + name)
            );

        Iterator<Type> fieldTypes = fields.values().iterator();

        for (int i = 0; i < index; i++) {
            fieldTypes.next();
        }

        return fieldTypes.next();
    }

    public boolean hasField(String name) {
        return fields.containsKey(name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RecordType that = (RecordType) o;

        var thisIter = fields.entrySet().iterator();
        var thatIter = that.fields.entrySet().iterator();

        while (thisIter.hasNext())
            if (!thatIter.hasNext() || !Objects.equals(thisIter.next(), thatIter.next()))
                return false;

        return !thatIter.hasNext();
    }

    @Override
    public int hashCode() {
        return fields.hashCode();
    }
}
