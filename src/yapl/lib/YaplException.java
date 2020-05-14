package yapl.lib;

import yapl.interfaces.CompilerError;

public class YaplException extends Throwable implements CompilerError {

    private final int errorNumber;
    private final int line;
    private final int column;
    YaplExceptionArgs args;

    private YaplException(int errorNumber, int line, int column) {
        this.errorNumber = errorNumber;
        this.line = line;
        this.column = column;
    }

    public YaplException(int errorNumber, int line, int column, YaplExceptionArgs args){
        this(errorNumber, line, column);
        this.args = args;
    }

    @Override
    public int errorNumber() {
        return errorNumber;
    }

    @Override
    public int line() {
        return line;
    }

    @Override
    public int column() {
        return column;
    }

    @Override
    public String getMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("ERROR %d (line %d, column %d)", errorNumber, line, column));
        sb.append('\n');

        switch (errorNumber)
        {
            case CompilerError.EndIdentMismatch:
                sb.append(String.format("End %s does not match %s %s",
                        args.startName, args.isProgram ? "Program" : "Procedure", args.endName));
                break;

            case CompilerError.SymbolExists:
                sb.append(String.format("symbol %s already declared in current scope (as %s)", args.name, args.kind));
                break;

            case CompilerError.IdentNotDecl:
                sb.append(String.format("identifier %s not declared", args.name));
                break;

            case CompilerError.SymbolIllegalUse:
                sb.append(String.format("illegal use of %s %s", args.kind, args.name));
                break;

            case CompilerError.ArrayLenNotArray:
                sb.append("expression after '#' is not an array type");
        }

        return sb.toString();
    }
}
