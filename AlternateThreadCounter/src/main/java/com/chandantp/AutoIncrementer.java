package com.chandantp;

public class AutoIncrementer implements Runnable {

    private static int MAX_COUNT = 10;

    private Counter counter;
    private boolean startFirst = false;

    AutoIncrementer(Counter counter, boolean startFirst) {
        this.counter = counter;
        this.startFirst = startFirst;
    }

    @Override
    public void run() {
        String name = Thread.currentThread().getName();

        while (true) {
            synchronized (counter) {
                counter.increment();
                System.out.println("Thread " + name + " : count = " + counter.get());

                if (counter.get() >= MAX_COUNT) {
                    System.exit(0);
                }

                try {
                    counter.notify();
                    counter.wait();
                } catch (InterruptedException e) {
                    System.out.println("Thread " + name + " : wait interrupted!");
                }
            }
        }
    }
}
