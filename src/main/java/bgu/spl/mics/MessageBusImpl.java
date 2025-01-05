package bgu.spl.mics;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import bgu.spl.mics.application.messages.TickBroadcast;

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

    // to check yellow line
    private final ConcurrentHashMap<Class<? extends Event<?>>, BlockingQueue<MicroService>> eventSubscribers;
    private final ConcurrentHashMap<Class<? extends Broadcast>, BlockingQueue<MicroService>> broadcastSubscribers;
    private final ConcurrentHashMap<MicroService, BlockingQueue<Message>> microServicesQueues;
    private final ConcurrentHashMap<Event<?>, Future<?>> eventAndFutureUnresolved;

    // CopyOnWriteArrayList<MicroService>> broadcastSubscribers
    private MessageBusImpl() {
        eventSubscribers = new ConcurrentHashMap<>(3); // Do it need to be size of 2? because of pose events
        broadcastSubscribers = new ConcurrentHashMap<>(3);
        microServicesQueues = new ConcurrentHashMap<>();
        eventAndFutureUnresolved = new ConcurrentHashMap<>();
    }

    public static MessageBusImpl getInstance() {
        return MessageBusHolder.instance;
    }

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

    @Override
    public <T> void complete(Event<T> e, T result) {
        @SuppressWarnings("unchecked")
        Future<T> future = (Future<T>) eventAndFutureUnresolved.get(e);
        future.resolve(result);
        eventAndFutureUnresolved.remove(e);
    }

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

    // does e is unique??
    // according to what the hash map maps MicroService nameqadreess??
    @Override
    public <T> Future<T> sendEvent(Event<T> e) {
        // To check if possible that there will be something to process but no microservice in queue to handle it
        BlockingQueue<MicroService> eventOptions = eventSubscribers.get(e.getClass());
        // Synchronized eventOptions in case there is only one microservice that can b active 
        synchronized (eventOptions) {
            if (eventOptions != null){
                MicroService m = eventOptions.poll();
                if (m != null) {
                    eventOptions.add(m); // keep round robin
                    Future<T> future = new Future<T>();
                    eventAndFutureUnresolved.put(e, future);
                    try {
                        microServicesQueues.get(m).add(e);
                    }
                    catch (NullPointerException np){
                        System.out.println("You try to send event to unregistered microservice");
                        return null;
                    }
                    return future;
                }
            }
        }
        return null; // In case no micro-service has subscribed
    }

    // Where initialization of events creation and all of those??
    @Override
    public void register(MicroService m) {
        microServicesQueues.put(m, new LinkedBlockingQueue<Message>());
    }

    @Override
    // if necessary to do on all or handle other way
    // Synchronized because we don't want to assign new messages to MicroService m
    public void unregister(MicroService m) {
        BlockingQueue<Message> needToFinished = microServicesQueues.remove(m);
        // Microservice is registered 
        if (needToFinished != null){
            for (Message mes : needToFinished) {
                // We need to resolve events
                if (mes instanceof Event<?>){
                    eventAndFutureUnresolved.get(mes).resolve(null); // We want to resolve any waiting Futures
                    eventAndFutureUnresolved.remove(mes);
                }
            }
        }
        for (BlockingQueue<MicroService> ev : eventSubscribers.values()) {
            synchronized(ev){
                ev.remove(m);
            }
        }
        for (BlockingQueue<MicroService> bc : broadcastSubscribers.values()) {
            synchronized(bc){
                bc.remove(m);
            }
        }
    }

    @Override
    // is this the interupted one? head?
    public Message awaitMessage(MicroService m) throws InterruptedException {
        BlockingQueue<Message> queue = microServicesQueues.get(m);
        if (queue == null) {// to check if can be not null after this line
            throw new IllegalStateException("MicroService not registered: " + m.getName());
        }
        return queue.take(); // Blocks until a message is available
    }

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
