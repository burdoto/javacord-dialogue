package de.kaleidox.javacord.dialogue.model;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.jetbrains.annotations.Contract;

public interface SelfTimeoutable<Self extends SelfTimeoutable> {
    @Contract(value = "_, _ -> this", mutates = "this")
    Self withTimeout(long time, TimeUnit unit);

    Optional<Duration> getTimeout();

    @SuppressWarnings("ConstantConditions")
    @Contract(value = "-> this", mutates = "this")
    default Self removeTimeout() {
        return withTimeout(0, null);
    }
}
