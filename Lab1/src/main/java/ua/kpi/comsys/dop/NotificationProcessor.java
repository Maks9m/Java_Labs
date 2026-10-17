package ua.kpi.comsys.dop;

public class NotificationProcessor {

    public String process(Notification notification) {
        if (notification == null) {
            throw new IllegalArgumentException("Notification cannot be null");
        }
        return switch (notification) {
            case Notification.TextMsg msg -> """
                    Notification: TEXT
                    From: %s
                    Message: %s
                    """.formatted(msg.sender(), msg.text());
            case Notification.ImageMsg msg -> """
                    Notification: IMAGE
                    From: %s
                    URL: %s (%d KB)
                    """.formatted(msg.sender(), msg.url(), msg.sizeKb());
            case Notification.AudioMsg msg -> """
                    Notification: AUDIO
                    From: %s
                    Duration: %d seconds
                    """.formatted(msg.sender(), msg.durationSeconds());
        };
    }
}
