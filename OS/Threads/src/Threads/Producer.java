package Threads;
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//


import static java.lang.Thread.sleep;

class Producer implements Runnable {
    private Buffer buffer;
    private int numbers;

    Producer(Buffer buffer, int numbers) {
        this.buffer = buffer;
        this.numbers = numbers;
    }

    @Override
    public void run() {
        try {
            int i = 2;
            while (i <= numbers+1) {

            synchronized (this) {
                if (i == numbers + 1) {
                buffer.flag = true;
                 break;
                }
}
                buffer.produce(i++);
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}