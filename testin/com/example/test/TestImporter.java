package com.example.test;

import com.example.test.sub.TestEnum;

public class TestImporter {

    public static String something;

    static {
        something = "Awesome!";
    }

    public boolean isEven(TestEnum e) {
        return e.equals(TestEnum.TWO);
    }

}