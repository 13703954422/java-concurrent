import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class PM {
	
	private Pool<Student> pools = new Pool<Student>(1000);
	
	public static void main(String[] args) {
		PM pm = new PM();
		ExecutorService executor = Executors.newFixedThreadPool(6); 
		executor.execute(pm.new consume());
		executor.execute(pm.new consume());
		executor.execute(pm.new consume());
		executor.execute(pm.new produce());
		executor.execute(pm.new produce());
		executor.execute(pm.new produce());
	}
	
	 class produce implements Runnable {

		@Override
		public void run() {
			while(true) {
				try {
					pools.offer(new Student());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
		}
		
	}

	class consume implements Runnable {

		@Override
		public void run() {
			while(true) {
				try {
					pools.poll();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
	}

}


