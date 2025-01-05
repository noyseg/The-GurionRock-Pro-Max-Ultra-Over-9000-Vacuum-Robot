package bgu.spl.mics;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;


/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus
 * interface.
 * Write your implementation here!
 * Only one public method (in addition to getters which can be public solely for
 * unit testing) may be added to this class
 * All other methods and members you add the class must be private.
 */
public class MessageBusImpl implements MessageBus {
    private static class MessageBusHolder {
        private static MessageBusImpl instance = new MessageBusImpl();
    }

    private final ConcurrentHashMap<Class<? extends Event<?>>, BlockingQueue<MicroService>> eventSubscribers;
    private final ConcurrentHashMap<Class<? extends Broadcast>, BlockingQueue<MicroService>> broadcastSubscribers;
    private final ConcurrentHashMap<MicroService, BlockingQueue<Message>> microServicesQueues;
    private final ConcurrentHashMap<Event<?>, Future<?>> eventAndFutureUnresolved;

    /**
     * Constructs a new instance of MessageBusImpl.
     * Initializes the internal data structures used for managing event and broadcast subscriptions,
     * microservice message queues, and unresolved event futures.
     */
    private MessageBusImpl() {
        eventSubscribers = new ConcurrentHashMap<>(3);
        broadcastSubscribers = new ConcurrentHashMap<>(3);
        microServicesQueues = new ConcurrentHashMap<>();
        eventAndFutureUnresolved = new ConcurrentHashMap<>();
    }

    public static MessageBusImpl getInstance() {
        return MessageBusHolder.instance;
    }

    /**
     * Subscribes a microservice to a specific type of event.
     * This allows the microservice to receive events of the specified type.
     *
     * @param <T>  the type of the result expected from the event
     * @param type the class type of the event to subscribe to.
     *             It must be a class that implements the Event interface.
     * @param m    the microservice that wants to subscribe to the event.
     *             It must be previously registered with the message bus.
     * @throws IllegalStateException if the microservice is not registered.
     */
    @Override
    public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
        //Checking if microservice is already registered
        if (microServicesQueues.get(m) != null) {
            eventSubscribers.putIfAbsent(type, new LinkedBlockingQueue<>()); // If specified key is not already associated
            // with a value, associate it with the given
            if (!eventSubscribers.get(type).contains(m)) {
                eventSubscribers.get(type).add(m);
            }
        } else {
            throw new IllegalStateException("Microservice is not registered");
        }
    }

    /**
     * Subscribes a microservice to a specific type of broadcast message.
     * This allows the microservice to receive all broadcast messages of the specified type.
     *
     * @param type the class type of the broadcast messages to subscribe to.
     *             It must be a class that implements the Broadcast interface.
     * @param m    the microservice that wants to subscribe to the broadcast messages.
     *             It must be previously registered with the message bus.
     * @throws IllegalStateException if the microservice is not registered.
     */
    @Override
    public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
        if (microServicesQueues.get(m) != null) {
            broadcastSubscribers.putIfAbsent(type, new LinkedBlockingQueue<>());// If specified key is not already
            // associated with a value, associate it
            // with the given value atomicly
            if (!broadcastSubscribers.get(type).contains(m)) {
                broadcastSubscribers.get(type).add(m);
            }
        } else {
            throw new IllegalStateException("Microservice is not registered");
        }
    }

    /**
     * Completes the processing of a given event by resolving its associated Future object with the provided result.
     *
     * @param <T>    the type of the result expected from the event
     * @param e      the event whose processing is to be completed
     * @param result the result to resolve the Future object associated with the event
     */
    @Override
    public <T> void complete(Event<T> e, T result) {
        @SuppressWarnings("unchecked")
        Future<T> future = (Future<T>) eventAndFutureUnresolved.get(e);
        future.resolve(result);
        eventAndFutureUnresolved.remove(e);
    }

    /**
     * Sends a broadcast message to all microservices subscribed to the broadcast type.
     *
     * @param b the broadcast message to be sent. It must be an instance of a class
     *          that implements the Broadcast interface.
     */
    @Override
    public void sendBroadcast(Broadcast b) {
        for (MicroService ms : broadcastSubscribers.get(b.getClass())) {
            try {
                    microServicesQueues.get(ms).add(b);
                }
                catch (NullPointerException np){
                    System.out.println("You try to send brodcast to unregistered microservice");
                }
            }
        }

    /**
     * Sends an event to one of the subscribed microservices in a round-robin fashion.
     * If no microservice is subscribed to the event type, the event is not sent.
     *
     * @param <T> the type of the result expected from the event
     * @param e   the event to be sent
     * @return a Future object that will be resolved once the event is processed,
     * or null if no microservice is subscribed to the event type
     */
    @Override
    public <T> Future<T> sendEvent(Event<T> e) {
        BlockingQueue<MicroService> eventOptions = eventSubscribers.get(e.getClass());
        // Synchronized eventOptions in case there is only one microservice that can b active
        synchronized (eventOptions) {
            if (eventOptions != null) {
                MicroService m = eventOptions.poll();
                if (m != null) {
                    eventOptions.add(m); // keep round robin
                    Future<T> future = new Future<T>();
                    eventAndFutureUnresolved.put(e, future);
                    try {
                        microServicesQueues.get(m).add(e);
                    } catch (NullPointerException np) {
                        System.out.println("You try to send event to unregistered microservice");
                        return null;
                    }
                    return future;
                }
            }
        }
        return null; // In case no micro-service has subscribed
    }

    /**
     * Registers a microservice to the message bus, allowing it to send and receive messages.
     *
     * @param m the microservice to be registered
     * @throws IllegalStateException if the microservice is already registered
     */
    @Override
    public void register(MicroService m) {
        microServicesQueues.put(m, new LinkedBlockingQueue<Message>());
    }

    @Override
    /**
     * Unregisters a microservice from the message bus, removing it from all
     * subscriptions and resolving any pending events with null.
     *
     * @param m the microservice to be unregistered. It must be previously registered
     *          with the message bus.
     */
    public void unregister(MicroService m) {
        BlockingQueue<Message> needToFinished = microServicesQueues.remove(m);
        // Microservice is registered
        if (needToFinished != null) {
            for (Message mes : needToFinished) {
                // We need to resolve events
                if (mes instanceof Event<?> && eventAndFutureUnresolved.get(mes)!=null) {
                    eventAndFutureUnresolved.get(mes).resolve(null); // We want to resolve any waiting Futures
                    eventAndFutureUnresolved.remove(mes);
                }
            }
        }
        for (BlockingQueue<MicroService> ev : eventSubscribers.values()) {
            synchronized (ev) {
                ev.remove(m);
            }
        }
        for (BlockingQueue<MicroService> bc : broadcastSubscribers.values()) {
            synchronized (bc) {
                bc.remove(m);
            }
        }
    }

    @Override
    /**
     * Retrieves and removes the head of the message queue for the specified microservice,
     * waiting if necessary until a message becomes available.
     *
     * @param m the microservice whose message queue is to be accessed.
     *          It must be previously registered with the message bus.
     * @return the head message from the microservice's queue.
     * @throws InterruptedException if interrupted while waiting for a message.
     * @throws IllegalStateException if the microservice is not registered.
     */
    public Message awaitMessage(MicroService m) throws InterruptedException {
        BlockingQueue<Message> queue = microServicesQueues.get(m);
        if (queue == null) {// to check if can be not null after this line
            throw new IllegalStateException("MicroService not registered: " + m.getName());
        }
        return queue.take(); // Blocks until a message is available
    }

    /**
     * Getters for testing purpose.
     */
    public ConcurrentHashMap<MicroService, BlockingQueue<Message>> getMicroServicesQueues() {
        return this.microServicesQueues;
    }

    public ConcurrentHashMap<Class<? extends Event<?>>, BlockingQueue<MicroService>> getEventSubscribers() {
        return this.eventSubscribers;
    }

    public ConcurrentHashMap<Class<? extends Broadcast>, BlockingQueue<MicroService>> getBroadcastSubscribers() {
        return this.broadcastSubscribers;
    }

    public ConcurrentHashMap<Event<?>, Future<?>> getEventAndFutureUnresolved() {
        return this.eventAndFutureUnresolved;
    }
}
