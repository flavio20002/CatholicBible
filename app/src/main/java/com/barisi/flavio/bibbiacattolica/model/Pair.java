package com.barisi.flavio.bibbiacattolica.model;

import java.io.Serializable;

public class Pair<A, B> implements Serializable {

    private final A val0;
    private final B val1;

    public Pair(final A value0, final B value1) {
        this.val0 = value0;
        this.val1 = value1;
    }

    public A getVal0() {
        return val0;
    }

    public B getVal1() {
        return val1;
    }
}