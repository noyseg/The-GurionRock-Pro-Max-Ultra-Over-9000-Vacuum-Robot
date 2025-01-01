package bgu.spl.mics;

import bgu.spl.mics.application.messages.PoseEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.Pose;
import bgu.spl.mics.application.services.TimeService;
import bgu.spl.mics.example.messages.ExampleBroadcast;
import bgu.spl.mics.example.messages.ExampleEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.*;

class MessageBusTest {
    private MessageBusImpl messageBus;
    private MicroService testMicroService;
    private PoseEvent poseEvent;
    private TickBroadcast tickBroadcast;

    @BeforeEach
    void setUp() {
        messageBus = MessageBusImpl.getIstance(); // Create a fresh instance
        testMicroService = new TimeService(4, 3);
        poseEvent = new PoseEvent(new Pose(1.0f, 1.0f, 30.0f, 5), "Test");
        tickBroadcast = new TickBroadcast("TimeService", 5);

    }


    @Test
    void subscribeEvent() {
        // Test subscribing to an event
        messageBus.register(testMicroService);
        messageBus.subscribeEvent(ExampleEvent.class, testMicroService);
        assertTrue(messageBus.getEventSubscribers().get(ExampleEvent.class).contains(testMicroService));

        // Test subscribing to the same event multiple times
        messageBus.subscribeEvent(ExampleEvent.class, testMicroService);
        assertEquals(1, messageBus.getEventSubscribers().get(ExampleEvent.class).size());

        // Test subscribing to multiple events

        messageBus.subscribeEvent(PoseEvent.class, testMicroService);
        assertTrue(messageBus.getEventSubscribers().get(PoseEvent.class).contains(testMicroService));

        // Test subscribing multiple MicroServices to the same event
        MicroService anotherService = new TimeService(2, 2);
        messageBus.register(anotherService);
        messageBus.subscribeEvent(ExampleEvent.class, anotherService);
        assertTrue(messageBus.getEventSubscribers().get(ExampleEvent.class).contains(anotherService));
        assertEquals(2, messageBus.getEventSubscribers().get(ExampleEvent.class).size());

        // Test subscribing to an event without being registered
        MicroService unregisteredService = new TimeService(1, 1);
        assertThrows(IllegalStateException.class, () -> messageBus.subscribeEvent(ExampleEvent.class, unregisteredService));

//        // Test subscribing to a null event
//        assertThrows(IllegalArgumentException.class, () -> messageBus.subscribeEvent(null, testMicroService));
//
//        // Test subscribing a null MicroService
//        assertThrows(IllegalArgumentException.class, () -> messageBus.subscribeEvent(ExampleEvent.class, null));

        // Test subscribing after unregistering
        messageBus.unregister(testMicroService);
        assertThrows(IllegalStateException.class, () -> messageBus.subscribeEvent(ExampleEvent.class, testMicroService));

        // Test concurrent subscription
        int threadCount = 10;
        CountDownLatch latch = new CountDownLatch(threadCount);
        for (int i = 0; i < threadCount; i++) {
            new Thread(() -> {
                MicroService service = new TimeService(1, 1);
                messageBus.register(service);
                messageBus.subscribeEvent(ExampleEvent.class, service);
                latch.countDown();
            }).start();
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            fail("Thread interruption occurred");
        }
        assertEquals(threadCount + 1, messageBus.getEventSubscribers().get(ExampleEvent.class).size());
    }

    @Test
    void subscribeBroadcast() {
        // Test subscribing to a broadcast
        messageBus.register(testMicroService);
        messageBus.subscribeBroadcast(ExampleBroadcast.class, testMicroService);
        assertTrue(messageBus.getBroadcastSubscribers().get(ExampleBroadcast.class).contains(testMicroService));

        // Test subscribing to the same broadcast multiple times
        messageBus.subscribeBroadcast(ExampleBroadcast.class, testMicroService);
        assertEquals(1, messageBus.getBroadcastSubscribers().get(ExampleBroadcast.class).size());

        // Test subscribing to multiple broadcasts
        messageBus.subscribeBroadcast(TickBroadcast.class, testMicroService);
        assertTrue(messageBus.getBroadcastSubscribers().get(TickBroadcast.class).contains(testMicroService));

        // Test subscribing multiple MicroServices to the same broadcast
        MicroService anotherService = new TimeService(2, 2);
        messageBus.register(anotherService);
        messageBus.subscribeBroadcast(ExampleBroadcast.class, anotherService);
        assertTrue(messageBus.getBroadcastSubscribers().get(ExampleBroadcast.class).contains(anotherService));
        assertEquals(2, messageBus.getBroadcastSubscribers().get(ExampleBroadcast.class).size());

        // Test subscribing to a broadcast without being registered
        MicroService unregisteredService = new TimeService(1, 1);
        assertThrows(IllegalStateException.class, () -> messageBus.subscribeBroadcast(ExampleBroadcast.class, unregisteredService));

//        // Test subscribing to a null broadcast
//        assertThrows(IllegalArgumentException.class, () -> messageBus.subscribeBroadcast(null, testMicroService));
//
//        // Test subscribing a null MicroService
//        assertThrows(IllegalArgumentException.class, () -> messageBus.subscribeBroadcast(ExampleBroadcast.class, null));

        // Test subscribing after unregistering
        messageBus.unregister(testMicroService);
        assertThrows(IllegalStateException.class, () -> messageBus.subscribeBroadcast(ExampleBroadcast.class, testMicroService));

        // Test concurrent subscription
        int threadCount = 10;
        CountDownLatch latch = new CountDownLatch(threadCount);
        for (int i = 0; i < threadCount; i++) {
            new Thread(() -> {
                MicroService service = new TimeService(1, 1);
                messageBus.register(service);
                messageBus.subscribeBroadcast(ExampleBroadcast.class, service);
                latch.countDown();
            }).start();
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            fail("Thread interruption occurred");
        }
        assertEquals(threadCount + 1, messageBus.getBroadcastSubscribers().get(ExampleBroadcast.class).size());
    }

    @Test
    void complete() {
        // Test completing a task
        // Create a mock event and result
        Event<String> mockEvent = new Event<String>() {
        };
        String mockResult = "Test Result";

        // Create a future and associate it with the event
        Future<String> future = new Future<>();
        messageBus.getEventAndFutureUnresolved().put(mockEvent, future);

        // Call the complete method
        messageBus.complete(mockEvent, mockResult);

        // Assert that the future is resolved with the correct result
        assertTrue(future.isDone());
        assertEquals(mockResult, future.get());

        // Assert that the event is removed from the unresolved map
        assertFalse(messageBus.getEventAndFutureUnresolved().containsKey(mockEvent));
        
    }

    @Test
    void sendBroadcast() {
    }

    @Test
    void sendEvent() {
    }


    @Test
    void register() {
        MessageBusImpl messageBus = MessageBusImpl.getIstance();

        // Test registering a single MicroService
        messageBus.register(testMicroService);
        assertNotNull(messageBus.getMicroServicesQueues().get(testMicroService));
        assertEquals(1, messageBus.getMicroServicesQueues().size());

        // Test registering multiple MicroServices
        MicroService anotherService = new TimeService(2, 2);
        messageBus.register(anotherService);
        assertNotNull(messageBus.getMicroServicesQueues().get(anotherService));
        assertEquals(2, messageBus.getMicroServicesQueues().size());

        // Test registering the same MicroService twice
        messageBus.register(testMicroService);
        assertEquals(2, messageBus.getMicroServicesQueues().size());

//        // Test registering null MicroService
//        assertThrows(IllegalArgumentException.class, () -> messageBus.register(null));

        // Test registering after unregistering
        messageBus.unregister(testMicroService);
        messageBus.register(testMicroService);
        assertNotNull(messageBus.getMicroServicesQueues().get(testMicroService));
        assertEquals(2, messageBus.getMicroServicesQueues().size());

        // Test registering a large number of MicroServices
        for (int i = 0; i < 1000; i++) {
            MicroService service = new TimeService(1, 1);
            messageBus.register(service);
        }
        assertEquals(1002, messageBus.getMicroServicesQueues().size());


        // This test assumes that the MessageBusImpl is thread-safe
        int threadCount = 10;
        CountDownLatch latch = new CountDownLatch(threadCount);
        for (int i = 0; i < threadCount; i++) {
            new Thread(() -> {
                MicroService service = new TimeService(1, 1);
                messageBus.register(service);
                latch.countDown();
            }).start();
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            fail("Thread interruption occurred");
        }
        assertEquals(1012, messageBus.getMicroServicesQueues().size());
    }

    @Test
    void unregister() {
        // Test unregistering a MicroService that was not registered
        assertNull(messageBus.getMicroServicesQueues().get(testMicroService));
        messageBus.unregister(testMicroService);
        assertNull(messageBus.getMicroServicesQueues().get(testMicroService));

        // Test registering and then unregistering a MicroService
        messageBus.register(testMicroService);
        assertNotNull(messageBus.getMicroServicesQueues().get(testMicroService));
        messageBus.unregister(testMicroService);
        assertNull(messageBus.getMicroServicesQueues().get(testMicroService));

        // Test unregistering a MicroService that was subscribed to events
        ExampleEvent exampleEvent = new ExampleEvent("Test");
        messageBus.register(testMicroService);
        messageBus.subscribeEvent(ExampleEvent.class, testMicroService);
        messageBus.unregister(testMicroService);
        assertNull(messageBus.getMicroServicesQueues().get(testMicroService));
        assertFalse(messageBus.getEventSubscribers().get(ExampleEvent.class).contains(testMicroService));

        // Test unregistering a MicroService that was subscribed to broadcasts
        ExampleBroadcast exampleBroadcast = new ExampleBroadcast("Test");
        messageBus.register(testMicroService);
        messageBus.subscribeBroadcast(ExampleBroadcast.class, testMicroService);
        messageBus.unregister(testMicroService);
        assertNull(messageBus.getMicroServicesQueues().get(testMicroService));
        assertFalse(messageBus.getBroadcastSubscribers().get(ExampleBroadcast.class).contains(testMicroService));

        // Test unregistering a MicroService that has pending messages
        messageBus.register(testMicroService);
        messageBus.subscribeEvent(ExampleEvent.class, testMicroService);
        messageBus.sendEvent(exampleEvent);
        messageBus.unregister(testMicroService);
        assertNull(messageBus.getMicroServicesQueues().get(testMicroService));
        assertThrows(IllegalStateException.class, () -> messageBus.awaitMessage(testMicroService));

        // Test unregistering multiple MicroServices
        MicroService anotherService = new TimeService(2, 2);
        messageBus.register(testMicroService);
        messageBus.register(anotherService);
        messageBus.unregister(testMicroService);
        assertNull(messageBus.getMicroServicesQueues().get(testMicroService));
        assertNotNull(messageBus.getMicroServicesQueues().get(anotherService));
        messageBus.unregister(anotherService);
        assertNull(messageBus.getMicroServicesQueues().get(anotherService));

        // Test unregistering the same MicroService multiple times
        messageBus.register(testMicroService);
        messageBus.unregister(testMicroService);
        messageBus.unregister(testMicroService);
        assertNull(messageBus.getMicroServicesQueues().get(testMicroService));
    }

    @Test
    void awaitMessage() {
    }
}