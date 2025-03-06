// Created by Eric B. 17.05.2021 12:00
package de.ericzones.lobbysystem.manager;

public enum ChatFormat {

    VANISH("§k"),
    CROSSED("§m"),
    UNDERLINED("§n"),
    BOLD("§l"),
    ITALIC("§o"),
    NONE(null);

    private ChatFormat(String symbol){
        this.symbol = symbol;
    }

    private String symbol;

    public String getSymbol() {
        return symbol;
    }

}
