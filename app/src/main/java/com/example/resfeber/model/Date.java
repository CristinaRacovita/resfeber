package com.example.resfeber.model;

import java.io.Serializable;

public class Date implements Serializable {
    private Start start;

    public Start getStart() {
        return start;
    }

    public void setStart(Start start) {
        this.start = start;
    }
}
