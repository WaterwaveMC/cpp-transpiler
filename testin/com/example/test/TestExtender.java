package com.example.test;

public class TestExtender extends TestAbstract {

    static int a = 5;

    @Override
    public void sayHi() {
        System.out.println("Hi from TestExtender!");
        if(1 + 1 == 2) {
            System.out.println("1 + 1 is 2");
        } else {
            System.out.println("1 + 1 is not 2");
        }
    }

}