package Threads;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

import static java.lang.Thread.sleep;

public class Main {
    static Buffer buffer;
    public void consume(Buffer B)
    {
        buffer = B ;
        Consumer consumer = new Consumer(buffer);
        Thread c1 = new Thread(consumer);
        c1.start();

    }

}

