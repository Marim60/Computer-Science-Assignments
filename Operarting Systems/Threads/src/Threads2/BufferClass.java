package Threads2;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

class Buffer {
	public Queue<Integer> bufferlist = new LinkedList<Integer>();
	public int max_prime_number = 0;
	private int count_prime_numbers = 0;
	private int sizeOfBuffer = -1;
	public boolean flag = false;
	public boolean flag2 = false;
	FileWriter file;
	private long end = 0;

	Buffer(int sizeOfBuffer, String f) {
		try {
			file = new FileWriter(f);
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.sizeOfBuffer = sizeOfBuffer;
	}

	public long get_end() {
		return end;
	}

	public int get_count_prime_numbers() {
		return count_prime_numbers;
	}

	public boolean isPrime(int num) {
		if (num == 0 || num == 1)
			return false;
		for (int i = 2; i * i <= num; ++i)
			if (num % i == 0)
				return false;
		return true;
	}

	public void produce(int i) throws InterruptedException {
		synchronized (this) {
			while (bufferlist.size() >= sizeOfBuffer) {
				wait();
			}
			if (isPrime(i)) {
				bufferlist.add(i);
				max_prime_number = i;
				count_prime_numbers++;
			}
			notify();
		}
	}

	public void consume() throws InterruptedException, FileNotFoundException {
		while (true) {
			synchronized (this) {
				while (bufferlist.size() == 0) {
					if (flag) {
						flag2 = true;
						break;
					}
					wait();
				}
				if (bufferlist.peek() != null) {
					int num = bufferlist.poll();
					try {
						file.write("\"" + num + "\",");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				notify();
			}
			if (flag2) {
				try {
					file.close();
				    end = System.currentTimeMillis();
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			}

		}

	}
}