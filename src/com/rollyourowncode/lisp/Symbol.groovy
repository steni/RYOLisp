package com.rollyourowncode.lisp


class Symbol {
    String symbol

    public boolean equals(Symbol otherSymbol) {
        return symbol.equals(otherSymbol.symbol)
    }

    public int hashCode() {
        return symbol.hashCode()
    }

}
