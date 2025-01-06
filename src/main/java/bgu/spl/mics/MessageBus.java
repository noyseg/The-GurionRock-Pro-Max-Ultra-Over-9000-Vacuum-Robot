package bgu.spl.mics;

/**
 * The message-bus is a shared object used for communication between
 * micro-services.
 * It should be implemented as a thread-safe singleton.
 * The message-bus implementation must be thread-safe as
 * it is shared between all the micro-services in the system.
 * You must not alter any of the given methods of this interface.
 * You cannot add methods to this interface.
 */
public interface MessageBus {

    /**
     * Subscribes {@code m} to receive {@link Event}s of type {@code type}.
     * <p>
     * 
     * @param <T>  The type of the result expected by the completed event.
     * @param type The type to subscribe to,
     * @param m    The subscribing micro-service.
     * 
     * @invariant All registered microservices have a non-null, allocated message queue.
     * @invariant Subscribed microservices for events and broadcasts are properly tracked.
     * @invariant Futures associated with unresolved events are properly stored and managed.
     * @pre: type != null
     * @pre: m != null
     * @pre: The MicroService is registered.
     * @post: The MicroService will receive all events of the specified type.
     */
    <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m);

    /**
     * Subscribes {@code m} to receive {@link Broadcast}s of type {@code type}.
     * <p>
     * 
     * @param type The type to subscribe to.
     * @param m    The subscribing micro-service.
     * @pre b != null
     * @pre The MicroService is registered.
     * @post The MicroService will receive all broadcasts of the specified type.
     */
    void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m);

    /**
     * Notifies the MessageBus that the event {@code e} is completed and its
     * result was {@code result}.
     * When this method is called, the message-bus will resolve the {@link Future}
     * object associated with {@link Event} {@code e}.
     * <p>
     * 
     * @param <T>    The type of the result expected by the completed event.
     * @param e      The completed event.
     * @param result The resolved result of the completed event.
     * @pre: e != null
     * @pre: A valid unresolved Future is associated with the event.
     * @post: The event's Future is resolved with the provided result.
     * @post: Any subsequent calls to the Future's `get()` method will return the
     *        result.
     */
    <T> void complete(Event<T> e, T result);

    /**
     * Adds the {@link Broadcast} {@code b} to the message queues of all the
     * micro-services subscribed to {@code b.getClass()}.
     * <p>
     * 
     * @param b The message to added to the queues.
     * @pre: b != null
     * @pre: At least one MicroService is subscribed to the type of broadcast.
     * @post: All subscribed MicroServices for this type of broadcast will receive
     *        the message.
     */
    void sendBroadcast(Broadcast b);

    /**
     * Adds the {@link Event} {@code e} to the message queue of one of the
     * micro-services subscribed to {@code e.getClass()} in a round-robin
     * fashion. This method should be non-blocking.
     * <p>
     * 
     * @param <T> The type of the result expected by the event and its corresponding
     *            future object.
     * @param e   The event to add to the queue.
     * @return {@link Future<T>} object to be resolved once the processing is
     *         complete,
     *         null in case no micro-service has subscribed to {@code e.getClass()}.
     * @pre: e != null
     * @pre: At least one MicroService is subscribed to the type of event, or null
     *       will be returned.
     * @post: If a MicroService is subscribed, it will receive the event in a
     *        round-robin manner.
     * @post: A Future object is returned to the sender for tracking the event's
     *        result.
     */
    <T> Future<T> sendEvent(Event<T> e);

    /**
     * Allocates a message-queue for the {@link MicroService} {@code m}.
     * <p>
     * 
     * @param m the micro-service to create a queue for.
     * @pre: m != null.
     * @pre: The MicroService is not already registered.
     * @post: The MicroService can now subscribe to events and broadcasts,
     *        and send or receive messages.
     */
    void register(MicroService m);

    /**
     * Removes the message queue allocated to {@code m} via the call to
     * {@link #register(bgu.spl.mics.MicroService)} and cleans all references
     * related to {@code m} in this message-bus. If {@code m} was not
     * registered, nothing should happen.
     * <p>
     * 
     * @param m the micro-service to unregister.
     * @pre m != null
     * @pre The MicroService is registered.
     * @post The MicroService is no longer subscribed to any events or broadcasts.
     * @post All unresolved Futures associated with the MicroService are resolved
     *       with null.
     */
    void unregister(MicroService m);

    /**
     * Using this method, a <b>registered</b> micro-service can take message
     * from its allocated queue.
     * This method is blocking meaning that if no messages
     * are available in the micro-service queue it
     * should wait until a message becomes available.
     * The method should throw the {@link IllegalStateException} in the case
     * where {@code m} was never registered.
     * <p>
     * 
     * @param m The micro-service requesting to take a message from its message
     *          queue.
     * @return The next message in the {@code m}'s queue (blocking).
     * @throws InterruptedException if interrupted while waiting for a message
     *                              to became available.
     * @pre m != null
     * @pre The MicroService is registered.
     * @post The returned message is removed from the MicroService's queue.
     */
    Message awaitMessage(MicroService m) throws InterruptedException;

}
