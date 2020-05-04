package yapl.impl;

public enum SymbolKind {
    Program(YaplSymbol.Program),
    Procedure(YaplSymbol.Procedure),
    Variable(YaplSymbol.Variable),
    Constant(YaplSymbol.Constant),
    Typename(YaplSymbol.Typename),
    Field(YaplSymbol.Field),
    Parameter(YaplSymbol.Parameter);

    int kind;

    SymbolKind(int kind) {
        this.kind = kind;
    }

    public static SymbolKind find(int num) {
        for(SymbolKind symbolKind : SymbolKind.values()) {
            if(symbolKind.kind == num)
                return symbolKind;
        }

        throw new IllegalArgumentException(String.format("No SymbolKind found for number %d", num));
    }
}
