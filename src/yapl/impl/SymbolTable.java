package yapl.impl;

import yapl.interfaces.Symbol;
import yapl.interfaces.Symboltable;
import yapl.lib.YAPLException;

import java.util.*;

public class SymbolTable implements Symboltable {

    Stack<Scope> scopes = new Stack<>();
    boolean isInDebugMode = false;

    @Override
    public void openScope(boolean isGlobal) {
        scopes.push(new Scope(isGlobal));
    }

    @Override
    public void closeScope() {
        scopes.pop();
    }

    @Override
    public void addSymbol(Symbol s) throws YAPLException {
        scopes.peek().putSymbol(s.getName(), s);
    }

    @Override
    public Symbol lookup(String name) throws YAPLException {
        if (name == null)
            throw new YAPLException();

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
}
