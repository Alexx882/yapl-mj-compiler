package yapl.impl;

import yapl.interfaces.CompilerError;

public enum ErrorType {

    /* --- Error Numbers --- */

    Internal(CompilerError.Internal, "Internal error: %s"),
    Lexical(CompilerError.Lexical, "Lexical error."),
    Syntax(CompilerError.Syntax, "Syntax error."),

    /* Symbol check errors */

    SymbolExists(CompilerError.SymbolExists, "Symbol '%s' already declared in current scope (as %s)."),
    IdentNotDecl(CompilerError.IdentNotDecl, "Identifier '%s' not declared."),
    SymbolIllegalUse(CompilerError.SymbolIllegalUse, "Illegal use of %s '%s'."),
    EndIdentMismatch(CompilerError.EndIdentMismatch, "'End %s' does not match '%s %s'"),

    /* Type check errors */

    SelectorNotArray(CompilerError.SelectorNotArray, "Expression before '[' is not an array type."),
    BadArraySelector(CompilerError.BadArraySelector, "Array index or dimension is not an integer type."),
    ArrayLenNotArray(CompilerError.ArrayLenNotArray, "Expression after '#' is not an array type."),
    IllegalOp1Type(CompilerError.IllegalOp1Type, "Illegal operand type for unary operator '%s'."),
    IllegalOp2Type(CompilerError.IllegalOp2Type, "Illegal operand type for binary operator '%s'."),
    IllegalRelOpType(CompilerError.IllegalRelOpType, "Illegal operand type for relational operator '%s'."),
    IllegalEqualOpType(CompilerError.IllegalEqualOpType, "Illegal operand type for equality operator '%s'."),
    ProcNotFuncExpr(CompilerError.ProcNotFuncExpr, "Using procedure '%s' (not a function) in expression."),
    ReadonlyAssign(CompilerError.ReadonlyAssign, "Read-only l-value in assignment."),
    TypeMismatchAssign(CompilerError.TypeMismatchAssign, "Type mismatch in assignment."),
    ArgNotApplicable(CompilerError.ArgNotApplicable, "Argument #%s not applicable to procedure '%s'."),
    ReadonlyArg(CompilerError.ReadonlyArg, "Read-only argument passed to read-write procedure."),
    TooFewArgs(CompilerError.TooFewArgs, "Too few arguments for procedure '%s'."),
    CondNotBool(CompilerError.CondNotBool, "Condition is not a boolean expression."),
    ReadonlyNotReference(CompilerError.ReadonlyNotReference, "Readonly not followed by reference type."),
    MissingReturn(CompilerError.MissingReturn, "Missing return statement in function '%s'."),
    InvalidReturnType(CompilerError.InvalidReturnType, "Returning none or invalid type from function '%s'."),
    IllegalRetValProc(CompilerError.IllegalRetValProc, "Illegal return value in procedure '%s' (not a function)."),
    IllegalRetValMain(CompilerError.IllegalRetValMain, "Illegal return value in main program."),
    SelectorNotRecord(CompilerError.SelectorNotRecord, "Expression before '.' is not a record type."),
    InvalidRecordField(CompilerError.InvalidRecordField, "Invalid field '%s' of record '%s'."),
    InvalidNewType(CompilerError.InvalidNewType, "Invalid type used with 'new' operator."),

    /* Code generation errors */

    NoMoreRegs(CompilerError.NoMoreRegs, "Too many registers used."),
    TooManyDims(CompilerError.TooManyDims, "Too many array dimensions.");

    /* --- End of error numbers --- */

    public final int errorNumber;
    public final String message;

    ErrorType(int value, String message) {
        this.errorNumber = value;
        this.message = message;
    }
}
