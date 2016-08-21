package in.shamit.wikipedia.parse.article.parts.threadpool;
import java.util.concurrent.*;

public class BoundedExecutor {
	   private final Executor exec;
	    private final Semaphore semaphore;
	    public BoundedExecutor(Executor exec, int bound) {
	        this.exec = exec;
	        this.semaphore = new Semaphore(bound);
	    }
	    public void submitTask(final Runnable command)
	            throws InterruptedException {
	        semaphore.acquire();
	        try {
	            exec.execute(new Runnable() {
	                public void run() {
	                    try {
	                        command.run();
	                    } finally {
	                        semaphore.release();
	                    }
	                }
	            });
	        } catch (RejectedExecutionException e) {
	            semaphore.release();
	        }
	    }
}
