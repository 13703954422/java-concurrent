import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;


public class Main {
	
	private final ReentrantLock lock ;   //定义锁
	private static int readCount = 0;    //读者的数量
	private Semaphore writeSemaphore ;   //写信号量

	public Main() {
		lock = new ReentrantLock();
		writeSemaphore = new Semaphore(1);
	}
	
	public static void main(String[] args) {
		Main main = new Main();
		Executor executors = Executors.newFixedThreadPool(4);
		executors.execute(main.new Reader());
		executors.execute(main.new Reader());
		executors.execute(main.new Writer());
		executors.execute(main.new Reader());
		
	}
	
	
	
	
	class Reader implements Runnable {

		@Override
		public void run() {
			before();             //读操作之前的操作
			read();               //读操作
			after();             //读操作之后的操作
		}
		
		public void before() {    //读操作之前的操作
			final ReentrantLock l = lock;
			l.lock();
			try {
				if(readCount == 0) {   //当有读者时，写者不能进入
					writeSemaphore.acquire(1);
				}
				readCount += 1;
				System.out.println("有1位读者进入");
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
				l.unlock();
			}
		}
		
		public void read() {         //读操作
			System.out.println("当前有 " + readCount + " 位读者");
		}
		
		public void after() {        //读操作之后的操作
			final ReentrantLock l = lock;
			l.lock();
			try {
				readCount -= 1;
				System.out.println("有1位读者离开" );
				if(readCount == 0)     //当读者为0时，写者才可以进入  
					writeSemaphore.release(1);
				
			} finally {
				l.unlock();
			}
		}
		
	}
	
	class Writer implements Runnable {

		@Override
		public void run() {
			final ReentrantLock l = lock;
			l.lock();
			try {
				try {
					writeSemaphore.acquire(1);     //同时只有一个写者可以进入
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.println("写者正在写");
				writeSemaphore.release(1);
			}  finally {
				l.unlock();
			}
			
		}
		
	}
}
