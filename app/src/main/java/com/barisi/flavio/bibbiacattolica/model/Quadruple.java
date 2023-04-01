package com.barisi.flavio.bibbiacattolica.model;

import java.io.Serializable;

public class Quadruple<A, B, C, D> implements Serializable {

    private final A val0;
    private final B val1;
    private final C val2;
    private final D val3;

    public Quadruple(final A value0, final B value1, final C value2, final D value3) {
        this.val0 = value0;
        this.val1 = value1;
        this.val2 = value2;
        this.val3 = value3;
    }

    public A getVal0() {
        return val0;
    }

    public B getVal1() {
        return val1;
    }

    public C getVal2() {
        return val2;
    }

    public D getVal3() {
        return val3;
    }

}