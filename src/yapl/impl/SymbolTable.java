package yapl.impl;

import yapl.interfaces.CompilerError;
import yapl.interfaces.Symbol;
import yapl.interfaces.Symboltable;
import yapl.lib.YaplException;

import java.util.*;

public class SymbolTable implements Symboltable {

    Stack<Scope> scopes = new Stack<>();
    boolean isInDebugMode = false;

    public SymbolTable(){
        scopes.push(getYaplPredefinedScope());
    }

    @Override
    public void openScope(boolean isGlobal) {
        scopes.push(new Scope(isGlobal));
    }

    public void openScope() {
        openScope(false);
    }

    @Override
    public void closeScope() {
        scopes.pop();
    }

    public void closeScope(String name) throws YaplException {
        Scope currentScope = scopes.pop();

        if (!currentScope.getParentSymbol().getName().equals(name))
            throw new YaplException(
                    CompilerError.EndIdentMismatch,
                    0,
                    0
            );
    }

    @Override
    public void addSymbol(Symbol s) throws YaplException {
        scopes.peek().putSymbol(s.getName(), s);
    }

    @Override
    public Symbol lookup(String name) throws YaplException {
        if (name == null)
            throw new YaplException(
                    CompilerError.Internal,
                    0, 0
            );

        // TODO implement message

        for (int i = scopes.size() - 1; i >= 0; --i)
            if (scopes.elementAt(i).containsSymbol(name))
                return scopes.elementAt(i).getSymbol(name);

        return null;
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
     * @return the scope
     */
    private static Scope getYaplPredefinedScope() {
        Scope predefScope = new Scope();

        for (String procedureName : new String[]{"writeint", "writebool", "writeln", "readint"})
            predefScope.putSymbol(procedureName, new YaplSymbol(procedureName, SymbolKind.Procedure));

        return predefScope;
    }
}
