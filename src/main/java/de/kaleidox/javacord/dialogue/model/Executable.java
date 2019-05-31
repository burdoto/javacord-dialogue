package de.kaleidox.javacord.dialogue.model;

import java.util.concurrent.CompletableFuture;

public interface Executable<R> {
    CompletableFuture<R> executeAsync();

    default R execute() {
        return executeAsync().join();
    }
}
