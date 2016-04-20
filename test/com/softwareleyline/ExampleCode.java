package com.softwareleyline;

/**
 * Created by Geoff on 4/19/2016.
 */
public class ExampleCode {

    private final boolean a, b, d;

    public ExampleCode(boolean a, boolean b, boolean d) {
        this.a = a;
        this.b = b;
        this.d = d;
    }

    public void exampleDag(){

        if(A()){

            if(B()){
                C();
            }
        }
        else{
            C();
        }

        if(D()){
            E();
        }

        F();
    }

    private boolean A() { return a; }
    private boolean B() { return b; }
    private void C(){}
    private boolean D() { return d; }
    private void E(){}
    private void F(){}
}
