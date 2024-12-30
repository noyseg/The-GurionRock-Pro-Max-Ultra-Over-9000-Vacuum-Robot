package bgu.spl.mics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MessageBusTest {
    private MessageBusImpl messageBus;
    private MicroService testMicroService;

    @BeforeEach
    void setUp() {
        messageBus = MessageBusImpl.getIstance(); // Create a fresh instance
        testMicroService = new MicroService("TestService") { // Create a test MicroService
            @Override
            protected void initialize() {
            }
        };
    }

    @Test
    void subscribeEvent() {
    }

    @Test
    void subscribeBroadcast() {
    }

    @Test
    void complete() {
    }

    @Test
    void sendBroadcast() {
    }

    @Test
    void sendEvent() {
    }

    @Test
    void register() {
    }

    @Test
    void unregister() {
        messageBus.register(testMicroService); // Register the MicroService
        // Verify that the MicroService's queue is created
        assertNotNull(messageBus.getMicroServicesQueues().get(testMicroService),
                "MicroService's queue should be created after registration.");
    }

    @Test
    void awaitMessage() {
    }
}