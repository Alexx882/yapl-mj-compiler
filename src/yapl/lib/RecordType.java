package yapl.lib;

import yapl.impl.ErrorType;
import yapl.interfaces.Symbol;

import java.util.*;

public class RecordType extends Type {

    public final String name;
    private final ArrayList<Symbol> fields;
    private final HashMap<String, Integer> fieldLookup;

    public RecordType(String name) {
        this.name = name;

        fields = new ArrayList<>();
        fieldLookup = new HashMap<>();
    }

    public void addField(Symbol s) {
        fieldLookup.put(s.getName(), fields.size());
        fields.add(s);
    }

    public Symbol getField(int i) {
        return fields.get(i);
    }

    public Symbol getField(String name) {
        return fields.get(getFieldOffset(name));
    }

    public Type getFieldType(String name) {
        return getField(name).getType();
    }

    public Type getFieldType(int index) {
        return getField(index).getType();
    }

    public boolean hasField(String name) {
        return fieldLookup.containsKey(name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RecordType that = (RecordType) o;

        if (fields.size() != that.fields.size()) return false;

        for (int i = 0; i < fields.size(); i++) {
            var thisField = getField(i);
            var thatField = that.getField(i);

            // compare field names
            if (!Objects.equals(thisField.getName(), thatField.getName()))
                return false;

            // check if both fields are self reference to prevent falling into infinite loop
            if (isSelfReference(i) && that.isSelfReference(i))
                continue;

            // compare field types
            if (!Objects.equals(thisField.getType(), thatField.getType()))
                return false;
        }

        return true;
    }

    /**
     * Checks if field #i is a self reference.
     *
     * Example:
     *
     * Record List
     *     int item;
     *     List next;
     * EndRecord;
     *
     * the field "next" (index 1) is a self reference.
     */
    private boolean isSelfReference(int i) {
        return this == getFieldType(i);
    }

    @Override
    public int hashCode() {
        return fields.hashCode();
    }

    public int nFields() {
        return fields.size();
    }

    public int getFieldOffset(String name) {
        return fieldLookup.get(name);
    }
}
