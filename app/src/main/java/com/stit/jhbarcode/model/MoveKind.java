package com.stit.jhbarcode.model;

public enum MoveKind {
    PackLocMove("1"),
    HeadCoilLocMove("2"),
    WireCpilLocMove("3");

    private String value;

    MoveKind(String value) { this.value = value; }

    public String getValue() {
        return value;
    }
}
