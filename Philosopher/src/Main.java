import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;


public class Main {
	
	private static final int THINKING = 0;
	private static final int HUNGRY = 1;
	private static final int EATING = 2;
	private final ReentrantLock lock ;
	private int[] state ;
	private Semaphore s[];
	
	private int n;
	
	public Main(int n) {
		this.n = n;
		lock = new ReentrantLock(true);
		state = new int[n];
		s = new Semaphore[n];
		for(int i = 0 ; i < n ; i++) {
			s[i] = new Semaphore(1);
		}
		
		Executor excutors = Executors.newFixedThreadPool(5);
		for(int i = 0 ; i < n ; i++) {
			excutors.execute(new Philosopher(i));
		}
		
			
	}
	
	public static void main(String[] args) {
		new Main(5);
	}
	
	
	class Philosopher implements Runnable {

		public int i;
		
		public Philosopher(int i) {
			super();
			this.i = i;
		}
		
		

		@Override
		public void run() {
			while(true) {
				think();
				takeChops(i);
				eat();
				putChops(i);
			}
			
		}
		
		public void takeChops(int i) {
			final ReentrantLock l = lock;
			l.lock();
			try {
				state[i] = HUNGRY;
				test(i);
			} finally {
				l.unlock();
			}
			try {
				s[i].acquire(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		public void putChops(int i) {
			final ReentrantLock l = lock;
			l.lock();
			try {
				state[i] = THINKING;
				test((i - 1) % n);
				test((i + 1) % n);
			} finally {
				l.unlock();
			}
		}
		
		public void test(int i) {
			if(state[(i + n) % n] == HUNGRY && state[(i - 1 + n) % n] != EATING && state[(i + 1) % n] != EATING) {
				state[(i + n) % n] = EATING;
				s[(i + n) % n].release(1);
			}
		}
		
		public void think() {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("哲学家 " + i + " 正在思考");
		}
		
		public void eat() {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("哲学家 " + i + " 正在吃饭");
		}
		
	}
}
