package yapl.impl;

import yapl.interfaces.Symbol;

import java.util.HashMap;
import java.util.Map;

public class Scope {
    public final Map<String, Symbol> symbols = new HashMap<>();
    public final boolean isGlobal;
    public Symbol parentSymbol;

    public Scope() {
        this(false);
    }

    public Scope(boolean isGlobal) {
        this.isGlobal = isGlobal;
    }

    public Symbol getParentSymbol() {
        return parentSymbol;
    }

    public void setParentSymbol(Symbol parentSymbol) {
        this.parentSymbol = parentSymbol;
    }

    public void putSymbol(String name, Symbol symbol) {
        symbols.put(name, symbol);
    }

    public Symbol getSymbol(String name) {
        return symbols.get(name);
    }

    public boolean containsSymbol(String name) {
        return symbols.containsKey(name);
    }
}
