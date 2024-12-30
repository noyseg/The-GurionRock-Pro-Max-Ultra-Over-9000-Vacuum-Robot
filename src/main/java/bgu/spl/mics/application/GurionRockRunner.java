package bgu.spl.mics.application;

/**
 * The main entry point for the GurionRock Pro Max Ultra Over 9000 simulation.
 * <p>
 * This class initializes the system and starts the simulation by setting up
 * services, objects, and configurations.
 * </p>
 */
public class GurionRockRunner {

    /**
     * The main method of the simulation.
     * This method sets up the necessary components, parses configuration files,
     * initializes services, and starts the simulation.
     *
     * @param args Command-line arguments. The first argument is expected to be the path to the configuration file.
     */
    public static void main(String[] args) {
        System.out.println("Starting the GurionRock Simulation!");

        // Example of thread creation
        Thread t1 = new Thread(() -> {
            try {
                System.out.println("Thread 1 is running...");
                Thread.sleep(500); // Simulating some work
            } catch (InterruptedException e) {
                System.out.println("Thread 1 was interrupted.");
            }
        }, "Thread-1");

        Thread t2 = new Thread(() -> {
            try {
                System.out.println("Thread 2 is running...");
                Thread.sleep(500); // Simulating some work
            } catch (InterruptedException e) {
                System.out.println("Thread 2 was interrupted.");
            }
        }, "Thread-2");

        // Start threads
        t1.start();
        t2.start();

        // Wait for threads to complete or simulate waiting
        try {
            t1.join(1000); // Wait for t1 to finish
            t2.join(1000); // Wait for t2 to finish
        } catch (InterruptedException e) {
            System.out.println("Main thread was interrupted while waiting for child threads.");
        }

        // Interrupt threads as needed (if they were still running)
        t1.interrupt();
        t2.interrupt();

        System.out.println("Simulation completed. Goodbye!");


        // TODO: Parse configuration file.
        // TODO: Initialize system components and services.
        // TODO: Start the simulation.
    }
}
