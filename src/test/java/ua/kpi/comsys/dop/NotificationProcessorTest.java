package ua.kpi.comsys.dop;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NotificationProcessorTest {

    private final NotificationProcessor processor = new NotificationProcessor();

    @Test
    void nullNotificationThrowsIllegalArgumentException() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class, () -> processor.process(null));
        assertEquals("Notification cannot be null", ex.getMessage());
    }

    @Test
    void processesTextMsg() {
        String result = processor.process(new Notification.TextMsg("Alice", "Hello, world"));
        String expected = """
                Notification: TEXT
                From: Alice
                Message: Hello, world
                """;
        assertEquals(expected, result);
    }

    @Test
    void processesImageMsg() {
        String result = processor.process(new Notification.ImageMsg("Bob", "https://example.com/a.png", 120));
        String expected = """
                Notification: IMAGE
                From: Bob
                URL: https://example.com/a.png (120 KB)
                """;
        assertEquals(expected, result);
    }

    @Test
    void processesAudioMsg() {
        String result = processor.process(new Notification.AudioMsg("Carol", 42));
        String expected = """
                Notification: AUDIO
                From: Carol
                Duration: 42 seconds
                """;
        assertEquals(expected, result);
    }
}