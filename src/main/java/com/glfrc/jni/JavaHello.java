package com.glfrc.jni;

public class JavaHello {
    static {
        System.loadLibrary("hello"); // loads libhello.so
    }

    public native String sayHello(String name);
}
