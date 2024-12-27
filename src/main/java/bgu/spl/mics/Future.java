package bgu.spl.mics;

import java.util.concurrent.TimeUnit;

/**
 * A Future object represents a promised result - an object that will
 * eventually be resolved to hold a result of some operation. The class allows
 * Retrieving the result once it is available.
 * 
 * Only private methods may be added to this class.
 * No public constructor is allowed except for the empty constructor.
 */
public class Future<T> {
	private boolean isResolved;
	private Object lock;
	private T result; 
	
	/**
	 * This should be the the only public constructor in this class.
	 */
	public Future() {
		isResolved = false;
		lock = new Object();
		result = null;
	}
	
	/**
     * retrieves the result the Future object holds if it has been resolved.
     * This is a blocking method! It waits for the computation in case it has
     * not been completed.
     * <p>
     * @return return the result of type T if it is available, if not wait until it is available.
     * 	       
     */
	public T get() {
		synchronized (lock){
		while (!isResolved){
			try{
				lock.wait();
			}
			catch (InterruptedException ie){
				System.out.println("Get Future linit time Interrupt: " + Thread.currentThread());
				Thread.currentThread().interrupt(); // To do put interrupt somewhere
			}
		}
		return result;
		}
	}
	
	/**
     * Resolves the result of this Future object.
     */
	public void resolve (T result) {
		synchronized(lock){
			if (!isResolved) {
				this.result = result;
				isResolved = true;
				lock.notifyAll();
			}
		}
	}
	
	/**
     * @return true if this object has been resolved, false otherwise
     */
	public boolean isDone() {
		synchronized (lock){
			return isResolved;
		}
	}
	
	/**
     * retrieves the result the Future object holds if it has been resolved,
     * This method is non-blocking, it has a limited amount of time determined
     * by {@code timeout}
     * <p>
     * @param timout 	the maximal amount of time units to wait for the result.
     * @param unit		the {@link TimeUnit} time units to wait.
     * @return return the result of type T if it is available, if not, 
     * 	       wait for {@code timeout} TimeUnits {@code unit}. If time has
     *         elapsed, return null.
     */
	public T get(long timeout, TimeUnit unit) {
		synchronized(lock){
		if (isResolved){
			return result;
		}
		long time = unit.toMillis(timeout);
		long remainingTime = time; 
		long endTime = System.currentTimeMillis() + remainingTime;
		while(remainingTime > 0){
			if (isResolved){
				return result;
			}
			try {
				lock.wait(remainingTime);
			}
			catch (InterruptedException ie){
				System.out.println("Get Future linit time Interrupt: " + Thread.currentThread());
				Thread.currentThread().interrupt(); // To do put interrupt somewhere
			}
			remainingTime = endTime - System.currentTimeMillis(); // to consider transform to another place to be accuratie 
		}
		return null; 
	}
	}
}
