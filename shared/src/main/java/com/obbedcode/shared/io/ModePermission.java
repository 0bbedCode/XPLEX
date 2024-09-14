package com.obbedcode.shared.io;

public enum ModePermission {
    NONE(0),
    READ(4),
    WRITE(2),
    EXECUTE(1),

    EXECUTE_WRITE(3),
    READ_EXECUTE(5),
    READ_WRITE(6),
    READ_WRITE_EXECUTE(7);

    private final int value;
    ModePermission(int value) { this.value = value; }
    public int getValue() { return value; }
}