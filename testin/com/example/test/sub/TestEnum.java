package com.example.test.sub;

public enum TestEnum {
    ONE(1), TWO(2), THREE(3);

    private final int wow;
    public TestEnum(int a) {
        this.wow = a + 10;
    }

    public int getWow() {
        return wow;
    }
}