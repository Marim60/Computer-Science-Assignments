package Threads;

import java.io.FileNotFoundException;
import java.util.Queue;

class Consumer implements Runnable {
    Buffer buffer;
    Consumer(Buffer buffer) {
        this.buffer = buffer;
    }
    @Override
    public void run() {
        try {

            buffer.consume();

        } catch (InterruptedException | FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}