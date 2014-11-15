package com.resources.common;

public enum MovieSubType {

    movie(1), tv(2);

    public final int typeValue;

    private MovieSubType(int typeValue){
        this.typeValue = typeValue;
    }

}
