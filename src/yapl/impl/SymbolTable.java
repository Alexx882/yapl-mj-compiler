package yapl.impl;

import yapl.interfaces.CompilerError;
import yapl.interfaces.Symbol;
import yapl.interfaces.Symboltable;
import yapl.lib.ProcedureType;
import yapl.lib.Type;
import yapl.lib.YaplException;
import yapl.lib.YaplExceptionArgs;

import java.util.*;

public class SymbolTable implements Symboltable {

    Stack<Scope> scopes = new Stack<>();
    boolean isInDebugMode = false;

    public SymbolTable() {
        scopes.push(getYaplPredefinedScope());
    }

    @Override
    public void openScope(boolean isGlobal) {
        if (isGlobal)
            // todo check
            throw new IllegalStateException("No more global scopes allowed");

        scopes.push(new Scope(isGlobal));
    }

    public void openScope() {
        openScope(false);
    }

    /**
     * Opens a new scope with the given symbol as parent. The parent symbol gets added to the OUTER scope.
     *
     * @param parentSymbol
     * @param row
     * @param col
     * @throws YaplException
     */
    public void openScope(YaplSymbol parentSymbol, int row, int col) throws YaplException {
        addSymbol(parentSymbol, row, col);

        openScope();
        setParentSymbol(parentSymbol);
    }

    @Override
    public void closeScope() {
        scopes.pop();
    }

    /**
     * Closes a named scope.
     *
     * @param name      The name of the scope at the end
     * @param isProgram True, if the scope was for a program
     * @param row
     * @param col
     * @throws YaplException
     */
    public void closeScope(String name, boolean isProgram, int row, int col) throws YaplException {
        Scope currentScope = scopes.pop();
        String startName = currentScope.getParentSymbol().getName();

        if (!startName.equals(name))
            throw new YaplException(CompilerError.EndIdentMismatch, row, col, new YaplExceptionArgs(isProgram, startName, name));
    }

    @Override
    public void addSymbol(Symbol s) throws YaplException {
        throw new UnsupportedOperationException("Use the overload with row and col instead.");
    }

    public void addSymbol(Symbol s, int row, int col) throws YaplException {
        Scope curScope = scopes.peek();

        if (curScope.getParentSymbol() != null && SymbolKind.find(curScope.getParentSymbol().getKind()) == SymbolKind.Typename)
            s.setKind(SymbolKind.Field.kind);

        if (curScope.containsSymbol(s.getName()))
            throw new YaplException(CompilerError.SymbolExists, row, col, new YaplExceptionArgs(s.getName(), s.getKindString()));

        curScope.putSymbol(s.getName(), s);
    }

    public void printSymbols() {
        for (Scope s : scopes) {
            System.out.println(s.getSymbolNames());
        }
    }

    @Override
    public Symbol lookup(String name) throws YaplException {
        if (name == null)
            throw new IllegalArgumentException("name must not be null");

        for (int i = scopes.size() - 1; i >= 0; --i)
            if (scopes.elementAt(i).containsSymbol(name))
                return scopes.elementAt(i).getSymbol(name);

        return null;
    }

    /**
     * Checks if the token is correctly declared, based on 3.2.1.
     *
     * @param name the name of the token
     * @return the correctly declared symbol
     * @throws YaplException
     */
    public Symbol checkCorrectDeclarationAsIdentifier(String name, int row, int col) throws YaplException {
        Symbol s = this.lookup(name);

        if (s == null)
            throw new YaplException(CompilerError.IdentNotDecl, row, col, new YaplExceptionArgs(name));

        switch (SymbolKind.find(s.getKind())) {
            case Variable:
            case Constant:
            case Typename:
            case Procedure:
            case Parameter:
                return s;

            default:
                throw new YaplException(CompilerError.IdentNotDecl, row, col, new YaplExceptionArgs(name));
        }
    }

    public void checkCorrectDeclarationAsProcedure(String name, int row, int col) throws YaplException {
        Symbol s = checkCorrectDeclarationAsIdentifier(name, row, col);

        if (SymbolKind.find(s.getKind()) != SymbolKind.Procedure)
            throw new YaplException(CompilerError.SymbolIllegalUse, row, col, new YaplExceptionArgs(name, s.getKindString()));
    }

    public void checkCorrectDeclarationAsPrimaryExpression(String name, int row, int col) throws YaplException {
        Symbol s = checkCorrectDeclarationAsIdentifier(name, row, col);

        switch (SymbolKind.find(s.getKind())) {
            case Variable:
            case Constant:
            case Parameter:
                return;

            default:
                throw new YaplException(CompilerError.SymbolIllegalUse, row, col, new YaplExceptionArgs(name, s.getKindString()));
        }
    }

    public void checkCorrectDeclarationAsTypeName(String name, int row, int col) throws YaplException {
        Symbol s = checkCorrectDeclarationAsIdentifier(name, row, col);

        if (SymbolKind.find(s.getKind()) != SymbolKind.Typename)
            throw new YaplException(CompilerError.SymbolIllegalUse, row, col, new YaplExceptionArgs(name, s.getKindString()));
    }

    public void checkCorrectDeclarationAsArray(String name, int row, int col) throws YaplException {
        Symbol s = checkCorrectDeclarationAsIdentifier(name, row, col);

        switch (SymbolKind.find(s.getKind())) {
            case Variable:
            case Parameter:
                return;

            default:
                throw new YaplException(CompilerError.SymbolIllegalUse, row, col, new YaplExceptionArgs(name, s.getKindString()));
        }
    }

    public void checkCorrectDeclarationAsLValue(String name, int row, int col) throws YaplException {
        Symbol s = checkCorrectDeclarationAsIdentifier(name, row, col);

        switch (SymbolKind.find(s.getKind())) {
            case Variable:
            case Parameter:
                return;

            default:
                throw new YaplException(CompilerError.SymbolIllegalUse, row, col, new YaplExceptionArgs(name, s.getKindString()));
        }
    }

    @Override
    public void setParentSymbol(Symbol sym) {
        scopes.lastElement().setParentSymbol(sym);
    }

    @Override
    public Symbol getNearestParentSymbol(int kind) {
        for (int i = scopes.size() - 1; i >= 0; --i)
            if (scopes.get(i).getParentSymbol() != null
                    && scopes.get(i).getParentSymbol().getKind() == kind)
                return scopes.get(i).getParentSymbol();

        return null;
    }

    @Override
    public void setDebug(boolean on) {
        this.isInDebugMode = on;
    }

    /**
     * Creates a new scope containing the predefined procedures from the YAPL syntax.
     *
     * @return the scope
     */
    private static Scope getYaplPredefinedScope() {
        Scope predefScope = new Scope(true);

        LinkedList<ProcedureType> predef = new LinkedList<>();

        ProcedureType writeint = new ProcedureType("writeint", Type.VOID);
        writeint.addParam("", Type.INT);
        predef.add(writeint);

        ProcedureType writebool = new ProcedureType("writebool", Type.VOID);
        writeint.addParam("", Type.BOOL);
        predef.add(writeint);

        predef.add(new ProcedureType("writeln", Type.VOID));

        predef.add(new ProcedureType("writeint", Type.INT));

        for (ProcedureType procedure : predef)
            predefScope.putSymbol(procedure.getName(), new YaplSymbol(procedure.getName(), SymbolKind.Procedure, procedure));

        return predefScope;
    }
}
