package com.example.test;

@Data
public class SimpleClass {

    private int x = 5;
    @Getter
    private String y = "Hello";
    public boolean z = true;
    private Object o;

    public void sayHi() {
        System.out.println("Hi!");
    }

}