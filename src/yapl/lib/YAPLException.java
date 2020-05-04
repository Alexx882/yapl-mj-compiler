package yapl.lib;

import yapl.interfaces.CompilerError;

public class YAPLException extends Throwable implements CompilerError {

    private final int errorNumber;
    private final int line;
    private final int column;

    public YAPLException(int errorNumber, int line, int column) {
        this.errorNumber = errorNumber;
        this.line = line;
        this.column = column;
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
        return String.format("ERROR %d (line %d, column %d)", errorNumber, line, column);
    }
}
