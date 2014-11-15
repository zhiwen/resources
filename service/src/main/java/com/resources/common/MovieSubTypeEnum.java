package com.resources.common;

public enum MovieSubTypeEnum {

    movie(1), tv(2);

    private final int value;

    private MovieSubTypeEnum(int value){
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public static MovieSubTypeEnum valueOf(int value) {
        if (1 == value) {
            return movie;
        }
        if (2 == value) {
            return tv;
        }
        return null;
    }
}
