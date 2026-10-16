package ua.kpi.comsys.dop;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class NotificationTest {

    @Test
    void notificationIsSealedWithThreeVariants() {
        assertTrue(Notification.class.isSealed());
        assertEquals(3, Notification.class.getPermittedSubclasses().length);
    }

    @Test
    void textMsgStoresFields() {
        Notification.TextMsg msg = new Notification.TextMsg("Alice", "Hello");
        assertEquals("Alice", msg.sender());
        assertEquals("Hello", msg.text());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t", "\n"})
    void textMsgRejectsBlankSender(String sender) {
        assertThrows(IllegalArgumentException.class, () -> new Notification.TextMsg(sender, "Hello"));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t", "\n"})
    void textMsgRejectsBlankText(String text) {
        assertThrows(IllegalArgumentException.class, () -> new Notification.TextMsg("Alice", text));
    }

    @Test
    void imageMsgStoresFields() {
        Notification.ImageMsg msg = new Notification.ImageMsg("Bob", "https://example.com/a.png", 120);
        assertEquals("Bob", msg.sender());
        assertEquals("https://example.com/a.png", msg.url());
        assertEquals(120, msg.sizeKb());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t"})
    void imageMsgRejectsBlankSender(String sender) {
        assertThrows(IllegalArgumentException.class,
                () -> new Notification.ImageMsg(sender, "https://example.com/a.png", 1));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t"})
    void imageMsgRejectsBlankUrl(String url) {
        assertThrows(IllegalArgumentException.class, () -> new Notification.ImageMsg("Bob", url, 1));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -500})
    void imageMsgRejectsNonPositiveSize(int sizeKb) {
        assertThrows(IllegalArgumentException.class,
                () -> new Notification.ImageMsg("Bob", "https://example.com/a.png", sizeKb));
    }

    @Test
    void audioMsgStoresFields() {
        Notification.AudioMsg msg = new Notification.AudioMsg("Carol", 42);
        assertEquals("Carol", msg.sender());
        assertEquals(42, msg.durationSeconds());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t"})
    void audioMsgRejectsBlankSender(String sender) {
        assertThrows(IllegalArgumentException.class, () -> new Notification.AudioMsg(sender, 1));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -60})
    void audioMsgRejectsNonPositiveDuration(int durationSeconds) {
        assertThrows(IllegalArgumentException.class, () -> new Notification.AudioMsg("Carol", durationSeconds));
    }
}