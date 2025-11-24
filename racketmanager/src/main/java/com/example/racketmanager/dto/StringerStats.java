package com.example.racketmanager.dto;

public class StringerStats {

    public String name;
    public int poly;
    public int nylon;
    public int natural;
    public int total;

    public StringerStats(String name, int poly, int nylon, int natural) {
        this.name = name;
        this.poly = poly;
        this.nylon = nylon;
        this.natural = natural;
        this.total = poly * 800 + nylon * 800 + natural * 1000;
    }
}
