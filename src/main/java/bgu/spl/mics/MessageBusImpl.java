package bgu.spl.mics;

import java.util.LinkedList;
import java.util.Queue;
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

	// to check yellow line
	private final ConcurrentHashMap<Class<? extends Event>, BlockingQueue<MicroService>> eventSubscribers;
	private final ConcurrentHashMap<Class<? extends Broadcast>, BlockingQueue<MicroService>> broadcastSubscribers;
	private final ConcurrentHashMap<MicroService, BlockingQueue<Message>> microServicesQueues;
	private final ConcurrentHashMap<Event<?>, Future<?>> eventAndFutureUnresolved;

	// CopyOnWriteArrayList<MicroService>> broadcastSubscribers
	private MessageBusImpl() {
		eventSubscribers = new ConcurrentHashMap<>(3); // do it need to be size of 2? beacuese of pose events
		broadcastSubscribers = new ConcurrentHashMap<>(3);
		microServicesQueues = new ConcurrentHashMap<>();
		eventAndFutureUnresolved = new ConcurrentHashMap<>();
	}

	public static MessageBusImpl getIstance() {
		return MessageBusHolder.instance;
	}

	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		eventSubscribers.putIfAbsent(type, new LinkedBlockingQueue<>()); // If specified key is not already associated
																			// with a value, associate it with the given
																			// value atomicly
		eventSubscribers.get(type).add(m);
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		broadcastSubscribers.putIfAbsent(type, new LinkedBlockingQueue<>());// If specified key is not already
																			// associated with a value, associate it
																			// with the given value atomicly
		broadcastSubscribers.get(type).add(m);
	}

	@Override
	public <T> void complete(Event<T> e, T result) {
		Future<T> future = (Future<T>) eventAndFutureUnresolved.get(e);
		future.resolve(result);
		eventAndFutureUnresolved.remove(e);
	}

	@Override
	public void sendBroadcast(Broadcast b) {
		for (MicroService ms : broadcastSubscribers.get(b.getClass())) {
			microServicesQueues.get(ms).add(b);
		}
	}

	// does e is unique??
	// according to what the hash map maps MicroService nameqadreess??
	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		Future<T> future = new Future<T>();
		eventAndFutureUnresolved.put(e, future);
		BlockingQueue<MicroService> eventOptions = eventSubscribers.get(e.getClass());
		// To check if possible that there will be something to process but no
		// microservice in queue to handle it
		synchronized (eventOptions) {
			MicroService m = eventOptions.poll();
			if (m != null) {
				eventOptions.add(m); // keep round robin
				microServicesQueues.get(m).add(e);
			}
		}
		
		return future;
	}

	// where intitilization of events creation and all of those??
	@Override
	public void register(MicroService m) {
		microServicesQueues.put(m, new LinkedBlockingQueue<Message>());
	}

	@Override
	// if neccecry to do on all or handle other way
	// Synchronized becasue we don't want to assign new messeges to MicroService m
	public synchronized void unregister(MicroService m) {
		if (microServicesQueues.containsKey(m)) {
			for (BlockingQueue<MicroService> ev : eventSubscribers.values()) {
				ev.remove(m);
			}
			for (BlockingQueue<MicroService> bc : broadcastSubscribers.values()) {
				bc.remove(m);
			}
			for (Message mes : microServicesQueues.get(m)) {
				// if neccesery???
				eventAndFutureUnresolved.get(mes).resolve(null); // We want to resolve any waiting Futures
				eventAndFutureUnresolved.remove(mes);
			}
			microServicesQueues.remove(m);
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
}
