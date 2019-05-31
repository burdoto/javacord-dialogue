package de.kaleidox.javacord.dialogue.model;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public interface SelfTimeoutable<Self extends SelfTimeoutable> {
    Self withTimeout(long time, TimeUnit unit);

    Optional<Duration> getTimeout();

    @SuppressWarnings({"ConstantConditions", "unchecked"})
    default Self removeTimeout() {
        withTimeout(0, null);
        return (Self) this;
    }
}
