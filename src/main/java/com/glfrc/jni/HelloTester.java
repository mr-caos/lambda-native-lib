package com.glfrc.jni;

public class HelloTester {

    public static JavaHello hi = new JavaHello();

    public static void main(String[] args) {
        System.out.println(hi.sayHello("pippo"));
        System.out.println(hi.sayHello("pluto"));
    }
}
