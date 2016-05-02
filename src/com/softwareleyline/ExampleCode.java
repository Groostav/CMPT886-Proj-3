package com.softwareleyline;

/**
 * Created by Geoff on 4/19/2016.
 */
public class ExampleCode {

    private final boolean a, b, d;

    public ExampleCode(){
        this(true, true, true);
    }

    public ExampleCode(boolean a, boolean b, boolean d) {
        this.a = a;
        this.b = b;
        this.d = d;
    }

    public void runDAG(){

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

    public boolean A() {
        Assig3DriverKt.hit("A");
        return a;
    }
    public boolean B() { return b; }
    public void C(){}
    public boolean D() { return d; }
    public void E(){}
    public void F(){}
}
