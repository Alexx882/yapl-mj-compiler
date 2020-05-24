package yapl.lib;

import yapl.compiler.Token;
import yapl.impl.ErrorType;
import yapl.interfaces.CompilerError;

public class YaplException extends Throwable implements CompilerError {

    private final ErrorType errorType;
    private final int line;
    private final int column;

    // args is declared as Object[] since it is only used in String.format(String format, Object... args)
    // this allows for the use of Tokens as input, since Token.toString() evaluates to Token.image
    private final Object[] args;

    public YaplException(ErrorType errorType, int line, int column, Object... args){
        this.errorType = errorType;
        this.line = line;
        this.column = column;
        this.args = args;
    }

    public YaplException(ErrorType errorType, Token token, Object... args){
        this(errorType, token.beginLine, token.beginColumn, args);
    }

    @Override
    public int errorNumber() {
        return errorType.errorNumber;
    }

    public ErrorType errorType() {
        return errorType;
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
        sb.append(String.format("ERROR %d (line %d, column %d)", errorType.errorNumber, line, column));
        sb.append('\n');

        sb.append(String.format(errorType.message, args));

        return sb.toString();
    }
}
