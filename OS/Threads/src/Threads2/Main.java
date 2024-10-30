package Threads2;

import Threads2.Buffer;
import Threads2.Consumer;
import Threads2.Producer;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

public class Main {
	static Buffer buffer;
	static long start = 0;
	public int get_Max_Prime() {
		return buffer.max_prime_number;
	}

	public int get_Count_Prime_Numbers() {
		return buffer.get_count_prime_numbers();
	}

	public boolean get_flag2()
	{
		return buffer.flag2;
	}
	public String get_time() {
		NumberFormat formatter = new DecimalFormat("0.000000");
		String S = formatter.format((buffer.get_end() - start) / 1000d) + "ms";
		return S;
	}

	public void Work(int value, int b, String s) throws InterruptedException {
		try (Scanner input = new Scanner(System.in)) {
			// System.out.print("Enter Name File : ");
			String f = s;
			// System.out.print("Enter the buffer size: ");
			int sizeOfBuffer = b;
			// System.out.print("Enter numbers: ");
			int numbers = value;
			buffer = new Buffer(sizeOfBuffer, f);
			Producer producer = new Producer(buffer, numbers);
			Consumer consumer = new Consumer(buffer);
			Thread p1 = new Thread(producer);
			Thread c1 = new Thread(consumer);
			Thread c2 = new Thread(consumer);
			Thread c3 = new Thread(consumer);
			start = System.currentTimeMillis();
			p1.start();
			c1.start();
			p1.join();
			c1.join();
			c2.start();
			c2.join();
			c3.start();
			c3.join();
		}
	}

}