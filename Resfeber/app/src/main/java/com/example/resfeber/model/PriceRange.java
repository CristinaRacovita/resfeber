package com.example.resfeber.model;

import java.io.Serializable;

public class PriceRange implements Serializable {
    private String currency;
    private Number min;

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Number getMin() {
        return min;
    }

    public void setMin(Number min) {
        this.min = min;
    }
}
