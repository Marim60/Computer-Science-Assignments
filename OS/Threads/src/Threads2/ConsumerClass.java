package Threads2;

import Threads2.Buffer;

import java.io.FileNotFoundException;

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