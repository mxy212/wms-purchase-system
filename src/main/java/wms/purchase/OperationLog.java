package wms.purchase;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class OperationLog {
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final LocalDateTime time;
    private final String operation;
    private final String fromState;
    private final String toState;

    public OperationLog(String operation, String fromState, String toState) {
        this.time = LocalDateTime.now();
        this.operation = operation;
        this.fromState = fromState;
        this.toState = toState;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public String getOperation() {
        return operation;
    }

    public String getFromState() {
        return fromState;
    }

    public String getToState() {
        return toState;
    }

    @Override
    public String toString() {
        return time.format(FORMATTER) + " | " + operation + " | "
                + fromState + " -> " + toState;
    }
}
