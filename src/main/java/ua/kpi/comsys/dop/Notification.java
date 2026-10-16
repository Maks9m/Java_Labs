package ua.kpi.comsys.dop;

public sealed interface Notification permits Notification.TextMsg, Notification.ImageMsg, Notification.AudioMsg {

    record TextMsg(String sender, String text) implements Notification {
        public TextMsg {
            requireNotBlank(sender, "sender");
            requireNotBlank(text, "text");
        }
    }

    record ImageMsg(String sender, String url, int sizeKb) implements Notification {
        public ImageMsg {
            requireNotBlank(sender, "sender");
            requireNotBlank(url, "url");
            requirePositive(sizeKb, "sizeKb");
        }
    }

    record AudioMsg(String sender, int durationSeconds) implements Notification {
        public AudioMsg {
            requireNotBlank(sender, "sender");
            requirePositive(durationSeconds, "durationSeconds");
        }
    }

    private static void requireNotBlank(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " cannot be null or blank");
        }
    }

    private static void requirePositive(int value, String field) {
        if (value <= 0) {
            throw new IllegalArgumentException(field + " must be positive");
        }
    }
}
