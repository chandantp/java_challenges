package com.chandantp;

public class App {

    public static void main(String[] args) throws Exception {

        Counter counter = new Counter();
        Thread inc1 = new Thread(new AutoIncrementer(counter, true), "T1");
        Thread inc2 = new Thread(new AutoIncrementer(counter, false), "T2");

        inc1.start();
        inc2.start();

        inc1.join();
        inc2.join();

        System.out.println("Program exit!");
    }
}
