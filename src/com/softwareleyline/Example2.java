package com.softwareleyline;

/**
 * Created by Geoff on 5/2/2016.
 */
@SuppressWarnings("WeakerAccess")
public class Example2 {

    private final int firstBranch;

    public Example2(){
        this(1);
    }

    public Example2(int firstBranch){

        this.firstBranch = firstBranch;
    }

    public void runDAG(){

        switch(first()){
            case 0: A(); break;
            case 1: B(); break;
            case 2: C(); break;
        }

        collector();
    }

    int first(){
        return firstBranch;
    }

    void A(){}
    void B(){}
    void C(){}

    void collector(){}


}
