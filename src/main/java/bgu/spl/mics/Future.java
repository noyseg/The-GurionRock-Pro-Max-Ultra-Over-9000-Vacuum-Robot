package bgu.spl.mics;

import java.util.concurrent.TimeUnit;

public class Future<T> {
    private T result; // The result of the computation
    private boolean isResolved; // Tracks whether the result is resolved
    private final Object lock = new Object(); // Used for thread synchronization

    /**
     * This should be the only public constructor in this class.
     */
    public Future() {
        this.result = null;
        this.isResolved = false;
    }

    /**
     * Retrieves the result the Future object holds if it has been resolved.
     * This is a blocking method! It waits for the computation in case it has
     * not been completed.
     *
     * @return return the result of type T if it is available, if not wait until it is available.
     */
    public T get() {
        synchronized (lock) {
            while (!isResolved) {
                try {
                    lock.wait(); // Wait until the result is resolved
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); // Preserve interruption status
                }
            }
            return result;
        }
    }

    /**
     * Resolves the result of this Future object.
     *
     * @param result The result to be set.
     */
    public void resolve(T result) {
        synchronized (lock) {
            if (!isResolved) {
                this.result = result;
                this.isResolved = true;
                lock.notifyAll(); // Notify all waiting threads
            }
        }
    }

    /**
     * @return true if this object has been resolved, false otherwise
     */
    public boolean isDone() {
        synchronized (lock) {
            return isResolved;
        }
    }

    /**
     * Retrieves the result the Future object holds if it has been resolved.
     * This method is non-blocking; it has a limited amount of time determined
     * by {@code timeout}.
     *
     * @param timeout the maximal amount of time units to wait for the result.
     * @param unit    the {@link TimeUnit} time units to wait.
     * @return return the result of type T if it is available, if not,
     * wait for {@code timeout} TimeUnits {@code unit}. If time has
     * elapsed, return null.
     */
    public T get(long timeout, TimeUnit unit) {
        synchronized (lock) {
            if (isResolved) {
                return result;
            }

            try {
                long timeoutMillis = unit.toMillis(timeout);
                lock.wait(timeoutMillis); // Wait for the result or timeout
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Preserve interruption status
            }

            return isResolved ? result : null; // Return result if resolved, else null
        }
    }
}
