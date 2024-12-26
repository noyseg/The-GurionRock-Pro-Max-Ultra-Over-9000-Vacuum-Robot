package bgu.spl.mics;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;


/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only one public method (in addition to getters which can be public solely for unit testing) may be added to this class
 * All other methods and members you add the class must be private.
 */
public class MessageBusImpl implements MessageBus {
	private static MessageBusImpl instance = new MessageBusImpl();
	// to check yellow line 
	private final ConcurrentHashMap<Class<? extends Event>, BlockingQueue<MicroService>> eventSubscribers;
	private final ConcurrentHashMap<Class<? extends Broadcast>, BlockingQueue<MicroService>> broadcastSubscribers;
	private final ConcurrentHashMap<MicroService,BlockingQueue<Message>> callBacksAwait;
	private final ConcurrentHashMap<Event<?>, Future<?>> eventAndFutureUnresolved;

	// CopyOnWriteArrayList<MicroService>> broadcastSubscribers
	private MessageBusImpl(){
		eventSubscribers = new ConcurrentHashMap<>(3); //do it need to be size of 2? beacuese of pose events 
		broadcastSubscribers = new ConcurrentHashMap<>(3);
		callBacksAwait = new ConcurrentHashMap<>();
		eventAndFutureUnresolved = new ConcurrentHashMap<>();
	}

	public MessageBusImpl getIstance(){
		return instance;
	}

	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		if (eventSubscribers.get(type) == null){
			eventSubscribers.put(type, new LinkedBlockingQueue<>());
		}
		eventSubscribers.get(type).add(m); 
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		if (broadcastSubscribers.get(type) == null){
			broadcastSubscribers.put(type, new LinkedBlockingQueue<>());
		}
		broadcastSubscribers.get(type).add(m); 
	}

	@Override
	public <T> void complete(Event<T> e, T result) {
		Future<T> future = (Future<T>)eventAndFutureUnresolved.get(e);
		if (future != null){
			future.resolve(result);
			eventAndFutureUnresolved.remove(e);
		}
		
	}

	@Override
	// not to have a problem with time service and terminated!!!!! 
	public void sendBroadcast(Broadcast b) {
		for (MicroService ms: broadcastSubscribers.get(b.getClass())){
			callBacksAwait.get(ms).add(b); // chat did offer and asked null in neccesery 
		}
	}
    // does e is unique?? 
	// according to what the hash map maps MicroService nameqadreess?? 	
	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		LinkedList<MicroService> eventOptions = eventSubscribers.get(e.getClass());
		// what happens to the other thread??????
		synchronized (eventOptions){
		MicroService m = eventOptions.removeFirst();
		eventOptions.addLast(m); // keep round robin 
		callBacksAwait.get(m).add(e);
		}
		// Event doesn't already exists 
		Future<T> future = new Future<T>();
		eventAndFutureUnresolved.put(e,future);
		return future;
	}

	// where intitilization of events creation and all of those?? 
	@Override
	public void register(MicroService m) {
		callBacksAwait.put(m,new LinkedBlockingQueue<Message>());

	}

	@Override
	public void unregister(MicroService m) {
		// TODO Auto-generated method stub

	}

	@Override
	// is this the interupted one? head? 
	public Message awaitMessage(MicroService m) throws InterruptedException {
		BlockingQueue<Message> queue = callBacksAwait.get(m);
        if (queue == null) {
            throw new IllegalStateException("MicroService not registered: " + m.getName());
        }
        return queue.take(); // Blocks until a message is available
	}
}
