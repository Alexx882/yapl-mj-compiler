package yapl.impl;

import yapl.lib.ProcedureType;
import yapl.lib.Type;

import static yapl.lib.Type.*;

public enum PredefinedFunction {

    /**
     * Procedure void writeint(int i);
     *
     * write i to stdout
     */
    writeint(VOID, INT),

    /**
     * Procedure void writebool(bool b);
     *
     * write b ("True" or "False") to stdout
     */
    writebool(VOID, BOOL),

    /**
     * Procedure void writeln();
     *
     * write a newline character to stdout
     */
    writeln(VOID),

    /**
     * Procedure int readint();
     *
     * read an integer value from stdin; characters following the number up to newline are ignored.
     */
    readint(INT);

    public Type[] params;
    public ProcedureType procedureType;

    PredefinedFunction(Type returnType, Type... params) {
        this.params = params;
        procedureType = new ProcedureType(this.name(), returnType);
        int i = 1;
        for (Type param : params)
            procedureType.addParam("arg" + i++, param);
    }
}
