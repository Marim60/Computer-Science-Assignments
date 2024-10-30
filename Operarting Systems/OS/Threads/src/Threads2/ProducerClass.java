package Threads2;

import Threads2.Buffer;

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
			while (i <= numbers) {
				buffer.produce(i++);
				if (i == numbers)
					buffer.flag = true;
			}

		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}