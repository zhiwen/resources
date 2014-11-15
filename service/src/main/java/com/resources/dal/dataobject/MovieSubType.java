package com.resources.dal.dataobject;

public enum MovieSubType {

    movie(1), tv(2);

    private final int typeValue;

    private MovieSubType(int typeValue){
        this.typeValue = typeValue;
    }

}
